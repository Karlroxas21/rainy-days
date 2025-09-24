package com.rainydaysengine.rainydays.domain.service.entry;

import com.rainydaysengine.rainydays.domain.port.entry.IEntryService;
import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.infra.minio.Minio;
import com.rainydaysengine.rainydays.infra.postgres.entity.EntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.EntryRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.GroupRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserEntriesRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import com.rainydaysengine.rainydays.utils.CallResult;
import com.rainydaysengine.rainydays.utils.CallWrapper;
import com.rainydaysengine.rainydays.utils.RenameFile;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
public class Entry implements IEntryService {
    private static final Logger logger = LoggerFactory.getLogger(Entry.class);

    private final EntryRepository entryRepository;
    private final UserEntriesRepository userEntriesRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final Minio minio;

    public Entry(EntryRepository entryRepository,
                 UserEntriesRepository userEntriesRepository,
                 UserRepository userRepository,
                 GroupRepository groupRepository, Minio minio) {
        this.entryRepository = entryRepository;
        this.userEntriesRepository = userEntriesRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.minio = minio;
    }

    /**
     * @param depositEntryDto
     * @return EntryId
     */
    @Transactional
    @Override
    public String addEntry(DepositEntryDto depositEntryDto) {

        // Verify if UserId Exists
        Optional<UsersEntity> user = this.userRepository.findById(depositEntryDto.getUserId());
        if(user.isEmpty()) {
            logger.error("Entry#addEntry(): this.userRepository.findById() no user found: {}", depositEntryDto.getUserId());
            throw ApplicationError.NotFound(depositEntryDto.getUserId());
        }

        String fullName = user.get().getFirstName().toLowerCase() + "_" + user.get().getLastName().toLowerCase();

        // Verify if Group Exists
        CallResult<Optional<GroupEntity>> existingGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(depositEntryDto.getGroupId()));
        if(existingGroup.isFailure()) {
            logger.error("Entry#addEntry(): this.groupRepository.findById() failed", existingGroup.getError());
            throw ApplicationError.InternalError(existingGroup.getError());
        }

        if(existingGroup.getResult().isEmpty()) {
            logger.info("Entry#addEntry(): this.groupRepository.findById() can't find group", depositEntryDto.getGroupId());
            throw ApplicationError.Conflict(depositEntryDto.getGroupId() + " non-existent group");
        }

        // Add entry first
        EntriesEntity entriesEntity = new EntriesEntity();
        entriesEntity.setUserId(depositEntryDto.getUserId());
        entriesEntity.setAmount(depositEntryDto.getAmount());
        entriesEntity.setNotes(depositEntryDto.getNote());

        // Upload to Minio
        CallResult<String> photoEvidence = CallWrapper.syncCall(() -> this.uploadFile(depositEntryDto.getPhoto(), fullName));
        if(photoEvidence.isFailure()){
            logger.error("Entry#addEntry(): this.uploadFile() failed", photoEvidence.getError());
            throw ApplicationError.InternalError(photoEvidence.getError());
        }

        entriesEntity.setPhotoEvidence(photoEvidence.getResult());

        CallResult<EntriesEntity> depositoryAmount = CallWrapper.syncCall(() -> this.entryRepository.save(entriesEntity));
        if(depositoryAmount.isFailure()){
            logger.error("Entry#addEntry(): this.entryRepository.save() failed", depositoryAmount.getError());
            throw ApplicationError.InternalError(depositoryAmount.getError());
        }

        UUID entryId = depositoryAmount.getResult().getId();

        // Add UserEntries
        UserEntriesEntity entry = new UserEntriesEntity();
        entry.setEntryId(entryId);
        entry.setUserId(depositEntryDto.getUserId());
        entry.setGroupId(depositEntryDto.getGroupId());

        CallResult<UserEntriesEntity> userEntries = CallWrapper.syncCall(() -> this.userEntriesRepository.save(entry));
        if(depositoryAmount.isFailure()){
            logger.error("Entry#addEntry(): this.userEntriesRepository.save() failed", userEntries.getError());
            throw ApplicationError.InternalError(userEntries.getError());
        }

        return userEntries.getResult().getId().toString();
    }

    private String uploadFile(MultipartFile file, String user) throws Exception {
        String renamedFile = RenameFile.rename(file, "karl");
        String objectName = "app/entries/" + renamedFile;
        String contentType = file.getContentType();

        this.minio.uploadFile(objectName, file, contentType);
        return objectName;
    }

}

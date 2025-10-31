package com.rainydaysengine.rainydays.application.service.entry;

import com.rainydaysengine.rainydays.application.port.entry.IEntryPort;
import com.rainydaysengine.rainydays.application.port.entry.IEntryService;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupProgress;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.MemberRanking;
import com.rainydaysengine.rainydays.errors.ApplicationError;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class Entry implements IEntryService {
    private static final Logger logger = LoggerFactory.getLogger(Entry.class);

    private final EntryRepository entryRepository;
    private final UserEntriesRepository userEntriesRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final IEntryPort minio;

    /**
     * @param depositEntryDto
     * @return EntryId
     */
    @Transactional // Spring handles the transactional.
    @Override
    public String addEntry(DepositEntryDto depositEntryDto) {

        // Verify if UserId Exists
        CallResult<Optional<UsersEntity>> user = CallWrapper.syncCall(() -> this.userRepository.findById(depositEntryDto.getUserId()));
        if (user.isFailure()) {
            logger.error("Entry#addEntry(): this.userRepository.findById() failed", user.getError());
            throw ApplicationError.InternalError(user.getError());
        }
        if (user.getResult().isEmpty()) {
            logger.error("Entry#addEntry(): this.userRepository.findById() no user found: {}", depositEntryDto.getUserId());
            throw ApplicationError.NotFound(depositEntryDto.getUserId());
        }

        String fullName = user.getResult().get().getFirstName().toLowerCase() + "_" + user.getResult().get().getLastName().toLowerCase();

        // Verify if Group Exists
        CallResult<Optional<GroupEntity>> existingGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(depositEntryDto.getGroupId()));
        if (existingGroup.isFailure()) {
            logger.error("Entry#addEntry(): this.groupRepository.findById() failed", existingGroup.getError());
            throw ApplicationError.InternalError(existingGroup.getError());
        }

        if (existingGroup.getResult().isEmpty()) {
            logger.info("Entry#addEntry(): this.groupRepository.findById() can't find group", depositEntryDto.getGroupId());
            throw ApplicationError.Conflict(depositEntryDto.getGroupId() + " non-existent group");
        }

        // Add entry
        EntriesEntity entriesEntity = new EntriesEntity();
        entriesEntity.setUserId(depositEntryDto.getUserId());
        entriesEntity.setAmount(depositEntryDto.getAmount());
        entriesEntity.setNotes(depositEntryDto.getNote());

        // Upload to Minio
        CallResult<String> photoEvidence = CallWrapper.syncCall(() -> this.uploadFile(depositEntryDto.getPhoto(), fullName));
        if (photoEvidence.isFailure()) {
            logger.error("Entry#addEntry(): this.uploadFile() failed", photoEvidence.getError());
            throw ApplicationError.InternalError(photoEvidence.getError());
        }

        entriesEntity.setPhotoEvidence(photoEvidence.getResult());

        CallResult<EntriesEntity> depositoryAmount = CallWrapper.syncCall(() -> this.entryRepository.save(entriesEntity));
        if (depositoryAmount.isFailure()) {
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
        if (depositoryAmount.isFailure()) {
            logger.error("Entry#addEntry(): this.userEntriesRepository.save() failed", userEntries.getError());
            throw ApplicationError.InternalError(userEntries.getError());
        }

        return userEntries.getResult().getId().toString();
    }

    /**
     * @param userId
     * @return RecentEntriesResponse
     */
    @Override
    public Page<RecentEntriesResponse> recentEntriesByUserId(String userId, String search, Pageable pageable) {
        CallResult<Page<RecentEntriesResponse>> userEntries =
                CallWrapper.syncCall(() -> this.userEntriesRepository.findAllRecentEntriesByUserId(UUID.fromString(userId), search, pageable));
        if (userEntries.isFailure()) {
            logger.error("Entry#recentEntries(): this.userEntriesRepository.findAllRecentEntriesByUserId() failed",
                    userEntries.getError());
            throw ApplicationError.InternalError(userEntries.getError());
        }

        return userEntries.getResult();
    }

    /**
     * @param entryId
     * @Param userId
     * @return EntryResponse
     */
    @Override
    public EntryResponse findEntry(String entryId, String userId) {
        CallResult<Optional<EntryResponse>> entry =
                CallWrapper.syncCall(() -> this.userEntriesRepository.findEntryById(UUID.fromString(entryId), UUID.fromString(userId)));
        if (entry.isFailure()) {
            logger.error("Entry#recentEntries(): this.userEntriesRepository.findEntryById() failed", entry.getError());
            throw ApplicationError.InternalError(entry.getError());
        }

        return entry.getResult().get();
    }

    /**
     * @param userId
     * @param groupId
     * @return TotalAmountContributedByUserResponse
     */
    @Override
    public TotalAmountContributedByUserResponse findTotalAmountContributedByUser(String userId, String groupId) {
        // Check if userId is present
        CallResult<Optional<UsersEntity>> isValidUser = CallWrapper.syncCall(() -> this.userRepository.findById(UUID.fromString(userId)));
        if (isValidUser.getResult().isEmpty()) {
            logger.error("Entry#findTotalAmountContributedByUser(): this.userRepository.findById() no user found", userId);
            throw ApplicationError.NotFound(userId);
        }
        if (isValidUser.isFailure()) {
            logger.error("Entry#findTotalAmountContributedByUser(): this.userRepository.findById() failed", isValidUser.getError());
            throw ApplicationError.InternalError(isValidUser.getError());
        }

        // Check if group is valid
        CallResult<Optional<GroupEntity>> isValidGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(UUID.fromString(groupId)));
        if (isValidGroup.getResult().isEmpty()) {
            logger.error("Entry#findTotalAmountContributedByUser(): this.groupRepository.findById() no entry found", groupId);
            throw ApplicationError.NotFound(groupId);
        }
        if (isValidGroup.isFailure()) {
            logger.error("Entry#findTotalAmountContributedByUser(): this.groupRepository.findById() failed", isValidGroup.getError());
            throw ApplicationError.InternalError(isValidGroup.getError());
        }

        // findTotalAmountContributedByUser
        CallResult<Optional<TotalAmountContributedByUserResponse>> totalContribution =
                CallWrapper.syncCall(() -> this.userEntriesRepository.findTotalAmountContributedByUser(UUID.fromString(userId), UUID.fromString(groupId)));
        if (totalContribution.isFailure()) {
            logger.error("Entry#findTotalAmountContributedByUser(): this.userEntriesRepository.findTotalAmountContributedByUser() failed", totalContribution.getError());
            throw ApplicationError.InternalError(totalContribution.getError());
        }
        if (totalContribution.getResult().isEmpty()) {
            logger.info("No contribution found for user {} in group {}", userId, groupId);
            throw ApplicationError.NotFound("No contribution found for user" + userId + " in group " + groupId);
        }
        return totalContribution.getResult().get();
    }

    /**
     * @param groupId
     * @return GroupStatisticResponse
     */
    @Override
    public GroupStatisticResponse getGroupStatistics(UUID groupId) {
        // Check if group is valid
        CallResult<Optional<GroupEntity>> isValidGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(groupId));
        if (isValidGroup.getResult().isEmpty()) {
            logger.error("Entry#getGroupStatistics(): this.groupRepository.findById() no entry found", groupId);
            throw ApplicationError.NotFound(groupId);
        }
        if (isValidGroup.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.groupRepository.findById() failed", isValidGroup.getError());
            throw ApplicationError.InternalError(isValidGroup.getError());
        }

        // get Group Progress
        CallResult<Optional<GroupProgress>> groupProgress =
                CallWrapper.syncCall(() -> this.userEntriesRepository.getGroupProgress(groupId));
        if (groupProgress.getResult().isEmpty()) {
            logger.info("No group progress found in {} group\", groupId");
        }
        if (groupProgress.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.userEntriesRepository.getGroupProgress()", groupProgress.getError());
            throw ApplicationError.InternalError(groupProgress.getError());
        }

        // Get Group Progress in Current Month
        CallResult<Optional<GroupProgress>> getGroupTotalInCurrentMonth =
                CallWrapper.syncCall(() -> this.userEntriesRepository.getGroupTotalInCurrentMonth(groupId));
        if (getGroupTotalInCurrentMonth.getResult().isEmpty()) {
            logger.info("No group progress in current month found in {} group\", groupId");
        }
        if (getGroupTotalInCurrentMonth.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.userEntriesRepository.getGroupTotalInCurrentMonth()", getGroupTotalInCurrentMonth.getError());
            throw ApplicationError.InternalError(getGroupTotalInCurrentMonth.getError());
        }

        // Get Member Ranking
        CallResult<List<MemberRanking>> getMemberRankingInCurrentMonth =
                CallWrapper.syncCall(() -> this.userEntriesRepository.getMemberRankingInCurrentMonth(groupId));
        if (getMemberRankingInCurrentMonth.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.userEntriesRepository.getMemberRankingInCurrentMonth()", getMemberRankingInCurrentMonth.getError());
            throw ApplicationError.InternalError(getMemberRankingInCurrentMonth.getError());
        }

        GroupStatisticResponse res = new GroupStatisticResponse(
                groupProgress.getResult().get(),
                getGroupTotalInCurrentMonth.getResult().get(),
                getMemberRankingInCurrentMonth.getResult()
        );

        return res;
    }

    private String uploadFile(MultipartFile file, String user) throws Exception {
        String renamedFile = RenameFile.rename(file, "karl");
        String objectName = "app/entries/" + renamedFile;
        String contentType = file.getContentType();

        this.minio.uploadFile(objectName, file, contentType);
        return objectName;
    }

}

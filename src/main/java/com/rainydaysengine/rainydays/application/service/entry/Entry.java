package com.rainydaysengine.rainydays.application.service.entry;

import com.rainydaysengine.rainydays.application.port.entry.IEntryPort;
import com.rainydaysengine.rainydays.application.port.entry.IEntryService;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupProgress;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.MemberRanking;
import com.rainydaysengine.rainydays.application.service.entry.history.AllRecentEntriesInGroup;
import com.rainydaysengine.rainydays.application.service.entry.history.EntriesSummaryHistory;
import com.rainydaysengine.rainydays.application.service.entry.history.GroupCompleteHistory;
import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntryType;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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

    @Value("${minio.bucket}")
    private String bucket;

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

        // Add entry
        EntriesEntity entriesEntity = new EntriesEntity();
        entriesEntity.setUserId(depositEntryDto.getUserId());
        entriesEntity.setEntryType(EntryType.valueOf(depositEntryDto.getEntryType().toString()));
        entriesEntity.setAmount(depositEntryDto.getAmount());
        entriesEntity.setNotes(depositEntryDto.getNote());

        // Upload to Minio
        CallResult<String> photoEvidence = CallWrapper.syncCall(() -> this.uploadFile(depositEntryDto.getPhoto(), fullName));
        if (photoEvidence.isFailure()) {
            logger.error("Entry#addEntry(): this.uploadFile() failed", photoEvidence.getError());
            throw ApplicationError.InternalError(photoEvidence.getError());
        }

        entriesEntity.setPhotoEvidence(photoEvidence.getResult());

        CallResult<EntriesEntity> amount = CallWrapper.syncCall(() -> this.entryRepository.save(entriesEntity));
        if (amount.isFailure()) {
            logger.error("Entry#addEntry(): this.entryRepository.save() failed", amount.getError());
            throw ApplicationError.InternalError(amount.getError());
        }
        System.out.println("Entries Entity: " + amount.getResult().getEntryType());

        UUID entryId = amount.getResult().getId();

        // Add UserEntries
        UserEntriesEntity entry = new UserEntriesEntity();
        entry.setEntryId(entryId);
        entry.setUserId(depositEntryDto.getUserId());
        entry.setGroupId(depositEntryDto.getGroupId());

        CallResult<UserEntriesEntity> userEntries = CallWrapper.syncCall(() -> this.userEntriesRepository.save(entry));
        if (amount.isFailure()) {
            logger.error("Entry#addEntry(): this.userEntriesRepository.save() failed", userEntries.getError());
            throw ApplicationError.InternalError(userEntries.getError());
        }

        // Register cleanup if transaction rolls back
        // Hook into the transaction lifecycle using Spring transaction sync callbacks.
        // To test, delete all record first in entries and user_entries DB table then run this query:
        // ALTER TABLE entries
        // ALTER COLUMN entry_type TYPE varchar
        // it should throw 'could not execute statement [ERROR: column \"entry_type\" is of type integer but expression is of type...'
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        removeObject(photoEvidence.getResult());
                    } catch (Exception e) {
                        logger.error("Failed to clean up MinIO file after rollback", e);
                    }
                }
            }
        });

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
    public GroupStatisticResponse getGroupStatistics(String groupId) {
        // Check if group is valid
        CallResult<Optional<GroupEntity>> isValidGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(UUID.fromString(groupId)));
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
                CallWrapper.syncCall(() -> this.userEntriesRepository.getGroupProgress(UUID.fromString(groupId)));
        if (groupProgress.getResult().isEmpty()) {
            logger.info("No group progress found in {} group\", groupId");
        }
        if (groupProgress.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.userEntriesRepository.getGroupProgress()", groupProgress.getError());
            throw ApplicationError.InternalError(groupProgress.getError());
        }

        // Get Group Progress in Current Month
        CallResult<Optional<GroupProgress>> getGroupTotalInCurrentMonth =
                CallWrapper.syncCall(() -> this.userEntriesRepository.getGroupTotalInCurrentMonth(UUID.fromString(groupId)));
        if (getGroupTotalInCurrentMonth.getResult().isEmpty()) {
            logger.info("No group progress in current month found in {} group\", groupId");
        }
        if (getGroupTotalInCurrentMonth.isFailure()) {
            logger.error("Entry#getGroupStatistics(): this.userEntriesRepository.getGroupTotalInCurrentMonth()", getGroupTotalInCurrentMonth.getError());
            throw ApplicationError.InternalError(getGroupTotalInCurrentMonth.getError());
        }

        // Get Member Ranking
        CallResult<List<MemberRanking>> getMemberRankingInCurrentMonth =
                CallWrapper.syncCall(() -> this.userEntriesRepository.getMemberRankingInCurrentMonth(UUID.fromString(groupId)));
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

    /**
     * @param groupId, month (0-12), year(YYYY), pageable
     * @return
     */
    @Override
    public GroupCompleteHistory getCompleteGroupHistory(String groupId, Integer month, Integer year, Pageable pageable) {
        System.out.println("Entry Service: " + groupId);
        // Check if groupId is existing group
        CallResult<Optional<GroupEntity>> isValidGroup = CallWrapper.syncCall(() -> this.groupRepository.findById(UUID.fromString(groupId)));
        if (isValidGroup.getResult().isEmpty()) {
            logger.error("Entry#getCompleteGroupHistory(): this.groupRepository.findById() no entry found", groupId);
            throw ApplicationError.NotFound(groupId);
        }
        if (isValidGroup.isFailure()) {
            logger.error("Entry#getCompleteGroupHistory(): this.groupRepository.findById() failed", isValidGroup.getError());
            throw ApplicationError.InternalError(isValidGroup.getError());
        }

        // Get deposits history in group
        CallResult<Optional<EntriesSummaryHistory>> depositSummary = CallWrapper.syncCall(
                () -> this.userEntriesRepository.getEntriesSummaryHistory(UUID.fromString(groupId), EntryType.DEPOSIT.toString()));
        if (depositSummary.isFailure()) {
            logger.error("Entry#getCompleteGroupHistory(): this.userEntriesRepository.getEntriesSummaryHistory() failed", depositSummary.getError());
            throw ApplicationError.InternalError(depositSummary.getError());
        }

        // Get withdraws history in group
        CallResult<Optional<EntriesSummaryHistory>> withdrawSummary = CallWrapper.syncCall(
                () -> this.userEntriesRepository.getEntriesSummaryHistory(UUID.fromString(groupId), EntryType.WITHDRAW.toString()));
        if (withdrawSummary.isFailure()) {
            logger.error("Entry#getCompleteGroupHistory(): this.userEntriesRepository.getEntriesSummaryHistory() failed", withdrawSummary.getError());
            throw ApplicationError.InternalError(withdrawSummary.getError());
        }

        // Calculate get deposits - get withdraws
        BigDecimal totalDeposits = depositSummary.getResult().get().total();
        BigDecimal totalWithdraws = withdrawSummary.getResult().get().total();

        BigDecimal netChange = totalDeposits.subtract(totalWithdraws);

        // Get All entries in group
        CallResult<Page<AllRecentEntriesInGroup>> allRecentEntriesInGroup = CallWrapper.syncCall(
                () -> this.userEntriesRepository.getAllEntriesInGroup(
                        UUID.fromString(groupId),
                        month,
                        year,
                        pageable));
        if (allRecentEntriesInGroup.isFailure()) {
            logger.error("Entry#getCompleteGroupHistory(): this.userEntriesRepository.getAllEntriesInGroup() failed", allRecentEntriesInGroup.getError());
            throw ApplicationError.InternalError(allRecentEntriesInGroup.getError());
        }

        GroupCompleteHistory groupCompleteHistory = new GroupCompleteHistory(
                depositSummary.getResult().get(),
                withdrawSummary.getResult().get(),
                netChange,
                allRecentEntriesInGroup.getResult()
        );

        return groupCompleteHistory;
    }

    private String uploadFile(MultipartFile file, String user) throws Exception {
        String renamedFile = RenameFile.rename(file, "karl");
        String objectName = "app/entries/" + renamedFile;
        String contentType = file.getContentType();

        this.minio.putObject(objectName, file, contentType);
        return objectName;
    }

    private void removeObject(String objectName) throws Exception{
        this.minio.removeObject(bucket, objectName);
    }
}

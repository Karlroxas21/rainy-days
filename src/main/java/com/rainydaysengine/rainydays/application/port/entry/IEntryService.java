package com.rainydaysengine.rainydays.application.port.entry;

import com.rainydaysengine.rainydays.application.service.entry.*;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import com.rainydaysengine.rainydays.application.service.entry.history.GroupCompleteHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEntryService {
    String addEntry(DepositEntryDto depositEntryDto);

    Page<RecentEntriesResponse> recentEntriesByUserId(String userId, String search, Pageable pageable);

    EntryResponse findEntry(String entryId, String userId);

    TotalAmountContributedByUserResponse findTotalAmountContributedByUser(String userId, String groupId);

    GroupStatisticResponse getGroupStatistics(String groupId);

    GroupCompleteHistory getCompleteGroupHistory(
            String groupId,
            Integer month,
            Integer year,
            Pageable pageable);

    TotalPersonalFundByUserResponse totalPersonalFundByUserResponse(String userId);
}

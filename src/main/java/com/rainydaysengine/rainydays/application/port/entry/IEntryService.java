package com.rainydaysengine.rainydays.application.port.entry;

import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;
import com.rainydaysengine.rainydays.application.service.entry.EntryResponse;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import com.rainydaysengine.rainydays.application.service.entry.TotalAmountContributedByUserResponse;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IEntryService {
    String addEntry(DepositEntryDto depositEntryDto);
    Page<RecentEntriesResponse> recentEntriesByUserId(String userId, String search, Pageable pageable);
    EntryResponse findEntry(String entryId, String userId);
    TotalAmountContributedByUserResponse findTotalAmountContributedByUser(String userId, String groupId);
    GroupStatisticResponse getGroupStatistics(UUID groupId);
}

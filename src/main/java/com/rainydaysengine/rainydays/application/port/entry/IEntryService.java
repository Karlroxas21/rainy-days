package com.rainydaysengine.rainydays.application.port.entry;

import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;
import com.rainydaysengine.rainydays.application.service.entry.RecentEntriesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IEntryService {
    String addEntry(DepositEntryDto depositEntryDto);
    Page<RecentEntriesResponse> recentEntries(String userId, String search, Pageable pageable);

}

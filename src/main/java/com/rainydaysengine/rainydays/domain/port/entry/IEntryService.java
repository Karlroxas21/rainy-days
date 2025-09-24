package com.rainydaysengine.rainydays.domain.port.entry;

import com.rainydaysengine.rainydays.domain.service.entry.DepositEntryDto;

public interface IEntryService {
    String addEntry(DepositEntryDto depositEntryDto);
}

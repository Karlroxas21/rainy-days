package com.rainydaysengine.rainydays.application.port.entry;

import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;

public interface IEntryService {
    String addEntry(DepositEntryDto depositEntryDto);
}

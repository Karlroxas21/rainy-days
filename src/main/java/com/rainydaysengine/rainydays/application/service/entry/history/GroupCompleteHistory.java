package com.rainydaysengine.rainydays.application.service.entry.history;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public record GroupCompleteHistory(
        EntriesSummaryHistory deposits,
        EntriesSummaryHistory withdraws,
        BigDecimal netChange,
        Page<AllRecentEntriesInGroup> history
) {
}

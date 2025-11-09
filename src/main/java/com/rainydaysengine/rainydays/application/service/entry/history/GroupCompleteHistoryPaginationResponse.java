package com.rainydaysengine.rainydays.application.service.entry.history;

import com.rainydaysengine.rainydays.application.service.common.PaginationResponse;

import java.math.BigDecimal;

public record GroupCompleteHistoryPaginationResponse(
        EntriesSummaryHistory deposits,
        EntriesSummaryHistory withdraws,
        BigDecimal netChange,
        PaginationResponse<AllRecentEntriesInGroup> history
) {
}

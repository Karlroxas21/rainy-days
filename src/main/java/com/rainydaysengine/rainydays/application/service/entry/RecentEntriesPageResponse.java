package com.rainydaysengine.rainydays.application.service.entry;

import lombok.Builder;

import java.util.List;

@Builder
public record RecentEntriesPageResponse(
        List<RecentEntriesResponse> data,
        int currentMonthTotal,
        long monthsActive,
        int currentPage,
        long totalItems,
        int totalPages,
        int pageSize,
        String sort
) {}


package com.rainydaysengine.rainydays.application.service.pagination;

import java.util.List;

public record PaginationResponse<T>(
        List<T> data,
        int currentPage,
        long totalItems,
        int totalPages,
        int PageSize,
        String sort
) {
}

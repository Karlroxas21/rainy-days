package com.rainydaysengine.rainydays.application.service.entry.history;

import java.math.BigDecimal;

public record EntriesSummaryHistory(
        BigDecimal total,
        String entryType
) {
}
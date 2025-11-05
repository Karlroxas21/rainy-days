package com.rainydaysengine.rainydays.application.service.entry.groupstatistics;

import java.math.BigDecimal;

public record GroupProgress(
    String groupName,
    Long combinedGoal,
    BigDecimal total
) {
}

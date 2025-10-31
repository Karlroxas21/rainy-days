package com.rainydaysengine.rainydays.application.service.entry.groupstatistics;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberRanking(
        UUID userId,
        BigDecimal totalContribution
) {
}

package com.rainydaysengine.rainydays.application.service.entry.groupstatistics;

import java.util.List;

public record GroupStatisticResponse(
        GroupProgress groupProgress,
        GroupProgress groupProgressThisMonth,
        List<MemberRanking> memberRanking
) {
}

package com.rainydaysengine.rainydays.application.service.entry;

import lombok.Builder;

@Builder
public record TotalAmountContributedByUserResponse(
        String groupName,
        int combinedGoal,
        String firstName,
        String middleName,
        String lastName,
        String suffix,
        String profileUrl,
        int total
) {
}

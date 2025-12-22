package com.rainydaysengine.rainydays.application.service.usersgroup;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserGroupsResponse(
        UUID id,
        String groupName,
        int amount
) {
}

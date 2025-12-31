package com.rainydaysengine.rainydays.application.service.usersgroup;

import java.util.UUID;

public record UsersGroupWithTotalMembers(
        UUID id,
        String groupName,
        int amount,
        int totalMembers
) {
}

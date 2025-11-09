package com.rainydaysengine.rainydays.application.port.group;

import com.rainydaysengine.rainydays.application.service.group.GroupDto;

import java.util.UUID;

public interface IGroupService {
    UUID createNewGroup(GroupDto groupDto);

    void addUserToGroup(UUID userId, UUID groupId);
}

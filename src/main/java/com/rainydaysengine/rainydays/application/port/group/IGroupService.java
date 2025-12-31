package com.rainydaysengine.rainydays.application.port.group;

import com.rainydaysengine.rainydays.application.service.group.GroupDto;
import com.rainydaysengine.rainydays.application.service.usersgroup.UserGroupsResponse;
import com.rainydaysengine.rainydays.application.service.usersgroup.UsersGroupWithTotalMembers;

import java.util.List;
import java.util.UUID;

public interface IGroupService {
    UUID createNewGroup(GroupDto groupDto, String jwt);

    void addUserToGroup(UUID userId, UUID groupId);

    List<UsersGroupWithTotalMembers> getUserGroups(String jwt);
}

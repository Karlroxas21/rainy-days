package com.rainydaysengine.rainydays.domain.port.group;

import com.rainydaysengine.rainydays.domain.service.group.GroupDto;
import org.hibernate.validator.constraints.UUID;

public interface IGroupService {
    UUID createNewGroup(GroupDto groupDto);
}

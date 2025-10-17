package com.rainydaysengine.rainydays.application.service.group;

import com.rainydaysengine.rainydays.application.port.group.IGroupService;
import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.GroupRepository;
import com.rainydaysengine.rainydays.utils.CallResult;
import com.rainydaysengine.rainydays.utils.CallWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class Group implements IGroupService {
    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    private final GroupRepository groupRepository;

    /**
     * @param groupDto
     * @return UUID of new group
     */
    @Override
    public UUID createNewGroup(GroupDto groupDto) {
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupName(groupDto.getGroupName());
        groupEntity.setCombinedGoal(groupDto.getCombinedGoal());

        CallResult<Optional<String>> existingGroupName = CallWrapper.syncCall(() -> this.groupRepository.findByGroupName(groupDto.getGroupName()));
        if (existingGroupName.isFailure()) {
            logger.error("Group#createNewGroup(): this.groupRepository.findByGroupName() failed", existingGroupName.getError());
            throw ApplicationError.InternalError(existingGroupName.getError());
        }

        if (existingGroupName.getResult().isPresent()) {
            logger.info("Group#createNewGroup(): this.groupRepository.findByGroupName() groupName already exists", groupDto.getGroupName());
            throw ApplicationError.Conflict(groupDto.getGroupName() + " already exists");
        }

        CallResult<GroupEntity> savedGroupEntity = CallWrapper.syncCall(() -> this.groupRepository.save(groupEntity));
        if (savedGroupEntity.isFailure()) {
            logger.error("Group#createNewGroup(): this.groupRepository.save() failed", savedGroupEntity.getError());
            throw ApplicationError.InternalError(savedGroupEntity.getError());
        }
        return savedGroupEntity.getResult().getId();
    }
}

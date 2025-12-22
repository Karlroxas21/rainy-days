package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.application.service.group.Group;
import com.rainydaysengine.rainydays.application.service.group.GroupDto;
import com.rainydaysengine.rainydays.infra.postgres.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTests {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private Group groupService;

    private GroupDto groupDto;

    @BeforeEach
    public void init() {
        this.groupDto = GroupDto
                .builder()
                .groupName("Test Group")
                .combinedGoal(10000)
                .build();
    }

//    @Test
//    public void GroupService_CreateNewGroup_ReturnUUID() {
//
//        when(this.groupRepository.findByGroupName(this.groupDto.getGroupName()))
//                .thenReturn(Optional.empty());
//
//        GroupEntity savedGroupEntity = GroupEntity
//                .builder()
//                .id(UUID.randomUUID())
//                .groupName("Test")
//                .combinedGoal(10000)
//                .build();
//        when(this.groupRepository.save(Mockito.any(GroupEntity.class)))
//                .thenReturn(savedGroupEntity);
//
//        UUID result = this.groupService.createNewGroup(this.groupDto);
//
//        Assertions.assertThat(result).isNotNull();
//    }
}

package com.rainydaysengine.rainydays.infra.postgres;

import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.GroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class GroupRepositoryTests {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void groupRepository_Save_ReturnSavedGroup() {

        // Arrange
        GroupEntity groupEntity = GroupEntity.builder()
                .groupName("Test Group")
                .combinedGoal(10000)
                .build();

        // Act
        GroupEntity savedgroupEntity = groupRepository.save(groupEntity);

        // Assert
        Assertions.assertThat(savedgroupEntity).isNotNull();
        Assertions.assertThat(savedgroupEntity.getId());
    }

    @Test
    public void groupRepository_FindByGroupName_ReturnGroup() {
        UUID randomUuid = UUID.randomUUID();
        String groupName = "Test Group";

        GroupEntity groupEntity = GroupEntity.builder()
                .groupName(groupName)
                .combinedGoal(10000)
                .build();
        GroupEntity savedgroupEntity = groupRepository.save(groupEntity);

        Optional<String> group = groupRepository.findByGroupName(groupName);

        Assertions.assertThat(group).isNotNull();
    }
}

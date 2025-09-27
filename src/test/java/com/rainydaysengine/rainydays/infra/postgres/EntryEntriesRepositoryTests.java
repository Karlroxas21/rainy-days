package com.rainydaysengine.rainydays.infra.postgres;


import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserEntriesRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class EntryEntriesRepositoryTests {

    @Autowired
    private UserEntriesRepository userEntriesRepository;

    @Test
    public void userEntriesRepository_Save_ReturnSavedUserEntries() {
        UUID randomUuid = UUID.randomUUID();

        // Arrange
        UserEntriesEntity userEntries = UserEntriesEntity.builder()
                .entryId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .groupId(UUID.randomUUID())
                .build();

        // Act
        UserEntriesEntity savedUserEntriesEntity = userEntriesRepository.save(userEntries);

        // Assert
        Assertions.assertThat(savedUserEntriesEntity).isNotNull();
        Assertions.assertThat(savedUserEntriesEntity.getId());
    }

}

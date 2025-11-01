package com.rainydaysengine.rainydays.infra.postgres;

import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.EntryRepository;
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
public class EntryRepositoryTests {

    @Autowired
    private EntryRepository entryRepository;

    @Test
    public void userRepository_Save_ReturnSavedEntry() {
        EntriesEntity entriesEntity = EntriesEntity.builder()
                .userId(UUID.randomUUID())
                .amount(10000)
                .notes("Test note")
                .photoEvidence("/file/path/test.png")
                .build();

        EntriesEntity savedEntriesEntity = this.entryRepository.save(entriesEntity);

        Assertions.assertThat(savedEntriesEntity).isNotNull();
        Assertions.assertThat(savedEntriesEntity.getId());
    }
}

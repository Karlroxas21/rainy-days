package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.application.port.entry.IEntryPort;
import com.rainydaysengine.rainydays.application.service.entry.DepositEntryDto;
import com.rainydaysengine.rainydays.application.service.entry.Entry;
import com.rainydaysengine.rainydays.infra.postgres.entity.GroupEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UserEntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntriesEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.EntryRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.GroupRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserEntriesRepository;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import com.rainydaysengine.rainydays.utils.RenameFile;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntryServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private UserEntriesRepository userEntriesRepository;

    @Mock
    private IEntryPort iEntryPort;

    @InjectMocks
    private Entry entryService;

    private DepositEntryDto depositEntryDto;

    private MockMultipartFile mockFile;

    @BeforeEach
    public void init() {

        this.mockFile = new MockMultipartFile(
                "photo",
                "test-image.jpg",
                "image/jpeg",
                "dummy-image-content".getBytes()
        );

        this.depositEntryDto = DepositEntryDto
                .builder()
                .userId(UUID.randomUUID())
                .amount(10000)
                .note("test note")
                .photo(mockFile)
                .groupId(UUID.randomUUID())
                .build();
    }

    @SneakyThrows
    @Test
    public void EntryService_AddEntry_ReturnsString() {

        // In real code, JPA would auto-generate the 'id' on save() but in test, we need to
        // simulate that.
        UsersEntity mockUserEntity = UsersEntity
                .builder()
                .id(UUID.randomUUID())
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test")
                .firstName("First")
                .lastName("Last")
                .build();
        when(this.userRepository.findById(depositEntryDto.getUserId()))
                .thenReturn(Optional.of(mockUserEntity));
        UUID userId = mockUserEntity.getId();

        GroupEntity mockGroupEntity = GroupEntity
                .builder()
                .id(UUID.randomUUID())
                .groupName("TestGroup")
                .combinedGoal(10000)
                .build();
        when(this.groupRepository.findById(depositEntryDto.getGroupId()))
                .thenReturn(Optional.ofNullable(mockGroupEntity));
        UUID groupId = mockGroupEntity.getId();

        String renamedFile = RenameFile.rename(this.mockFile, "karl");
        String objectName = "app/entries/" + renamedFile;
        String contentType = this.mockFile.getContentType();


        doNothing().when(this.iEntryPort).putObject(objectName, this.mockFile, contentType);

        EntriesEntity entriesEntity = EntriesEntity
                .builder()
                .id(UUID.randomUUID())
                .userId(mockUserEntity.getId())
                .amount(20000)
                .photoEvidence("/file/path/test.png")
                .notes("Test Note")
                .build();
        when(this.entryRepository.save(Mockito.any(EntriesEntity.class)))
                .thenReturn(entriesEntity);

        UUID entryId = entriesEntity.getId();

        UserEntriesEntity userEntriesEntity = UserEntriesEntity
                .builder()
                .id(UUID.randomUUID())
                .entryId(entryId)
                .userId(userId)
                .groupId(groupId)
                .build();
        when(this.userEntriesRepository.save(Mockito.any(UserEntriesEntity.class)))
                .thenReturn(userEntriesEntity);

        String result = this.entryService.addEntry(this.depositEntryDto);

        Assertions.assertThat(result).isNotNull();
    }
}

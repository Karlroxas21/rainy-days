package com.rainydaysengine.rainydays.infra.minio;

import com.rainydaysengine.rainydays.application.port.entry.IEntryPort;
import com.rainydaysengine.rainydays.utils.RenameFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class MinioTests {

    @Mock
    private IEntryPort iEntryPort;

    private MockMultipartFile mockFile;

    @BeforeEach
    public void init() {
        this.mockFile = new MockMultipartFile(
                "photo",
                "test-image.jpg",
                "image/jpeg",
                "dummy-image-content".getBytes()
        );
    }

    @Test
    public void EntryService_AddEntry_ReturnsString(){
        String renamedFile = RenameFile.rename(this.mockFile, "karl");
        String objectName = "app/entries/" + renamedFile;
        String contentType = this.mockFile.getContentType();

        assertAll(() -> this.iEntryPort.uploadFile(objectName, this.mockFile, contentType));
    }
}

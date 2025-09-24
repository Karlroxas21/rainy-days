package com.rainydaysengine.rainydays.domain.service.entry;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
public class DepositEntryDto {

    @NotNull(message = "UserID cannot be empty")
    private UUID userId;

    @NotNull(message = "Amount cannot be empty")
    private int amount;

    private String note;

    @NotNull(message = "Photo cannot be empty")
    private MultipartFile photo;

    @NotNull(message = "Group ID cannot be empty")
    private UUID groupId;

}

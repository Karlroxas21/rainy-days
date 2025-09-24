package com.rainydaysengine.rainydays.domain.service.entry;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserEntriesDto {

    @NotNull(message = "EntryId cannot be empty")
    private UUID entry_id;

    @NotNull(message = "UserID cannot be empty")
    private UUID user_id;

    @NotNull(message = "GroupID cannot be empty")
    private UUID group_id;
}

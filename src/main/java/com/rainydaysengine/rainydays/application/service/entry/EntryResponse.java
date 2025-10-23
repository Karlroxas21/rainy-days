package com.rainydaysengine.rainydays.application.service.entry;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record EntryResponse(
        UUID entryId,
        int amount,
        String notes,
        String photoEvidence,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        UUID groupId,
        String groupName
) {
}

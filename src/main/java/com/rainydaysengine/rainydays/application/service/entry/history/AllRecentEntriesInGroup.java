package com.rainydaysengine.rainydays.application.service.entry.history;

import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntryType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AllRecentEntriesInGroup(
        UUID userId,
        UUID groupId,
        int amount,
        EntryType entryType,
        String notes,
        String photoEvidence,
        OffsetDateTime createdAt
) {
}

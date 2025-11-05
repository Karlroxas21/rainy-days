package com.rainydaysengine.rainydays.application.service.entry;

import com.rainydaysengine.rainydays.infra.postgres.entity.entries.EntryType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RecentEntriesResponse(
        UUID entryId,
        EntryType entryType,
        int entryAmount,
        String entryPhoto,
        String entryNotes,
        UUID groupId,
        String groupName
) {
}

package com.rainydaysengine.rainydays.application.service.entry;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RecentEntriesResponse(
        UUID entryId,
        int entryAmount,
        String entryPhoto,
        String entryNotes,
        UUID groupId,
        String groupName
) {
}

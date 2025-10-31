package com.rainydaysengine.rainydays.interfaces.web.group;

import com.rainydaysengine.rainydays.application.service.entry.Entry;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import com.rainydaysengine.rainydays.application.service.group.Group;
import com.rainydaysengine.rainydays.application.service.group.GroupDto;
import com.rainydaysengine.rainydays.interfaces.web.user.UserController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/group")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Group group;
    private final Entry entry;

    @PostMapping("/create-group")
    public ResponseEntity<UUID> createGroup(@RequestBody @Valid GroupDto groupDto) {
        UUID newGroup = this.group.createNewGroup(groupDto);

        return ResponseEntity.ok(newGroup);
    }

    @PostMapping("/{groupId}/user/{userId}")
    public ResponseEntity<Void> inviteUserToGroup(@PathVariable String groupId, @PathVariable String userId) {
        this.group.addUserToGroup(UUID.fromString(userId), UUID.fromString(groupId));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/group-stat")
    public ResponseEntity<GroupStatisticResponse> getGroupStatistics(@PathVariable String groupId) {
        GroupStatisticResponse res = this.entry.getGroupStatistics(UUID.fromString(groupId));

        return ResponseEntity.ok(res);
    }
}

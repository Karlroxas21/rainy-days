package com.rainydaysengine.rainydays.interfaces.web.group;

import com.rainydaysengine.rainydays.application.service.common.PaginationResponse;
import com.rainydaysengine.rainydays.application.service.entry.Entry;
import com.rainydaysengine.rainydays.application.service.entry.groupstatistics.GroupStatisticResponse;
import com.rainydaysengine.rainydays.application.service.entry.history.AllRecentEntriesInGroup;
import com.rainydaysengine.rainydays.application.service.entry.history.GroupCompleteHistory;
import com.rainydaysengine.rainydays.application.service.entry.history.GroupCompleteHistoryPaginationResponse;
import com.rainydaysengine.rainydays.application.service.group.Group;
import com.rainydaysengine.rainydays.application.service.group.GroupDto;
import com.rainydaysengine.rainydays.application.service.usersgroup.UserGroupsResponse;
import com.rainydaysengine.rainydays.interfaces.web.user.UserController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.rainydaysengine.rainydays.interfaces.web.user.UserController.DEFAULT_PAGE_SIZE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/group")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Group group;
    private final Entry entry;

    @PostMapping("/create-group")
    public ResponseEntity<UUID> createGroup(@RequestBody @Valid GroupDto groupDto, @RequestHeader("Authorization") String bearerToken) {
        String jwt = "";

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            jwt = bearerToken.substring(7); // Remove "Bearer " prefix.
        }

        UUID newGroup = this.group.createNewGroup(groupDto, jwt);

        return ResponseEntity.ok(newGroup);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserGroupsResponse>> getUserGroups(@RequestHeader("Authorization") String bearerToken) {
        String jwt = "";

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            jwt = bearerToken.substring(7); // Remove "Bearer " prefix.
        }

        List<UserGroupsResponse> userGroups = this.group.getUserGroups(jwt);

        return ResponseEntity.ok(userGroups);
    }

    @PostMapping("/{groupId}/user/{userId}")
    public ResponseEntity<Void> inviteUserToGroup(@PathVariable String groupId, @PathVariable String userId) {
        this.group.addUserToGroup(UUID.fromString(userId), UUID.fromString(groupId));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/group-stat")
    public ResponseEntity<GroupStatisticResponse> getGroupStatistics(@PathVariable String groupId) {
        GroupStatisticResponse res = this.entry.getGroupStatistics(String.valueOf(groupId));

        return ResponseEntity.ok(res);
    }

    /**
     * @param groupId
     * @param month    (0-12)
     * @param year     (YYYY)
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/complete-history")
    public ResponseEntity<GroupCompleteHistoryPaginationResponse> getGroupCompleteHistory(
            @PathVariable String groupId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @PageableDefault(page = 0, size = DEFAULT_PAGE_SIZE, direction = Sort.Direction.DESC)
            Pageable pageable) {

        GroupCompleteHistory groupCompleteHistory = this.entry.getCompleteGroupHistory(groupId, month, year, pageable);

        PaginationResponse<AllRecentEntriesInGroup> historyPaged = new PaginationResponse<>(
                groupCompleteHistory.history().getContent(),
                groupCompleteHistory.history().getNumber(),
                groupCompleteHistory.history().getTotalElements(),
                groupCompleteHistory.history().getTotalPages(),
                groupCompleteHistory.history().getSize(),
                pageable.getSort().toString()
        );

        GroupCompleteHistoryPaginationResponse res = new GroupCompleteHistoryPaginationResponse(
                groupCompleteHistory.deposits(),
                groupCompleteHistory.withdraws(),
                groupCompleteHistory.netChange(),
                historyPaged
        );

        return ResponseEntity.ok(res);
    }
}

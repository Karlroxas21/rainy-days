package com.rainydaysengine.rainydays.interfaces.web.group;

import com.rainydaysengine.rainydays.application.service.group.Group;
import com.rainydaysengine.rainydays.application.service.group.GroupDto;
import com.rainydaysengine.rainydays.interfaces.web.user.UserController;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/group")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Group group;

    public GroupController(Group group){
        this.group = group;
    }

    @PostMapping("/create-group")
    public ResponseEntity<UUID> createGroup(@RequestBody @Valid GroupDto groupDto) {
        UUID newGroup = this.group.createNewGroup(groupDto);

        return ResponseEntity.ok(newGroup);
    }
}

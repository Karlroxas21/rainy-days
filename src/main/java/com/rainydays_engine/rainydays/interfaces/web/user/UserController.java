package com.rainydays_engine.rainydays.interfaces.web.user;

import com.rainydays_engine.rainydays.application.service.user.User;
import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.application.service.user.UserResponse;
import com.rainydays_engine.rainydays.utils.CallWrapper;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final User user;

    public UserController(User user) {
        this.user = user;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<UserResponse>> registerUser(
            @Valid @RequestBody UserRequestDto requestDto) {

        return user.userRegister(requestDto)
                    .thenApply(userResponse -> ResponseEntity.ok(userResponse));

    }

    @PostMapping("/login")
     public CompletableFuture<ResponseEntity<UserResponse>> login(
            @Valid @RequestBody UserRequestDto requestDto) {

        return user.userRegister(requestDto)
                    .thenApply(userResponse -> ResponseEntity.ok(userResponse));

    }
    
}

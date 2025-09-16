package com.rainydays_engine.rainydays.interfaces.web.user;

import com.rainydays_engine.rainydays.domain.port.auth.Session;

import com.rainydays_engine.rainydays.domain.service.user.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final User user;

    public UserController(User user) {
        this.user = user;
    }

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<UserRegisterResponse>> registerUser(
            @Valid @RequestBody UserRequestDto requestDto) {

        return user.userRegister(requestDto)
                    .thenApply(userRegisterResponse -> ResponseEntity.status(201).body(userRegisterResponse));

    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest loginRequest) {
        UserLoginResponse response = user.userLogin(loginRequest.getIdentifier(), loginRequest.getPassword());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/whoami")
    public ResponseEntity<?> whoAmI(@RequestBody UserWhoAmIRequest userWhoAmIRequest) {
        Session session = user.whoAmI(userWhoAmIRequest.getSession_token());

        return ResponseEntity.ok(session);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid UserResetPasswordRequest userResetPasswordRequest) {

        String email = userResetPasswordRequest.getIdentity();
        String password = userResetPasswordRequest.getPassword();

        user.resetPassword(email, password);

        return ResponseEntity.noContent().build();
    }
    
}

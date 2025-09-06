package com.rainydays_engine.rainydays.application.service.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotBlank(message = "Identifier cannot be empty")
    private String identifier;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}

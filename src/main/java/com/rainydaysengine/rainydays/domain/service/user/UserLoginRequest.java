package com.rainydaysengine.rainydays.domain.service.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {

    @NotBlank(message = "Identifier cannot be empty")
    private String identifier;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}

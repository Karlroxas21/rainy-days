package com.rainydays_engine.rainydays.domain.service.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserWhoAmIRequest {

    @NotBlank(message = "Session token cannot be empty")
    private String session_token;
}

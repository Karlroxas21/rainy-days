package com.rainydaysengine.rainydays.domain.service.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserWhoAmIRequest {

    @NotBlank(message = "Session token cannot be empty")
    private String session_token;
}

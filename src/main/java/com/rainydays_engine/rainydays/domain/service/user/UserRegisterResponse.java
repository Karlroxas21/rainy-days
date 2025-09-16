package com.rainydays_engine.rainydays.domain.service.user;

import lombok.Builder;

import java.util.Optional;

@Builder
public record UserRegisterResponse(
        String iamId,
        String email,
        String username,
        String first_name,
        Optional<String> middle_name,
        String last_name
) {
}

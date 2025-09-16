package com.rainydays_engine.rainydays.domain.service.user;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record UserLoginResponse(
        Map<String, Object> traits,
        String token,
        OffsetDateTime expiry
        ){
}

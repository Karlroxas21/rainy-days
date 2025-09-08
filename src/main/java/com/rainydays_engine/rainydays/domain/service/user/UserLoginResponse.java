package com.rainydays_engine.rainydays.domain.service.user;

import java.time.OffsetDateTime;
import java.util.Map;

public record UserLoginResponse(
        Map<String, Object> traits,
        String token,
        OffsetDateTime expiry
        ){
}

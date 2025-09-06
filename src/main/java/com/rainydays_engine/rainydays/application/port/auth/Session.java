package com.rainydays_engine.rainydays.application.port.auth;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Session(
        String id,
        String token,
        OffsetDateTime expiresAt,
        List<sh.ory.kratos.model.SessionDevice> devices,
        Optional<AuthIdentity> identity,
        Map<String, Object> traits
){}


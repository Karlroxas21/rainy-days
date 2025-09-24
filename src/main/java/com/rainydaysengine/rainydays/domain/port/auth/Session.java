package com.rainydaysengine.rainydays.domain.port.auth;

import sh.ory.kratos.model.Identity;
import sh.ory.kratos.model.SessionDevice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Session(
        String id,
        String token,
        OffsetDateTime expiresAt,
        List<SessionDevice> devices,
        Optional<Identity> identity,
        Map<String, Object> traits
){ }


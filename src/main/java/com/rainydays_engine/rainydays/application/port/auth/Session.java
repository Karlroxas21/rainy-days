package com.rainydays_engine.rainydays.application.port.auth;

import java.util.Date;
import java.util.List;

public record Session(
        String id,
        String token,
        Date expiresAt,
        List<Device> devices,
        AuthIdentity identity
){}


package com.rainydays_engine.rainydays.application.port.auth;

import java.util.List;

public record AuthIdentity(
        String id,
        String schema_id,
        List<Traits> traits
) {}

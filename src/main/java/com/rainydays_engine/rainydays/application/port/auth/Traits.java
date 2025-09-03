package com.rainydays_engine.rainydays.application.port.auth;

import java.util.List;

public record Traits(
        String id,
        String email_address,
        String name,
        List<RoleTypes> role_tpyes
) {
}

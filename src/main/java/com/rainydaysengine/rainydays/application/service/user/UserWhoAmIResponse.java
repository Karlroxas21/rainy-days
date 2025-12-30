package com.rainydaysengine.rainydays.application.service.user;

import java.util.Optional;
import java.util.UUID;

public record UserWhoAmIResponse(
        UUID id,
        String email_address,
        String first_name,
        Optional<String> middle_name,
        String last_name,
        String profile_url
) {
}

package com.rainydaysengine.rainydays.application.service.user;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public record UserWhoAmIResponse(
        UUID id,
        String emailAddress,
        String firstName,
        Optional<String> middleName,
        String lastName,
        Optional<String> suffix,
        String profileUrl,
        int goal
) {
}

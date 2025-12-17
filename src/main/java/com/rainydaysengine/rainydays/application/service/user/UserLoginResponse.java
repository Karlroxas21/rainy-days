package com.rainydaysengine.rainydays.application.service.user;

import lombok.Builder;

@Builder
public record UserLoginResponse(
        String tokenSession
) {
}

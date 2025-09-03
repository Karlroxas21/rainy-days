package com.rainydays_engine.rainydays.application.port.user;

import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;

import java.util.concurrent.CompletableFuture;

public interface IUserPort {
    CompletableFuture<String> userRegister(UserRequestDto userRecord);
}


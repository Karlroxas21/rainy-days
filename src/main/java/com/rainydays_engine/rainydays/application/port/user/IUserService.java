package com.rainydays_engine.rainydays.application.port.user;

import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.application.service.user.UserResponse;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<UserResponse> userRegister(UserRequestDto userRequestDto);
}

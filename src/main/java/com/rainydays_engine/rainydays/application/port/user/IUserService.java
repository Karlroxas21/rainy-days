package com.rainydays_engine.rainydays.application.port.user;

import com.rainydays_engine.rainydays.application.service.user.UserLoginResponse;
import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.application.service.user.UserRegisterResponse;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<UserRegisterResponse> userRegister(UserRequestDto userRequestDto);
    UserLoginResponse userLogin(String identifier, String password);
}

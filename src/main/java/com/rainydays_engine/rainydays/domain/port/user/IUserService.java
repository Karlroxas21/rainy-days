package com.rainydays_engine.rainydays.domain.port.user;

import com.rainydays_engine.rainydays.domain.port.auth.Session;
import com.rainydays_engine.rainydays.domain.service.user.UserLoginResponse;
import com.rainydays_engine.rainydays.domain.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.domain.service.user.UserRegisterResponse;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<UserRegisterResponse> userRegister(UserRequestDto userRequestDto);
    UserLoginResponse userLogin(String identifier, String password);
    Session whoAmI(String token);
    void resetPassword(String id, String password);
}

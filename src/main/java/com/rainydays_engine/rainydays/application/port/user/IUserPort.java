package com.rainydays_engine.rainydays.application.port.user;

import com.rainydays_engine.rainydays.application.port.auth.Session;
import com.rainydays_engine.rainydays.application.service.user.UserLoginResponse;
import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;

import java.util.concurrent.CompletableFuture;



public interface IUserPort {
    CompletableFuture<String> userRegister(UserRequestDto userRecord);
    Session userLogin(String identifier, String password);
}


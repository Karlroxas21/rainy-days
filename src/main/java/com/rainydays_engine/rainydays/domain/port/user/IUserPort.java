package com.rainydays_engine.rainydays.domain.port.user;

import com.rainydays_engine.rainydays.domain.port.auth.Session;
import com.rainydays_engine.rainydays.domain.service.user.UserRequestDto;

import java.util.concurrent.CompletableFuture;



public interface IUserPort {
    CompletableFuture<String> userRegister(UserRequestDto userRecord);
    Session userLogin(String identifier, String password);
    Session getSessionFromToken(String sessionToken);
    void resetPassword(String identity, String password);
}


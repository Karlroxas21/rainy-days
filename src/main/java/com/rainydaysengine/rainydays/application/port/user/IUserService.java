package com.rainydaysengine.rainydays.application.port.user;

import com.rainydaysengine.rainydays.application.port.auth.Session;
import com.rainydaysengine.rainydays.application.service.user.UserLoginResponse;
import com.rainydaysengine.rainydays.application.service.user.UserRequestDto;
import com.rainydaysengine.rainydays.application.service.user.UserRegisterResponse;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<UserRegisterResponse> userRegister(UserRequestDto userRequestDto);

    UserLoginResponse userLogin(String identifier, String password);

    Session whoAmI(String token);

    void resetPassword(String id, String password);
}

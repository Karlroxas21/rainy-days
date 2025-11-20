package com.rainydaysengine.rainydays.application.port.user;

import com.rainydaysengine.rainydays.application.service.user.UserLoginRequest;
import com.rainydaysengine.rainydays.application.service.user.UserRegisterResponse;
import com.rainydaysengine.rainydays.application.service.user.UserRequestDto;

public interface IUserService {
    UserRegisterResponse userRegister(UserRequestDto userRequestDto);

    // Login
    String verify(UserLoginRequest loginRequest);

    void resetPassword(String id, String password);
}

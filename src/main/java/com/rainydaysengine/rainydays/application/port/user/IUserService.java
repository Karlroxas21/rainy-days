package com.rainydaysengine.rainydays.application.port.user;

import com.rainydaysengine.rainydays.application.service.user.UserRegisterResponse;
import com.rainydaysengine.rainydays.application.service.user.UserRequestDto;

public interface IUserService {
    UserRegisterResponse userRegister(UserRequestDto userRequestDto);

//    UserLoginResponse userLogin(String identifier, String password);
//
//    Session whoAmI(String token);
//
//    void resetPassword(String id, String password);
}

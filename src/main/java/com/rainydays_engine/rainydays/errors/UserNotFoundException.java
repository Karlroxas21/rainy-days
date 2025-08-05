package com.rainydays_engine.rainydays.errors;

public class UserNotFoundException extends ApiException{
    public UserNotFoundException(String userId) {
        super("User not found: " + userId, 404, "USER_NOT_FOUND");
    }
}



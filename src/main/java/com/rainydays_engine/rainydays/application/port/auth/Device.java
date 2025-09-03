package com.rainydays_engine.rainydays.application.port.auth;

public record Device(
        String id,
        String ip_address,
        String location,
        String user_agent
){}

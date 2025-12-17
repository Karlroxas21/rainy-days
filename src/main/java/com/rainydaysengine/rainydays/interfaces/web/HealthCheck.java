package com.rainydaysengine.rainydays.interfaces.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class HealthCheck {

    @GetMapping("/health")
    public String healthCheck() {
        return HttpStatus.OK.toString();
    }
}

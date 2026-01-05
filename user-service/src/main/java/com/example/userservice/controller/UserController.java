package com.example.userservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/")
public class UserController {

    private Environment env;

    @Autowired
    public UserController(Environment env) {
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on Local PORT %s Server PORT %s"
                , env.getProperty("local.server.port"), env.getProperty("server.port"));
    }

    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request) {
        log.info("User welcom IP: {}, {}, {}, {}",
                request.getRemoteAddr(), request.getRemoteHost(),
                request.getRequestURI(), request.getRequestURL());

        return env.getProperty("greeting.message");
    }
}

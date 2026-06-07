package com.signature.signatureapp.controller;


import com.signature.signatureapp.dto.AuthResponse;
import com.signature.signatureapp.dto.LoginRequest;
import com.signature.signatureapp.dto.RegisterRequest;
import com.signature.signatureapp.model.User;
import com.signature.signatureapp.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        return userService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request){

        String token =
                userService.login(request);

        return new AuthResponse(token);
    }
}
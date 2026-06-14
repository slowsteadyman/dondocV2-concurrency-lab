package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.auth.LoginRequest;
import com.dondoc.dto.auth.LoginResponse;
import com.dondoc.dto.auth.SignUpRequest;
import com.dondoc.dto.auth.SignUpResponse;
import com.dondoc.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request){
        return ApiResponse.ok(authService.loginUser(request), "로그인 성공");
    }

    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signup(@RequestBody SignUpRequest request){
        return ApiResponse.ok(new SignUpResponse(authService.createUser(request)), "회원가입 성공");
    }
}

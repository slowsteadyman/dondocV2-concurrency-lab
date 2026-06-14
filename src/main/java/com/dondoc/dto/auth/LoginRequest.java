package com.dondoc.dto.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String userId;
    private String userPassword;
}

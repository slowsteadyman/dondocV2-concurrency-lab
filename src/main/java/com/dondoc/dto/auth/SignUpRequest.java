package com.dondoc.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {
    private String userId;
    private String userPassword;
    private String name;
}

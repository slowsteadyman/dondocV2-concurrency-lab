package com.dondoc.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String name;
    private Integer age;
    private Long monthlyIncome;
    private Integer targetExpenseRatio;
    private Integer currentPigLevel;
    private Integer currentHouseLevel;
    private Integer currentCharacterLevel;
}

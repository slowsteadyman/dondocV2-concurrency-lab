package com.dondoc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String userId;
    private String userPassword;
    private String name;
    private Integer age;
    private Integer currentPigLevel;
    private Integer currentHouseLevel;
    private Integer currentCharacterLevel;
    private Long monthlyIncome;
    private Integer targetExpenseRatio;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}

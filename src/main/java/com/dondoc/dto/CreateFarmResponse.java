package com.dondoc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CreateFarmResponse {
    private Long farmId;
    private String farmName;
    private boolean joined;
    private LocalDateTime createdAt;
}

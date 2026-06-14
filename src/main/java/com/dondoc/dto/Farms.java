package com.dondoc.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farms {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FarmGetResponse {
        private Long farmId;
        private String farmName;
        private Integer memberCount;
        private Boolean joined;
        private LocalDateTime createdAt;
    }
}

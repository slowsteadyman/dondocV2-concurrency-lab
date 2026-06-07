package com.dondoc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FarmMembers {
    private Long id;
    private Long userId;
    private Long farmId;
    private LocalDateTime joinedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FarmJoinResponse {
        private Long userId;
        private Long farmId;
        private LocalDateTime joinedAt;
    }
}

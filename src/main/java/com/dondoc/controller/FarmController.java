package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.FarmMembers.FarmJoinResponse;
import com.dondoc.dto.Farms;
import com.dondoc.dto.Farms.FarmDetailResponse;
import com.dondoc.service.FarmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    // ── 농장 조회 ─────────────────────────────────────────────────────────────

    @GetMapping
    public ApiResponse<List<Farms.FarmGetResponse>> getFarmList(
            @RequestHeader("userId") Long userId) {
        return ApiResponse.ok(farmService.getFarmList(userId), "농장 목록 조회 성공");
    }

    @GetMapping("/{farmId}")
    public ApiResponse<FarmDetailResponse> getFarmDetail(
            @PathVariable Long farmId,
            @RequestHeader(value = "userId", required = false) Long userId) {
        return ApiResponse.ok(farmService.getFarmDetail(userId, farmId), "농장 상세 조회 성공");
    }

    // ── 농장 생성 ─────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<Farms.CreateResponse>> createFarm(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestBody Farms.CreateRequest request) {
        Farms.CreateResponse response = farmService.createFarm(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "농장 생성 성공"));
    }

    // ── 농장 가입 / 탈퇴 ──────────────────────────────────────────────────────

    @PostMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmJoinResponse>> addFarmMember(
            @RequestHeader("userId") long userId,
            @PathVariable long farmId) {
        FarmJoinResponse data = farmService.addFarmMember(userId, farmId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(data, "농장 가입 성공"));
    }

    @DeleteMapping("/{farmId}")
    public ResponseEntity<?> leaveFarm(
            @RequestHeader(value = "userId", required = false) Long userId,
            @PathVariable Long farmId) {
        return ResponseEntity.ok(farmService.leaveFarm(farmId, userId));
    }
}

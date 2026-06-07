package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.FarmMembers.FarmJoinResponse;
import com.dondoc.dto.Farms;
import com.dondoc.service.FarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService){
        this.farmService = farmService;
    }

    @GetMapping
    public List<Farms> getFarms() {
        return farmService.getFarms();
    }

    @GetMapping("/members")
    public List<FarmMembers> getFarmMembers() {
        return farmService.getFarmMembers();
    }

    @PostMapping
    public void createFarm(@RequestBody Farms farm){
        farmService.createFarm(farm);
    }

    @PostMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmJoinResponse>> addFarmMember(
            @RequestHeader("userId") long userId,
            @PathVariable long farmId) {
        try {
            FarmJoinResponse data = farmService.addFarmMember(userId, farmId);
            String message = "농장 가입 성공";
            return ResponseEntity.ok(ApiResponse.ok(data, message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("농장 가입 실패"));
        }
    }
}

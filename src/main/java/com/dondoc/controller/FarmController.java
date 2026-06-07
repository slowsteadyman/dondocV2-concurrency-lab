package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.Farms;
import com.dondoc.service.FarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farm")
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

    @PostMapping("/members")
    public void createFarmMember(@RequestBody FarmMembers farmMember){
        farmService.createFarmMember(farmMember);
    }

    @DeleteMapping("/{farmId}")
    public ResponseEntity<?> leaveFarm(
            @RequestHeader(value = "userId", required = false) Long userId,
            @PathVariable Long farmId) {
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "인증 토큰 없음"));
        }
        return ResponseEntity.ok(farmService.leaveFarm(farmId, userId));
    }


}

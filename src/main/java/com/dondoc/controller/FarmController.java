package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.Farms;
import com.dondoc.service.FarmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService){
        this.farmService = farmService;
    }

//    @GetMapping
//    public List<Farms> getFarms() {
//        return farmService.getFarms();
//    }

    @GetMapping("")
    public ApiResponse<List<Farms.FarmGetResponse>> getFarmList(@RequestHeader("userId") Long userId) {
        return ApiResponse.ok(farmService.getFarmList(userId), "농장 목록 조회 성공");
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


}

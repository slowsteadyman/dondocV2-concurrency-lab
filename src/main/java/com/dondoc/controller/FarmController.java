package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.CreateFarmRequest;
import com.dondoc.dto.CreateFarmResponse;
import com.dondoc.dto.FarmMembers;
import com.dondoc.dto.Farms;
import com.dondoc.service.FarmService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<CreateFarmResponse>> createFarm(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestBody CreateFarmRequest request
    ) {
        try {
            CreateFarmResponse response = farmService.createFarm(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, response, "농장 생성 성공"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }

    @PostMapping("/members")
    public void createFarmMember(@RequestBody FarmMembers farmMember){
        farmService.createFarmMember(farmMember);
    }


}

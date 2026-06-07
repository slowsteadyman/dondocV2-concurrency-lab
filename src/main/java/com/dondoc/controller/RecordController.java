package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.Categories;
import com.dondoc.dto.MonthlyHistories;
import com.dondoc.dto.Records;
import com.dondoc.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/record")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService){
        this.recordService = recordService;
    }

    @GetMapping
    public List<Records> getRecords() {
        return recordService.getRecords();
    }

    @GetMapping("/categories")
    public List<Categories> getCategories() {
        return recordService.getCategories();
    }

    @GetMapping("/monthly-history")
    public List<MonthlyHistories> getMonthlyHistory() {
        return recordService.getMonthlyHistories();
    }

    @PostMapping
    public void createRecord(@RequestBody Records record){
        recordService.createRecord(record);
    }

    @PostMapping("/categories")
    public void createCategory(@RequestBody Categories category){
        recordService.createCategory(category);
    }

    @PostMapping("/monthly-history")
    public void createMonthlyHistory(@RequestBody MonthlyHistories monthlyHistory){
        recordService.createMonthlyHistory(monthlyHistory);
    }

    @GetMapping
    public ResponseEntity<?> getMonthlyRecords(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestParam String yearMonth, @RequestParam(required = false) String type ){
        if (userId == null){
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "인증 토큰 없음"));
        }
        return ResponseEntity.ok(recordService.getMonthlyRecords(userId, yearMonth, type));
    }

}

package com.dondoc.controller;

import com.dondoc.dto.ApiResponse;
import com.dondoc.dto.Categories;
import com.dondoc.dto.MonthlyHistories;
import com.dondoc.dto.Records;
import com.dondoc.dto.Records.RecordUpdateRequest;
import com.dondoc.dto.Records.RecordUpdateResponse;
import com.dondoc.service.RecordService;
import com.dondoc.dto.Records.DailySummaryResponse;
import java.time.Year;
import java.time.YearMonth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService){
        this.recordService = recordService;
    }

    @GetMapping("/categories")
    public List<Categories.Category> getCategories() {
        return recordService.getCategories();
    }

    @GetMapping("/monthly-history")
    public List<MonthlyHistories> getMonthlyHistory() {
        return recordService.getMonthlyHistories();
    }

    @PostMapping
    public ApiResponse<Records.RecordSaveResponse> createRecord(@RequestHeader Long userId, @RequestBody Records.RecordSaveRequest saveRequest){
        return ApiResponse.ok(recordService.createRecord(userId, saveRequest),"거래 추가 성공");
    }

    @PostMapping("/categories")
    public void createCategory(@RequestBody Categories.Category category){
        recordService.createCategory(category);
    }

    @PostMapping("/monthly-history")
    public void createMonthlyHistory(@RequestBody MonthlyHistories monthlyHistory){
        recordService.createMonthlyHistory(monthlyHistory);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<ApiResponse<Records.DeleteResponse>> deleteRecord(
            @RequestHeader(value = "userId", required = false) Long userId,
            @PathVariable Long recordId
    ) {
        Records.DeleteResponse response = recordService.deleteRecord(userId, recordId);
        return ResponseEntity.ok(ApiResponse.ok(response, "거래 삭제 성공"));
    }
  
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RecordUpdateResponse>> updateRecord(
            @RequestHeader("userId") long userId,
            @PathVariable long id,
            @RequestBody RecordUpdateRequest dto) {
        RecordUpdateResponse data = recordService.updateRecord(userId, id, dto);
        String message = "거래 수정 성공";
        return ResponseEntity.ok(ApiResponse.ok(data, message));
    }
  
    @GetMapping("/summary/daily")
    public ResponseEntity<ApiResponse<List<DailySummaryResponse>>> getDailySummaries(
            @RequestHeader("userId") long userId,
            @RequestParam String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        List<DailySummaryResponse> data = recordService.getDailySummaries(userId, yearMonth);
        String message = "일별 통계 조회 성공";
        return ResponseEntity.ok(ApiResponse.ok(data, message));
    }
}

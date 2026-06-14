package com.dondoc.service;

import com.dondoc.dto.Categories;
import com.dondoc.dto.MonthlyHistories;
import com.dondoc.dto.Records;
import com.dondoc.dto.Records.RecordUpdateRequest;
import com.dondoc.dto.Records.RecordUpdateResponse;
import com.dondoc.dto.Records.DailySummaryResponse;
import com.dondoc.entity.Category;
import com.dondoc.entity.MonthlyHistory;
import com.dondoc.entity.Recorde;
import com.dondoc.exception.ApiException;
import com.dondoc.repository.CategoryRepository;
import com.dondoc.repository.MonthlyHistoryRepository;
import com.dondoc.repository.RecordRepository;
import com.dondoc.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordService {
    private final RecordRepository recordRepository;
    private final MonthlyHistoryRepository monthlyHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public RecordService(RecordRepository recordRepository, MonthlyHistoryRepository monthlyHistoryRepository, CategoryRepository categoryRepository, UserRepository userRepository){
        this.recordRepository = recordRepository;
        this.monthlyHistoryRepository = monthlyHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Records.Record> getRecords(){
        List<Recorde> entities = recordRepository.findAll();
        return entities.stream()
                .map(entity -> new Records.Record(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getCategoryId(),
                        entity.getAmount(),
                        entity.getDescription(),
                        entity.getMemo(),
                        entity.getRecordDate(),
                        entity.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<MonthlyHistories> getMonthlyHistories(){
        List<MonthlyHistory> entities = monthlyHistoryRepository.findAll();
        return entities.stream()
                .map(entity -> new MonthlyHistories(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getTargetMonth(),
                        entity.getAvgRatio(),
                        entity.getHouseLevel()
                ))
                .collect(Collectors.toList());
    }

    public List<Categories.Category> getCategories(){
        List<Category> entities = categoryRepository.findAll();
        return entities.stream()
                .map(entity -> new Categories.Category(
                        entity.getId(),
                        entity.getName(),
                        entity.getIcon(),
                        entity.getType()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Records.RecordSaveResponse createRecord(Long userId, Records.RecordSaveRequest saveRequest) {
        // 정합성 검사
        userRepository.findById(userId).orElseThrow(()->new ApiException(HttpStatus.CONFLICT, "존재하지 않는 사용자"));

        Long savedId = recordRepository.save(userId, saveRequest);
        Recorde recorde = recordRepository.findById(savedId).orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "거래 추가 중 에러 발생"));
        Category category = categoryRepository.findById(recorde.getCategoryId()).orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "카테고리 조회 중 오류 발생"));

        return new Records.RecordSaveResponse(
                recorde.getId(),
                category.getType(),
                new Categories.CategoryDto(category.getId(), category.getName()),
                recorde.getRecordDate(),
                recorde.getAmount(),
                recorde.getDescription(),
                recorde.getMemo()
        );
    }

    public void createMonthlyHistory(MonthlyHistories dto) {
        MonthlyHistory monthlyHistory = new MonthlyHistory(
                null, dto.getUserId(), dto.getTargetMonth(),
                dto.getAvgRatio(), dto.getHouseLevel()
        );
        monthlyHistoryRepository.save(monthlyHistory);
    }

    public void createCategory(Categories.Category dto){
        Category category = new Category(
                null, dto.getName(), dto.getIcon(),
                dto.getType()
        );
        categoryRepository.save(category);
    }

    public Records.DeleteResponse deleteRecord(Long userId, Long recordId) {
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 토큰 없음");
        }

        Recorde record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "거래를 찾을 수 없음"));

        if (!record.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인 거래가 아님");
        }

        recordRepository.deleteById(recordId);
        return new Records.DeleteResponse(recordId);
    }

    public RecordUpdateResponse updateRecord(long userId, long id, RecordUpdateRequest dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        Recorde existing = recordRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 거래입니다."));

        if (existing.getUserId() != userId) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인의 거래만 수정할 수 있습니다.");
        }

        Recorde recorde = new Recorde(
            id,
            existing.getUserId(),
            dto.getCategoryId(),
            dto.getAmount(),
            dto.getDescription(),
            dto.getMemo(),
            LocalDate.parse(dto.getDate()),
            existing.getCreatedAt()
        );

        recordRepository.update(recorde);

        Recorde updated = recordRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "거래 수정 후 조회에 실패했습니다."));
        Category category = categoryRepository.findById(updated.getCategoryId());

        return new RecordUpdateResponse(
            updated.getId(),
            category.getType(),
            updated.getRecordDate().toString(),
            new Categories.CategoryInfo(
                category.getId(),
                category.getName()
            ),
            updated.getAmount(),
            updated.getDescription(),
            updated.getMemo()
        );
    }

    public List<DailySummaryResponse> getDailySummaries(long userId, YearMonth yearMonth) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Recorde> records = recordRepository.findByDateRange(userId, start, end);

        Map<Long, String> categoryTypeMap = categoryRepository.findAll()
            .stream()
            .collect(Collectors.toMap(Category::getId, Category::getType));

        Map<LocalDate, long[]> summaryByDate = new HashMap<>();

        for (Recorde record : records) {
            LocalDate date = record.getRecordDate();
            String type = categoryTypeMap.get(record.getCategoryId());

            summaryByDate.putIfAbsent(date, new long[]{0L, 0L});

            if ("INCOME".equals(type)) {
                summaryByDate.get(date)[0] += record.getAmount();
            } else if ("EXPENSE".equals(type)) {
                summaryByDate.get(date)[1] += record.getAmount();
            }
        }

        // 날짜 오름차순 정렬을 위해 TreeMap으로 변환
        Map<LocalDate, long[]> sorted = new TreeMap<>(summaryByDate);

        List<DailySummaryResponse> result = new ArrayList<>();
        for (Map.Entry<LocalDate, long[]> entry : sorted.entrySet()) {
            LocalDate date = entry.getKey();
            long income = entry.getValue()[0];
            long expense = entry.getValue()[1];
            result.add(new DailySummaryResponse(date, income, expense, 0));
        }

        return result;
    }
}

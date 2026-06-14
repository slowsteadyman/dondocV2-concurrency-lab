package com.dondoc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Records {
    private Long id;
    private Long userId;
    private Long categoryId;
    private Long amount;
    private String description;
    private String memo;
    private LocalDate recordDate;
    private LocalDateTime createdAt;
  
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        private Long id;
        private Long userId;
        private Long categoryId;
        private Long amount;
        private String description;
        private String memo;
        private LocalDate recordDate;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteResponse {
        private Long recordId;
    }

    @Getter
    @AllArgsConstructor
    public static class MonthlySettlementResponse {
        private final String month;
        private final Long totalIncome;
        private final Long totalExpense;
        private final Long netIncome;
        private final Long monthlyBudget;
        private final Integer budgetUsedPercent;
        private final Integer avgPigState;
        private final Integer currentHouseLevel;
        private final Integer nextHouseLevel;
        private final List<SettlementCategoryExpense> categoryExpenses;
    }

    @Getter
    @AllArgsConstructor
    public static class SettlementCategoryExpense {
        private final SettlementCategory category;
        private final Long amount;
        private final Integer ratio;
    }

    @Getter
    @AllArgsConstructor
    public static class SettlementCategory {
        private final Long id;
        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public static class MonthlySummaryResponse {
        private final String month;
        private final Long totalIncome;
        private final Long totalExpense;
        private final Long netIncome;
        private final Integer savingRate;
        private final Integer transactionCount;
        private final Long avgDailyExpense;
        private final Long monthlyBudget;
        private final Integer budgetUsedPercent;
        private final Long remainBudget;
        private final Long recommendDailyBudget;
        private final List<SummaryDetail> incomeDetail;
        private final List<SummaryDetail> expenseDetail;
    }

    @Getter
    @AllArgsConstructor
    public static class SummaryDetail {
        private final SummaryCategory category;
        private final Long amount;
        private final Integer ratio;
    }

    @Getter
    @AllArgsConstructor
    public static class SummaryCategory {
        private final Long id;
        private final String name;
    }
  
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordUpdateRequest {
        private String type;
        private long categoryId;
        private String date;
        private long amount;
    }
  
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordUpdateResponse {
        private long id;
        private String type;
        private String date;
        private Categories.CategoryInfo category;
        private long amount;
    }
  
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySummaryResponse {
        private LocalDate date;
        private long income;
        private long expense;
        private int pigLevel;
    }
    
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecordSaveRequest {
        private String type;
        private Long categoryId;
        private LocalDate date;
        private Long amount;
        private String description;
        private String memo;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecordSaveResponse {
        private Long id;
        private String type;
        private Categories.CategoryDto category;
        private LocalDate date;
        private Long amount;
        private String description;
        private String memo;
    }
}

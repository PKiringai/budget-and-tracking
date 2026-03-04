package coop.bank.budget_tracking.dto.request;

import coop.bank.budget_tracking.enums.PeriodType;
import coop.bank.budget_tracking.enums.PeriodUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Request DTO for creating a new budget Supports multiple budget items per budget

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCreateRequest {

    @NotBlank(message = "Customer ID is required")
    private String userId;

    private String messageId;

    private String channelId;

    private String functionCode;

    @NotNull(message = "Total amount is required")
    private BigDecimal budgetAmount;

    private String currency;

    private Integer period;

    private PeriodUnit periodUnit;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "At least one category is required")
    private List<BudgetItemRequest> categories;

//    @Builder.Default
//    private Boolean alertThreshold80 = true;
//
//    @Builder.Default
//    private Boolean alertThreshold100 = true;

//    @Builder.Default
//    private Boolean rolloverEnabled = false;

    //Nested DTO representing individual budget items

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetItemRequest {

        @NotBlank(message = "Category code is required")
        private String categoryCode;

        @NotBlank(message = "Category name is required")
        private String categoryName;

        @NotNull(message = "Budget amount is required")
        @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
        private BigDecimal budgetAmount;
    }
}
package coop.bank.budget_tracking.dto.request;


import coop.bank.budget_tracking.enums.PeriodType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new budget
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCreateRequest {
    @NotBlank(message = "Customer ID is required")
    private String cifId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    private BigDecimal budgetAmount;

    @NotNull(message = "Period type is required")
    private PeriodType periodType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate; // Auto-calculated if not provided

    @Builder.Default
    private Boolean alertThreshold80 = true;

    @Builder.Default
    private Boolean alertThreshold100 = true;

    @Builder.Default
    private Boolean rolloverEnabled = false;

}

package coop.bank.budget_tracking.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating an existing budget
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetUpdateRequest {

    @NotNull(message = "Budget ID is required")
    private Long id;

    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    private BigDecimal budgetAmount;

    private LocalDate endDate;

    private Boolean isActive;

    private Boolean alertThreshold80;

    private Boolean alertThreshold100;

    private Boolean rolloverEnabled;
}

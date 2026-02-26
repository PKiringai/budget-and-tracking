package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for budget summary with analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummaryResponse {

    private String cifId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalBudget;
    private BigDecimal totalSpending;
    private BigDecimal remainingBudget;
    private Integer budgetUtilizationPercentage;
    private List<CategoryBudgetSummary> categoryBreakdown;
    private List<String> insights;
}

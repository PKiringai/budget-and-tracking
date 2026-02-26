package coop.bank.budget_tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Category-level budget summary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBudgetSummary {

    private String category;
    private BigDecimal budgetAmount;
    private BigDecimal spending;
    private BigDecimal remaining;
    private Integer percentageUsed;
    private String status; // ON_TRACK, WARNING, EXCEEDED
}

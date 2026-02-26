package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for spending analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendingAnalyticsResponse {

    private String cifId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSpending;
    private BigDecimal averageDailySpending;
    private String topCategory;
    private BigDecimal topCategoryAmount;
    private List<CategorySpendingDTO> categoryBreakdown;
    private List<MonthlyTrendDTO> monthlyTrend;
    private List<String> topMerchants;
    private List<SpendingInsightDTO> insights;
}

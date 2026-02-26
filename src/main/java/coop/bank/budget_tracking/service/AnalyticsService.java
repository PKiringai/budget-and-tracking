package coop.bank.budget_tracking.service;


import coop.bank.budget_tracking.dto.response.CategorySpendingDTO;
import coop.bank.budget_tracking.dto.response.MonthlyTrendDTO;
import coop.bank.budget_tracking.dto.response.SpendingAnalyticsResponse;
import coop.bank.budget_tracking.dto.response.SpendingInsightDTO;
import coop.bank.budget_tracking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analytics Service - Spending analytics and insights
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    /**
     * Get comprehensive spending analytics
     */
    public SpendingAnalyticsResponse getSpendingAnalytics(
            String cifId, LocalDate startDate, LocalDate endDate) {

        log.info("Generating spending analytics for CIF: {} from {} to {}",
                cifId, startDate, endDate);

        OffsetDateTime startDateTime = startDate.atStartOfDay()
                .atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime endDateTime = endDate.atTime(23, 59, 59)
                .atOffset(OffsetDateTime.now().getOffset());

        // Calculate total spending
        BigDecimal totalSpending = transactionRepository
                .calculateTotalExpenses(cifId, startDateTime, endDateTime);

        // Calculate average daily spending
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal averageDailySpending = totalSpending
                .divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.HALF_UP);

        // Get category breakdown
        List<CategorySpendingDTO> categoryBreakdown = getCategoryBreakdown(
                cifId, startDateTime, endDateTime);

        // Find top category
        String topCategory = categoryBreakdown.isEmpty()
                ? null
                : categoryBreakdown.get(0).getCategory();
        BigDecimal topCategoryAmount = categoryBreakdown.isEmpty()
                ? BigDecimal.ZERO
                : categoryBreakdown.get(0).getTotalAmount();

        // Get monthly trend
        List<MonthlyTrendDTO> monthlyTrend = getMonthlyTrend(cifId, 6);

        // Get top merchants
        List<String> topMerchants = transactionRepository
                .getTopMerchantsBySpending(cifId, startDateTime, endDateTime, 5);

        // Generate insights
        List<SpendingInsightDTO> insights = generateSpendingInsights(
                totalSpending, categoryBreakdown, monthlyTrend);

        return SpendingAnalyticsResponse.builder()
                .cifId(cifId)
                .startDate(startDate)
                .endDate(endDate)
                .totalSpending(totalSpending)
                .averageDailySpending(averageDailySpending)
                .topCategory(topCategory)
                .topCategoryAmount(topCategoryAmount)
                .categoryBreakdown(categoryBreakdown)
                .monthlyTrend(monthlyTrend)
                .topMerchants(topMerchants)
                .insights(insights)
                .build();
    }

    /**
     * Get category breakdown
     */
    public List<CategorySpendingDTO> getCategoryBreakdown(
            String cifId, OffsetDateTime startDate, OffsetDateTime endDate) {

        log.debug("Getting category breakdown for CIF: {}", cifId);

        List<Object[]> categoryData = transactionRepository
                .getCategoryWiseSpending(cifId, startDate, endDate);

        return categoryData.stream()
                .map(row -> new CategorySpendingDTO(
                        (String) row[0],           // category
                        (BigDecimal) row[1],       // totalAmount
                        null                        // transactionCount (can add if needed)
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get monthly income/expense trend
     */
    public List<MonthlyTrendDTO> getMonthlyTrend(String cifId, int months) {
        log.debug("Getting monthly trend for CIF: {} (last {} months)", cifId, months);

        List<Object[]> trendData = transactionRepository
                .getMonthlyTrend(cifId, months);

        return trendData.stream()
                .map(row -> new MonthlyTrendDTO(
                        ((java.sql.Timestamp) row[0]).toLocalDateTime().toLocalDate(), // month
                        (BigDecimal) row[1],  // income
                        (BigDecimal) row[2]   // expenses
                ))
                .collect(Collectors.toList());
    }

    /**
     * Generate personalized insights
     */
    public List<SpendingInsightDTO> generateInsights(String cifId) {
        log.info("Generating insights for CIF: {}", cifId);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime oneMonthAgo = now.minusMonths(1);

        BigDecimal totalSpending = transactionRepository
                .calculateTotalExpenses(cifId, oneMonthAgo, now);

        List<CategorySpendingDTO> categories = getCategoryBreakdown(
                cifId, oneMonthAgo, now);

        List<MonthlyTrendDTO> trend = getMonthlyTrend(cifId, 3);

        return generateSpendingInsights(totalSpending, categories, trend);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Generate spending insights
     */
    private List<SpendingInsightDTO> generateSpendingInsights(
            BigDecimal totalSpending,
            List<CategorySpendingDTO> categories,
            List<MonthlyTrendDTO> monthlyTrend) {

        List<SpendingInsightDTO> insights = new ArrayList<>();

        // Insight 1: Highest spending category
        if (!categories.isEmpty()) {
            CategorySpendingDTO topCategory = categories.get(0);
            BigDecimal percentage = topCategory.getTotalAmount()
                    .divide(totalSpending, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            insights.add(SpendingInsightDTO.builder()
                    .type("SPEND_PATTERN")
                    .message(String.format(
                            "Your highest spending category is %s (%.1f%% of total spending)",
                            topCategory.getCategory(), percentage))
                    .recommendation("Consider setting a budget for this category")
                    .priority("HIGH")
                    .build());
        }

        // Insight 2: Spending trend
        if (monthlyTrend.size() >= 2) {
            MonthlyTrendDTO lastMonth = monthlyTrend.get(0);
            MonthlyTrendDTO previousMonth = monthlyTrend.get(1);

            BigDecimal change = lastMonth.getExpenses()
                    .subtract(previousMonth.getExpenses());

            if (change.compareTo(BigDecimal.ZERO) > 0) {
                insights.add(SpendingInsightDTO.builder()
                        .type("SPEND_PATTERN")
                        .message(String.format(
                                "Your spending increased by KES %.2f compared to last month",
                                change))
                        .recommendation("Review your recent purchases to identify any unusual spending")
                        .priority("MEDIUM")
                        .build());
            } else if (change.compareTo(BigDecimal.ZERO) < 0) {
                insights.add(SpendingInsightDTO.builder()
                        .type("SAVINGS_TIP")
                        .message(String.format(
                                "Great job! You saved KES %.2f compared to last month",
                                change.abs()))
                        .priority("LOW")
                        .build());
            }
        }

        // Insight 3: Budget recommendation
        if (!categories.isEmpty() && categories.size() > 3) {
            insights.add(SpendingInsightDTO.builder()
                    .type("BUDGET_RECOMMENDATION")
                    .message("You're spending across multiple categories")
                    .recommendation("Consider creating budgets for your top 3 spending categories")
                    .priority("MEDIUM")
                    .build());
        }

        return insights;
    }
}

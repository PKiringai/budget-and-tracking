package coop.bank.budget_tracking.service;


import coop.bank.budget_tracking.dto.request.BudgetCreateRequest;
import coop.bank.budget_tracking.dto.request.BudgetUpdateRequest;
import coop.bank.budget_tracking.dto.response.BudgetResponse;
import coop.bank.budget_tracking.dto.response.BudgetSummaryResponse;
import coop.bank.budget_tracking.dto.response.CategoryBudgetSummary;
import coop.bank.budget_tracking.entity.Budget;
import coop.bank.budget_tracking.entity.BudgetAlert;
import coop.bank.budget_tracking.enums.AlertType;
import coop.bank.budget_tracking.enums.PeriodType;
import coop.bank.budget_tracking.exceptions.BudgetNotFoundException;
import coop.bank.budget_tracking.exceptions.InvalidBudgetException;
import coop.bank.budget_tracking.repository.BudgetAlertRepository;
import coop.bank.budget_tracking.repository.BudgetRepository;
import coop.bank.budget_tracking.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Budget Service - Core business logic for budget management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetAlertRepository budgetAlertRepository;

    /**
     * Create a new budget
     */
    @Transactional
    public BudgetResponse createBudget(BudgetCreateRequest request) {
        log.info("Creating budget for CIF: {}, Category: {}",
                request.getCifId(), request.getCategory());

        // Validate category exists in transactions
        validateCategoryExists(request.getCifId(), request.getCategory());

        // Calculate end date if not provided
        LocalDate endDate = request.getEndDate() != null
                ? request.getEndDate()
                : calculateEndDate(request.getStartDate(), request.getPeriodType());

        // Check for overlapping budgets
        List<Budget> overlapping = budgetRepository.findOverlappingBudgets(
                request.getCifId(),
                request.getCategory(),
                request.getStartDate(),
                endDate
        );

        if (!overlapping.isEmpty()) {
            throw new InvalidBudgetException(
                    "A budget already exists for category '" + request.getCategory() +
                            "' in the period " + request.getStartDate() + " to " + endDate
            );
        }

        // Create budget entity
        Budget budget = Budget.builder()
                .cifId(request.getCifId())
                .category(request.getCategory())
                .budgetAmount(request.getBudgetAmount())
                .periodType(request.getPeriodType())
                .startDate(request.getStartDate())
                .endDate(endDate)
                .isActive(true)
                .alertThreshold80(request.getAlertThreshold80())
                .alertThreshold100(request.getAlertThreshold100())
                .rolloverEnabled(request.getRolloverEnabled())
                .build();

        // Save budget
        Budget savedBudget = budgetRepository.save(budget);
        log.info("Budget created with ID: {}", savedBudget.getId());

        // Return enriched response
        return enrichBudgetResponse(savedBudget);
    }

    /**
     * Update existing budget
     */
    @Transactional
    public BudgetResponse updateBudget(BudgetUpdateRequest request) {
        log.info("Updating budget ID: {}", request.getId());

        Budget budget = budgetRepository.findById(request.getId())
                .orElseThrow(() -> new BudgetNotFoundException(
                        "Budget not found with ID: " + request.getId()));

        // Update only provided fields
        if (request.getBudgetAmount() != null) {
            budget.setBudgetAmount(request.getBudgetAmount());
        }
        if (request.getEndDate() != null) {
            budget.setEndDate(request.getEndDate());
        }
        if (request.getIsActive() != null) {
            budget.setIsActive(request.getIsActive());
        }
        if (request.getAlertThreshold80() != null) {
            budget.setAlertThreshold80(request.getAlertThreshold80());
        }
        if (request.getAlertThreshold100() != null) {
            budget.setAlertThreshold100(request.getAlertThreshold100());
        }
        if (request.getRolloverEnabled() != null) {
            budget.setRolloverEnabled(request.getRolloverEnabled());
        }

        Budget updatedBudget = budgetRepository.save(budget);
        log.info("Budget updated: {}", updatedBudget.getId());

        return enrichBudgetResponse(updatedBudget);
    }

    /**
     * Get budget by ID
     */
    public BudgetResponse getBudgetById(Long id) {
        log.debug("Fetching budget ID: {}", id);

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(
                        "Budget not found with ID: " + id));

        return enrichBudgetResponse(budget);
    }

    /**
     * Get all active budgets for customer
     */
    public List<BudgetResponse> getActiveBudgets(String cifId) {
        log.debug("Fetching active budgets for CIF: {}", cifId);

        List<Budget> budgets = budgetRepository.findActiveBudgetsByCifId(cifId);

        return budgets.stream()
                .map(this::enrichBudgetResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get comprehensive budget summary
     */
    public BudgetSummaryResponse getBudgetSummary(
            String cifId, LocalDate startDate, LocalDate endDate) {

        log.info("Generating budget summary for CIF: {} from {} to {}",
                cifId, startDate, endDate);

        OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(
                OffsetDateTime.now().getOffset());
        OffsetDateTime endDateTime = endDate.atTime(23, 59, 59).atOffset(
                OffsetDateTime.now().getOffset());

        // Get active budgets
        List<Budget> budgets = budgetRepository.findActiveBudgetsForDate(
                cifId, startDate);

        // Calculate totals
        BigDecimal totalIncome = transactionRepository.calculateTotalIncome(
                cifId, startDateTime, endDateTime);

        BigDecimal totalExpenses = transactionRepository.calculateTotalExpenses(
                cifId, startDateTime, endDateTime);

        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getBudgetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get category breakdown
        List<CategoryBudgetSummary> categoryBreakdown = budgets.stream()
                .map(budget -> buildCategoryBudgetSummary(
                        budget, startDateTime, endDateTime))
                .collect(Collectors.toList());

        BigDecimal totalSpending = categoryBreakdown.stream()
                .map(CategoryBudgetSummary::getSpending)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingBudget = totalBudget.subtract(totalSpending);

        Integer utilizationPercentage = calculatePercentage(
                totalSpending, totalBudget);

        // Generate insights
        List<String> insights = generateBudgetInsights(
                categoryBreakdown, totalIncome, totalExpenses);

        return BudgetSummaryResponse.builder()
                .cifId(cifId)
                .periodStart(startDate)
                .periodEnd(endDate)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .totalBudget(totalBudget)
                .totalSpending(totalSpending)
                .remainingBudget(remainingBudget)
                .budgetUtilizationPercentage(utilizationPercentage)
                .categoryBreakdown(categoryBreakdown)
                .insights(insights)
                .build();
    }

    /**
     * Delete budget (soft delete)
     */
    @Transactional
    public void deleteBudget(Long id) {
        log.info("Deleting budget ID: {}", id);

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(
                        "Budget not found with ID: " + id));

        budget.setIsActive(false);
        budgetRepository.save(budget);

        log.info("Budget soft-deleted: {}", id);
    }

    /**
     * Check and trigger budget alerts for customer
     */
    @Transactional
    public void checkAndTriggerAlerts(String cifId) {
        log.info("Checking budget alerts for CIF: {}", cifId);

        List<Budget> budgets = budgetRepository.findActiveBudgetsByCifId(cifId);

        for (Budget budget : budgets) {
            checkBudgetThresholds(budget);
        }

        log.info("Alert check completed for CIF: {}", cifId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Enrich budget response with calculated fields
     */
    private BudgetResponse enrichBudgetResponse(Budget budget) {
        OffsetDateTime startDateTime = budget.getStartDate().atStartOfDay()
                .atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime endDateTime = budget.getEndDate().atTime(23, 59, 59)
                .atOffset(OffsetDateTime.now().getOffset());

        BigDecimal currentSpending = transactionRepository
                .calculateTotalSpendingByCategory(
                        budget.getCifId(),
                        budget.getCategory(),
                        startDateTime,
                        endDateTime
                );

        BigDecimal remainingBudget = budget.getBudgetAmount()
                .subtract(currentSpending);

        Integer percentageUsed = calculatePercentage(
                currentSpending, budget.getBudgetAmount());

        Integer daysRemaining = (int) ChronoUnit.DAYS.between(
                LocalDate.now(), budget.getEndDate());

        return BudgetResponse.builder()
                .id(budget.getId())
                .cifId(budget.getCifId())
                .category(budget.getCategory())
                .budgetAmount(budget.getBudgetAmount())
                .periodType(budget.getPeriodType().name())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .isActive(budget.getIsActive())
                .alertThreshold80(budget.getAlertThreshold80())
                .alertThreshold100(budget.getAlertThreshold100())
                .rolloverEnabled(budget.getRolloverEnabled())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .currentSpending(currentSpending)
                .remainingBudget(remainingBudget)
                .percentageUsed(percentageUsed)
                .daysRemaining(daysRemaining)
                .build();
    }

    /**
     * Build category budget summary
     */
    private CategoryBudgetSummary buildCategoryBudgetSummary(
            Budget budget, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {

        BigDecimal spending = transactionRepository
                .calculateTotalSpendingByCategory(
                        budget.getCifId(),
                        budget.getCategory(),
                        startDateTime,
                        endDateTime
                );

        BigDecimal remaining = budget.getBudgetAmount().subtract(spending);
        Integer percentageUsed = calculatePercentage(spending, budget.getBudgetAmount());
        String status = determineStatus(percentageUsed);

        return CategoryBudgetSummary.builder()
                .category(budget.getCategory())
                .budgetAmount(budget.getBudgetAmount())
                .spending(spending)
                .remaining(remaining)
                .percentageUsed(percentageUsed)
                .status(status)
                .build();
    }

    /**
     * Check budget thresholds and create alerts
     */
    private void checkBudgetThresholds(Budget budget) {
        OffsetDateTime startDateTime = budget.getStartDate().atStartOfDay()
                .atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime endDateTime = budget.getEndDate().atTime(23, 59, 59)
                .atOffset(OffsetDateTime.now().getOffset());

        BigDecimal currentSpending = transactionRepository
                .calculateTotalSpendingByCategory(
                        budget.getCifId(),
                        budget.getCategory(),
                        startDateTime,
                        endDateTime
                );

        Integer percentageUsed = calculatePercentage(
                currentSpending, budget.getBudgetAmount());

        // Check 80% threshold
        if (budget.getAlertThreshold80() && percentageUsed >= 80 && percentageUsed < 100) {
            createAlertIfNotExists(budget, AlertType.THRESHOLD_80,
                    currentSpending, percentageUsed);
        }

        // Check 100% threshold
        if (budget.getAlertThreshold100() && percentageUsed >= 100) {
            AlertType alertType = percentageUsed > 100
                    ? AlertType.EXCEEDED
                    : AlertType.THRESHOLD_100;
            createAlertIfNotExists(budget, alertType,
                    currentSpending, percentageUsed);
        }
    }

    /**
     * Create alert if it doesn't already exist
     */
    private void createAlertIfNotExists(
            Budget budget, AlertType alertType,
            BigDecimal currentSpending, Integer percentageUsed) {

        // Check if alert already sent in last 24 hours
        OffsetDateTime oneDayAgo = OffsetDateTime.now().minusHours(24);
        boolean alreadySent = budgetAlertRepository.hasAlertBeenSent(
                budget.getId(), alertType, oneDayAgo);

        if (alreadySent) {
            log.debug("Alert {} already sent for budget {}", alertType, budget.getId());
            return;
        }

        String message = String.format(
                "Budget alert: %s - You've used %d%% (%s) of your %s budget (%s)",
                alertType.getDescription(),
                percentageUsed,
                currentSpending,
                budget.getCategory(),
                budget.getBudgetAmount()
        );

        BudgetAlert alert = BudgetAlert.builder()
                .budget(budget)
                .cifId(budget.getCifId())
                .alertType(alertType)
                .currentSpending(currentSpending)
                .budgetLimit(budget.getBudgetAmount())
                .percentageUsed(percentageUsed)
                .category(budget.getCategory())
                .alertMessage(message)
                .isSent(false)
                .notificationChannels("SMS,EMAIL,PUSH")
                .build();

        budgetAlertRepository.save(alert);
        log.info("Created {} alert for budget {}", alertType, budget.getId());
    }

    /**
     * Calculate end date based on period type
     */
    private LocalDate calculateEndDate(LocalDate startDate, PeriodType periodType) {
        return switch (periodType) {
            case DAILY -> startDate;
            case WEEKLY -> startDate.plusWeeks(1).minusDays(1);
            case MONTHLY -> startDate.plusMonths(1).minusDays(1);
            case QUARTERLY -> startDate.plusMonths(3).minusDays(1);
            case YEARLY -> startDate.plusYears(1).minusDays(1);
            case CUSTOM -> startDate.plusMonths(1).minusDays(1); // Default to monthly
        };
    }

    /**
     * Calculate percentage (spending / budget * 100)
     */
    private Integer calculatePercentage(BigDecimal amount, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return amount.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    /**
     * Determine budget status based on percentage used
     */
    private String determineStatus(Integer percentageUsed) {
        if (percentageUsed >= 100) {
            return "EXCEEDED";
        } else if (percentageUsed >= 80) {
            return "WARNING";
        } else {
            return "ON_TRACK";
        }
    }

    /**
     * Validate that category exists in customer's transactions
     */
    private void validateCategoryExists(String cifId, String category) {
        List<String> availableCategories = transactionRepository
                .findDistinctCategoriesByCifId(cifId);

        if (!availableCategories.contains(category)) {
            throw new InvalidBudgetException(
                    "Category '" + category + "' not found in your transactions. " +
                            "Available categories: " + String.join(", ", availableCategories)
            );
        }
    }

    /**
     * Generate budget insights
     */
    private List<String> generateBudgetInsights(
            List<CategoryBudgetSummary> categories,
            BigDecimal totalIncome,
            BigDecimal totalExpenses) {

        List<String> insights = new java.util.ArrayList<>();

        // Find categories that are over budget
        List<String> overBudget = categories.stream()
                .filter(cat -> "EXCEEDED".equals(cat.getStatus()))
                .map(CategoryBudgetSummary::getCategory)
                .toList();

        if (!overBudget.isEmpty()) {
            insights.add("⚠️ Over budget in: " + String.join(", ", overBudget));
        }

        // Find categories near budget limit
        List<String> nearLimit = categories.stream()
                .filter(cat -> "WARNING".equals(cat.getStatus()))
                .map(CategoryBudgetSummary::getCategory)
                .toList();

        if (!nearLimit.isEmpty()) {
            insights.add("⚡ Approaching limit in: " + String.join(", ", nearLimit));
        }

        // Savings rate
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal savingsRate = totalIncome.subtract(totalExpenses)
                    .divide(totalIncome, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            insights.add(String.format("💰 Savings rate: %.1f%%", savingsRate));
        }

        return insights;
    }
}

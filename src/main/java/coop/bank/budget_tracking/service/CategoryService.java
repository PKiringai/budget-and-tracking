package coop.bank.budget_tracking.service;


import coop.bank.budget_tracking.dto.response.CategoryDTO;
import coop.bank.budget_tracking.repository.BudgetRepository;
import coop.bank.budget_tracking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Category Service - Uses actual transaction categories
 * No separate category_master table needed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    /**
     * Get all distinct categories from all transactions
     */
    public List<String> getAllCategories() {
        log.info("Fetching all distinct categories from transactions");
        return transactionRepository.findAllDistinctCategories();
    }

    /**
     * Get categories for a specific customer with statistics
     */
    public List<CategoryDTO> getCustomerCategories(String cifId) {
        log.info("Fetching category statistics for customer: {}", cifId);

        // Get spending by category from transactions
        List<Object[]> categorySpending = transactionRepository
                .getCategoryWiseSpending(
                        cifId,
                        java.time.OffsetDateTime.now().minusMonths(12),
                        java.time.OffsetDateTime.now()
                );

        // Get active budget categories
        Set<String> budgetCategories = budgetRepository
                .findActiveBudgetsByCifId(cifId)
                .stream()
                .map(budget -> budget.getCategory())
                .collect(Collectors.toSet());

        // Build CategoryDTO list
        return categorySpending.stream()
                .map(row -> {
                    String categoryName = (String) row[0];
                    BigDecimal totalSpending = (BigDecimal) row[1];

                    return CategoryDTO.builder()
                            .categoryName(categoryName)
                            .transactionCount(null) // Can add if needed
                            .totalSpending(totalSpending)
                            .hasBudget(budgetCategories.contains(categoryName))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get available categories for budgeting (categories with transactions)
     */
    public List<String> getAvailableCategories(String cifId) {
        log.info("Fetching available categories for customer: {}", cifId);
        return transactionRepository.findDistinctCategoriesByCifId(cifId);
    }

    /**
     * Get category statistics for a customer
     */
    public List<CategoryDTO> getCategoryStatistics(String cifId) {
        log.info("Fetching detailed category statistics for customer: {}", cifId);

        return getCustomerCategories(cifId);
    }
}

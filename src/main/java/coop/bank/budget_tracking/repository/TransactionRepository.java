package coop.bank.budget_tracking.repository;

import coop.bank.budget_tracking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository
 *
 * Provides data access methods for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find all transactions for a customer with pagination
     */
    Page<Transaction> findByCifIdOrderByTransactionDateDesc(String cifId, Pageable pageable);

    /**
     * Find transaction by transaction ID
     */
    Optional<Transaction> findByTransactionId(String transactionId);

    // ==================== DATE RANGE QUERIES ====================

    /**
     * Find transactions for a customer within date range
     */
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findByCifIdAndDateRange(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Find transactions for a customer within date range with pagination
     */
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByCifIdAndDateRangePaginated(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable
    );

    // ==================== CATEGORY QUERIES ====================

    /**
     * Find expense transactions by category and date range
     */
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.category = :category " +
            "AND t.partTransactionType = 'DEBIT' " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findExpensesByCategory(
            @Param("cifId") String cifId,
            @Param("category") String category,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Get distinct categories for a customer
     */
    @Query("SELECT DISTINCT t.category FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.category IS NOT NULL " +
            "AND t.category <> '' " +
            "ORDER BY t.category")
    List<String> findDistinctCategoriesByCifId(@Param("cifId") String cifId);

    /**
     * Get all distinct categories from all transactions
     */
    @Query("SELECT DISTINCT t.category FROM Transaction t " +
            "WHERE t.category IS NOT NULL " +
            "AND t.category <> '' " +
            "ORDER BY t.category")
    List<String> findAllDistinctCategories();

    // ==================== AGGREGATION QUERIES ====================

    /**
     * Calculate total spending by category in date range
     */
    @Query("SELECT COALESCE(SUM(ABS(t.transactionAmount)), 0) " +
            "FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.category = :category " +
            "AND t.partTransactionType = 'DEBIT' " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSpendingByCategory(
            @Param("cifId") String cifId,
            @Param("category") String category,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Calculate total income in date range
     */
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) " +
            "FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.partTransactionType = 'CREDIT' " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalIncome(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Calculate total expenses in date range
     */
    @Query("SELECT COALESCE(SUM(ABS(t.transactionAmount)), 0) " +
            "FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.partTransactionType = 'DEBIT' " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpenses(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Get category-wise spending summary
     */
    @Query("SELECT t.category, COALESCE(SUM(ABS(t.transactionAmount)), 0) " +
            "FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND t.partTransactionType = 'DEBIT' " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.category " +
            "ORDER BY SUM(ABS(t.transactionAmount)) DESC")
    List<Object[]> getCategoryWiseSpending(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    // ==================== MERCHANT QUERIES ====================

    /**
     * Find transactions by merchant
     */
    List<Transaction> findByCifIdAndMerchantContainingIgnoreCaseOrderByTransactionDateDesc(
            String cifId, String merchant
    );

    /**
     * Get top merchants by spending
     */
    @Query(value = "SELECT merchant " +
            "FROM transactions " +
            "WHERE cif_id = :cifId " +
            "AND part_tran_type = 'DEBIT' " +
            "AND merchant IS NOT NULL " +
            "AND tran_date BETWEEN :startDate AND :endDate " +
            "GROUP BY merchant " +
            "ORDER BY SUM(ABS(tran_amt)) DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<String> getTopMerchantsBySpending(
            @Param("cifId") String cifId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("limit") int limit
    );

    // ==================== UNCATEGORIZED TRANSACTIONS ====================

    /**
     * Find uncategorized transactions (for manual categorization)
     */
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND (t.category IS NULL OR t.category = '') " +
            "AND t.transactionDate >= :fromDate " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findUncategorizedTransactions(
            @Param("cifId") String cifId,
            @Param("fromDate") OffsetDateTime fromDate
    );

    /**
     * Count uncategorized transactions
     */
    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.cifId = :cifId " +
            "AND (t.category IS NULL OR t.category = '')")
    Long countUncategorizedTransactions(@Param("cifId") String cifId);

    // ==================== MONTHLY STATISTICS ====================

    /**
     * Get monthly transaction count
     */
    @Query(value = "SELECT COUNT(*) " +
            "FROM transactions " +
            "WHERE cif_id = :cifId " +
            "AND DATE_TRUNC('month', tran_date) = DATE_TRUNC('month', CAST(:month AS timestamp))",
            nativeQuery = true)
    Long countMonthlyTransactions(
            @Param("cifId") String cifId,
            @Param("month") LocalDate month
    );

    /**
     * Get monthly spending trend (last N months)
     */
    @Query(value = "SELECT " +
            "DATE_TRUNC('month', tran_date) as month, " +
            "COALESCE(SUM(CASE WHEN part_tran_type = 'CREDIT' THEN tran_amt ELSE 0 END), 0) as income, " +
            "COALESCE(SUM(CASE WHEN part_tran_type = 'DEBIT' THEN ABS(tran_amt) ELSE 0 END), 0) as expenses " +
            "FROM transactions " +
            "WHERE cif_id = :cifId " +
            "AND tran_date >= CURRENT_DATE - INTERVAL ':months months' " +
            "GROUP BY DATE_TRUNC('month', tran_date) " +
            "ORDER BY month DESC",
            nativeQuery = true)
    List<Object[]> getMonthlyTrend(
            @Param("cifId") String cifId,
            @Param("months") int months
    );
}

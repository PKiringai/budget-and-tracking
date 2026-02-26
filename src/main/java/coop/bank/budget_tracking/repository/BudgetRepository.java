package coop.bank.budget_tracking.repository;

import coop.bank.budget_tracking.entity.Budget;
import coop.bank.budget_tracking.enums.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // ==================== ACTIVE BUDGET QUERIES ====================

    /**
     * Find all active budgets for a customer
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.isActive = true " +
            "ORDER BY b.category")
    List<Budget> findActiveBudgetsByCifId(@Param("cifId") String cifId);

    /**
     * Find active budget by customer and category
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.category = :category " +
            "AND b.isActive = true")
    Optional<Budget> findActiveBudgetByCifIdAndCategory(
            @Param("cifId") String cifId,
            @Param("category") String category
    );

    /**
     * Find active budgets for a specific date
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.isActive = true " +
            "AND :date BETWEEN b.startDate AND b.endDate " +
            "ORDER BY b.category")
    List<Budget> findActiveBudgetsForDate(
            @Param("cifId") String cifId,
            @Param("date") LocalDate date
    );

    // ==================== VALIDATION QUERIES ====================

    /**
     * Find overlapping budgets (for validation during creation)
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.category = :category " +
            "AND b.isActive = true " +
            "AND (" +
            "  (b.startDate BETWEEN :startDate AND :endDate) " +
            "  OR (b.endDate BETWEEN :startDate AND :endDate) " +
            "  OR (:startDate BETWEEN b.startDate AND b.endDate) " +
            "  OR (:endDate BETWEEN b.startDate AND b.endDate)" +
            ")")
    List<Budget> findOverlappingBudgets(
            @Param("cifId") String cifId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Check if active budget exists for category
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.category = :category " +
            "AND b.isActive = true")
    boolean existsActiveBudgetForCategory(
            @Param("cifId") String cifId,
            @Param("category") String category
    );

    // ==================== PERIOD TYPE QUERIES ====================

    /**
     * Find budgets by period type
     */
    List<Budget> findByCifIdAndPeriodTypeAndIsActiveTrue(
            String cifId,
            PeriodType periodType
    );

    /**
     * Find current month budgets
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.isActive = true " +
            "AND b.periodType = 'MONTHLY' " +
            "AND :currentDate BETWEEN b.startDate AND b.endDate")
    List<Budget> findCurrentMonthBudgets(
            @Param("cifId") String cifId,
            @Param("currentDate") LocalDate currentDate
    );

    // ==================== EXPIRY AND ALERTS ====================

    /**
     * Find budgets expiring soon
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.cifId = :cifId " +
            "AND b.isActive = true " +
            "AND b.endDate BETWEEN CURRENT_DATE AND :expiryDate " +
            "ORDER BY b.endDate")
    List<Budget> findBudgetsExpiringSoon(
            @Param("cifId") String cifId,
            @Param("expiryDate") LocalDate expiryDate
    );

    /**
     * Find all budgets requiring alert checks
     */
    @Query("SELECT b FROM Budget b " +
            "WHERE b.isActive = true " +
            "AND CURRENT_DATE BETWEEN b.startDate AND b.endDate " +
            "AND (b.alertThreshold80 = true OR b.alertThreshold100 = true)")
    List<Budget> findBudgetsRequiringAlertCheck();

    // ==================== SOFT DELETE ====================

    /**
     * Soft delete budget by ID
     */
    @Modifying
    @Query("UPDATE Budget b SET b.isActive = false, b.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE b.id = :budgetId")
    int softDelete(@Param("budgetId") Long budgetId);

    /**
     * Soft delete all budgets for a category
     */
    @Modifying
    @Query("UPDATE Budget b SET b.isActive = false, b.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE b.cifId = :cifId AND b.category = :category AND b.isActive = true")
    int softDeleteByCategory(
            @Param("cifId") String cifId,
            @Param("category") String category
    );

    // ==================== STATISTICS ====================

    /**
     * Count active budgets for customer
     */
    @Query("SELECT COUNT(b) FROM Budget b " +
            "WHERE b.cifId = :cifId AND b.isActive = true")
    Long countActiveBudgets(@Param("cifId") String cifId);

    /**
     * Get distinct categories with active budgets
     */
    @Query("SELECT DISTINCT b.category FROM Budget b " +
            "WHERE b.cifId = :cifId AND b.isActive = true " +
            "ORDER BY b.category")
    List<String> findActiveBudgetCategories(@Param("cifId") String cifId);
}

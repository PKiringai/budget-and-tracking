package coop.bank.budget_tracking.repository;

import coop.bank.budget_tracking.entity.BudgetAlert;
import coop.bank.budget_tracking.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BudgetAlertRepository extends JpaRepository<BudgetAlert, Long> {

    // ==================== UNSENT ALERTS ====================

    /**
     * Find all unsent alerts
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.isSent = false " +
            "ORDER BY a.createdAt DESC")
    List<BudgetAlert> findUnsentAlerts();

    /**
     * Find unsent alerts for a customer
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.cifId = :cifId " +
            "AND a.isSent = false " +
            "ORDER BY a.createdAt DESC")
    List<BudgetAlert> findUnsentAlertsByCifId(@Param("cifId") String cifId);

    // ==================== CUSTOMER ALERTS ====================

    /**
     * Find all alerts for a customer (sent and unsent)
     */
    List<BudgetAlert> findByCifIdOrderByCreatedAtDesc(String cifId);

    /**
     * Find recent alerts for a customer (last N days)
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.cifId = :cifId " +
            "AND a.createdAt >= :since " +
            "ORDER BY a.createdAt DESC")
    List<BudgetAlert> findRecentAlertsByCifId(
            @Param("cifId") String cifId,
            @Param("since") OffsetDateTime since
    );

    // ==================== BUDGET-SPECIFIC ALERTS ====================

    /**
     * Find recent alerts for a specific budget
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.budget.id = :budgetId " +
            "AND a.createdAt >= :since " +
            "ORDER BY a.createdAt DESC")
    List<BudgetAlert> findRecentAlertsByBudget(
            @Param("budgetId") Long budgetId,
            @Param("since") OffsetDateTime since
    );

    /**
     * Find alerts by budget and type
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.budget.id = :budgetId " +
            "AND a.alertType = :alertType " +
            "ORDER BY a.createdAt DESC")
    List<BudgetAlert> findByBudgetIdAndAlertType(
            @Param("budgetId") Long budgetId,
            @Param("alertType") AlertType alertType
    );

    // ==================== DUPLICATE CHECK ====================

    /**
     * Check if alert of same type was already sent for budget (within time period)
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM BudgetAlert a " +
            "WHERE a.budget.id = :budgetId " +
            "AND a.alertType = :alertType " +
            "AND a.isSent = true " +
            "AND a.sentAt >= :since")
    boolean hasAlertBeenSent(
            @Param("budgetId") Long budgetId,
            @Param("alertType") AlertType alertType,
            @Param("since") OffsetDateTime since
    );

    /**
     * Find latest sent alert for budget and type
     */
    @Query("SELECT a FROM BudgetAlert a " +
            "WHERE a.budget.id = :budgetId " +
            "AND a.alertType = :alertType " +
            "AND a.isSent = true " +
            "ORDER BY a.sentAt DESC " +
            "LIMIT 1")
    BudgetAlert findLatestSentAlert(
            @Param("budgetId") Long budgetId,
            @Param("alertType") AlertType alertType
    );

    // ==================== STATISTICS ====================

    /**
     * Count unsent alerts
     */
    @Query("SELECT COUNT(a) FROM BudgetAlert a WHERE a.isSent = false")
    Long countUnsentAlerts();

    /**
     * Count alerts by customer
     */
    Long countByCifId(String cifId);

    /**
     * Count alerts by type for customer
     */
    Long countByCifIdAndAlertType(String cifId, AlertType alertType);

    // ==================== BATCH OPERATIONS ====================

    /**
     * Mark alert as sent
     */
    @Modifying
    @Query("UPDATE BudgetAlert a " +
            "SET a.isSent = true, a.sentAt = CURRENT_TIMESTAMP " +
            "WHERE a.id = :alertId")
    int markAsSent(@Param("alertId") Long alertId);

    /**
     * Mark multiple alerts as sent
     */
    @Modifying
    @Query("UPDATE BudgetAlert a " +
            "SET a.isSent = true, a.sentAt = CURRENT_TIMESTAMP " +
            "WHERE a.id IN :alertIds")
    int markMultipleAsSent(@Param("alertIds") List<Long> alertIds);

    /**
     * Delete old alerts (cleanup)
     */
    @Modifying
    @Query("DELETE FROM BudgetAlert a WHERE a.createdAt < :before")
    int deleteOldAlerts(@Param("before") OffsetDateTime before);
}

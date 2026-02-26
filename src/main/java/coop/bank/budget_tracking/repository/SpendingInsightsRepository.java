package coop.bank.budget_tracking.repository;

import coop.bank.budget_tracking.entity.SpendingInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpendingInsightsRepository extends JpaRepository<SpendingInsight, Long> {

    // ==================== UNREAD INSIGHTS ====================

    /**
     * Find unread insights for customer
     */
    @Query("SELECT i FROM SpendingInsight i " +
            "WHERE i.cifId = :cifId " +
            "AND i.isRead = false " +
            "AND (i.validUntil IS NULL OR i.validUntil > CURRENT_TIMESTAMP) " +
            "ORDER BY i.priority DESC, i.createdAt DESC")
    List<SpendingInsight> findUnreadInsightsByCifId(@Param("cifId") String cifId);

    /**
     * Find high priority unread insights
     */
    @Query("SELECT i FROM SpendingInsight i " +
            "WHERE i.cifId = :cifId " +
            "AND i.isRead = false " +
            "AND i.priority = 'HIGH' " +
            "AND (i.validUntil IS NULL OR i.validUntil > CURRENT_TIMESTAMP) " +
            "ORDER BY i.createdAt DESC")
    List<SpendingInsight> findHighPriorityUnreadInsights(@Param("cifId") String cifId);

    // ==================== ALL INSIGHTS ====================

    /**
     * Find all insights for customer
     */
    List<SpendingInsight> findByCifIdOrderByCreatedAtDesc(String cifId);

    /**
     * Find insights by type
     */
    List<SpendingInsight> findByCifIdAndInsightTypeOrderByCreatedAtDesc(
            String cifId,
            String insightType
    );

    /**
     * Find insights by category
     */
    List<SpendingInsight> findByCifIdAndCategoryOrderByCreatedAtDesc(
            String cifId,
            String category
    );

    // ==================== VALIDITY CHECKS ====================

    /**
     * Find valid (non-expired) insights
     */
    @Query("SELECT i FROM SpendingInsight i " +
            "WHERE i.cifId = :cifId " +
            "AND (i.validUntil IS NULL OR i.validUntil > CURRENT_TIMESTAMP) " +
            "ORDER BY i.createdAt DESC")
    List<SpendingInsight> findValidInsightsByCifId(@Param("cifId") String cifId);

    /**
     * Delete expired insights
     */
    @Modifying
    @Query("DELETE FROM SpendingInsight i " +
            "WHERE i.validUntil IS NOT NULL AND i.validUntil < CURRENT_TIMESTAMP")
    int deleteExpiredInsights();

    // ==================== MARK AS READ/ACTIONED ====================

    /**
     * Mark insight as read
     */
    @Modifying
    @Query("UPDATE SpendingInsight i SET i.isRead = true WHERE i.id = :insightId")
    int markAsRead(@Param("insightId") Long insightId);

    /**
     * Mark insight as actioned
     */
    @Modifying
    @Query("UPDATE SpendingInsight i " +
            "SET i.isActioned = true WHERE i.id = :insightId")
    int markAsActioned(@Param("insightId") Long insightId);

    /**
     * Mark all insights as read for customer
     */
    @Modifying
    @Query("UPDATE SpendingInsight i SET i.isRead = true WHERE i.cifId = :cifId")
    int markAllAsRead(@Param("cifId") String cifId);

    // ==================== STATISTICS ====================

    /**
     * Count unread insights
     */
    Long countByCifIdAndIsReadFalse(String cifId);

    /**
     * Count insights by priority
     */
    Long countByCifIdAndPriority(String cifId, String priority);

    /**
     * Count actioned insights
     */
    Long countByCifIdAndIsActionedTrue(String cifId);
}

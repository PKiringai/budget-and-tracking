package coop.bank.budget_tracking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Spending Insight Entity - Personalized financial insights
 *
 * Stores AI-generated insights, tips, and recommendations
 * for customer financial behavior
 */
@Entity
@Table(name = "spending_insights", schema = "public", indexes = {
        @Index(name = "idx_insight_cif_read", columnList = "cif_id, is_read"),
        @Index(name = "idx_insight_type_priority", columnList = "insight_type, priority"),
        @Index(name = "idx_insight_valid_until", columnList = "valid_until")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SpendingInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cif_id", nullable = false, length = 100)
    private String cifId;

    @Column(name = "insight_type", nullable = false, length = 50)
    private String insightType; // SAVINGS_TIP, SPEND_PATTERN, BUDGET_RECOMMENDATION, ANOMALY

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "insight_message", columnDefinition = "text", nullable = false)
    private String insightMessage;

    @Column(name = "action_recommendation", columnDefinition = "text")
    private String actionRecommendation;

    @Column(name = "potential_savings", precision = 15, scale = 2)
    private BigDecimal potentialSavings;

    @Column(name = "priority", length = 20)
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "is_actioned", nullable = false)
    @Builder.Default
    private Boolean isActioned = false;

    @Column(name = "valid_until")
    private OffsetDateTime validUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    /**
     * Mark insight as read
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * Mark insight as actioned
     */
    public void markAsActioned() {
        this.isActioned = true;
    }

    /**
     * Check if insight is still valid
     */
    public boolean isValid() {
        if (validUntil == null) {
            return true;
        }
        return OffsetDateTime.now().isBefore(validUntil);
    }
}

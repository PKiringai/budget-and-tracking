package coop.bank.budget_tracking.entity;


import coop.bank.budget_tracking.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Budget Alert Entity - Tracks budget threshold alerts
 *
 * Created when a customer's spending reaches configured thresholds
 * (80%, 100%, or exceeds budget)
 */
@Entity
@Table(name = "budget_alerts", schema = "public", indexes = {
        @Index(name = "idx_alert_cif_sent", columnList = "cif_id, is_sent"),
        @Index(name = "idx_alert_budget_type", columnList = "budget_id, alert_type"),
        @Index(name = "idx_alert_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"budget"})
public class BudgetAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "cif_id", nullable = false, length = 100)
    private String cifId;

    @Column(name = "alert_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Column(name = "current_spending", precision = 15, scale = 2)
    private BigDecimal currentSpending;

    @Column(name = "budget_limit", precision = 15, scale = 2)
    private BigDecimal budgetLimit;

    @Column(name = "percentage_used")
    private Integer percentageUsed;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "alert_message", columnDefinition = "text")
    private String alertMessage;

    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "notification_channels", length = 100)
    private String notificationChannels; // Comma-separated: SMS,EMAIL,PUSH

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    /**
     * Mark alert as sent
     */
    public void markAsSent() {
        this.isSent = true;
        this.sentAt = OffsetDateTime.now();
    }
}

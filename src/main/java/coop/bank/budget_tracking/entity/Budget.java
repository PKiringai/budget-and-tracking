package coop.bank.budget_tracking.entity;


import coop.bank.budget_tracking.enums.PeriodType;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Budget Entity - Customer budget allocations by category
 *
 * Represents a budget set by a customer for a specific spending category
 * over a defined time period
 */
@Entity
@Table(name = "budgets", schema = "public", indexes = {
        @Index(name = "idx_budget_cif_category", columnList = "cif_id, category"),
        @Index(name = "idx_budget_active_dates",
                columnList = "is_active, start_date, end_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cif_id", nullable = false, length = 100)
    @NotBlank(message = "Customer ID is required")
    private String cifId;

    @Column(name = "category", nullable = false, length = 100)
    @NotBlank(message = "Category is required")
    private String category; // e.g., "Food, drinks", "Transport"

    @Column(name = "budget_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    private BigDecimal budgetAmount;

    @Column(name = "period_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Period type is required")
    private PeriodType periodType;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "alert_threshold_80", nullable = false)
    @Builder.Default
    private Boolean alertThreshold80 = true;

    @Column(name = "alert_threshold_100", nullable = false)
    @Builder.Default
    private Boolean alertThreshold100 = true;

    @Column(name = "rollover_enabled")
    @Builder.Default
    private Boolean rolloverEnabled = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Business validation

    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (endDate == null || startDate == null) {
            return true; // Will be caught by @NotNull
        }
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
}

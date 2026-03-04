package coop.bank.budget_tracking.entity;

import coop.bank.budget_tracking.enums.PeriodUnit;
import jakarta.persistence.*;
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
import java.util.List;

@Entity
@Table(name = "budgets", schema = "public")
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

    @Column(name = "budget_code", nullable = false, length = 100, unique = true)
    private String budgetCode; // e.g., same as frontend messageId

    @Column(name = "userId", nullable = false, length = 100)
    @NotBlank(message = "Customer ID is required")
    private String userId;

    @Column(name = "budget_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Total budget amount is required")
    private BigDecimal budgetAmount;

    @Column(name = "currency", nullable = false, length = 10)
    @NotBlank(message = "Currency is required")
    private String currency;

    @Column(name = "period")
    private Integer period; // optional

    @Column(name = "period_unit", length = 20)
    @Enumerated(EnumType.STRING)
    private PeriodUnit periodUnit; // optional MONTHS, WEEKS, DAYS

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

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

    // Relationship to BudgetItems
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetItems> items;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
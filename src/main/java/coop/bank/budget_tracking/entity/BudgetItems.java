package coop.bank.budget_tracking.entity;

import coop.bank.budget_tracking.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "budget_items",
        schema = "public",
        indexes = {
                @Index(name = "idx_budget_item_budget", columnList = "budget_id"),
                @Index(name = "idx_budget_item_category", columnList = "category_id"),
                @Index(name = "idx_budget_item_status", columnList = "status")
        }
)

public class BudgetItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "allocated_amount", nullable = false)
    private BigDecimal allocatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.from(LocalDateTime.now());
    }
}

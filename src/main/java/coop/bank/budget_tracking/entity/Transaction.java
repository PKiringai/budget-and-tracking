package coop.bank.budget_tracking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Transaction Entity - Maps to existing 'transactions' table
 *
 * Contains customer transaction data with ML categorization
 */
@Entity
@Table(name = "transactions", schema = "public", indexes = {
        @Index(name = "idx_trans_cif_category_date",
                columnList = "cif_id, category, tran_date"),
        @Index(name = "idx_trans_cif_date_range",
                columnList = "cif_id, tran_date, pstd_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"transactionParticular"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_table", length = 3)
    private String sourceTable;

    @Column(name = "tran_date")
    private OffsetDateTime transactionDate;

    @Column(name = "pstd_date")
    private OffsetDateTime postedDate;

    @Column(name = "tran_id", length = 100)
    private String transactionId;

    @Column(name = "cif_id", length = 100, nullable = false)
    private String cifId;

    @Column(name = "acid", length = 100)
    private String accountId;

    @Column(name = "foracid", length = 100)
    private String forAccountId;

    @Column(name = "part_tran_type", length = 50)
    private String partTransactionType; // DEBIT or CREDIT

    @Column(name = "tran_amt", precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "tran_particular", columnDefinition = "text")
    private String transactionParticular;

    @Column(name = "merchant", length = 500)
    private String merchant;

    @Column(name = "user_part_tran_code")
    private String userPartTransactionCode;

    @Column(name = "subcategory", length = 100)
    private String subcategory;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "confidence")
    private Double confidence; // ML confidence score (0.0 to 1.0)

    @Column(name = "migration_status")
    private String migrationStatus;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        processedAt = OffsetDateTime.now();
    }

    // Business logic helper methods

    /**
     * Check if transaction is an expense (DEBIT)
     */
    public boolean isExpense() {
        return "DEBIT".equalsIgnoreCase(partTransactionType) ||
                (transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) < 0);
    }

    /**
     * Check if transaction is income (CREDIT)
     */
    public boolean isIncome() {
        return "CREDIT".equalsIgnoreCase(partTransactionType) ||
                (transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * Get absolute transaction amount
     */
    public BigDecimal getAbsoluteAmount() {
        return transactionAmount != null ? transactionAmount.abs() : BigDecimal.ZERO;
    }

}

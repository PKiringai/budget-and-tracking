package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Response DTO for transaction information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private String transactionId;
    private OffsetDateTime transactionDate;
    private OffsetDateTime postedDate;
    private String merchant;
    private String transactionParticular;
    private BigDecimal amount;
    private String transactionType; // DEBIT or CREDIT
    private String category;
    private String subcategory;
    private Double confidence;
}

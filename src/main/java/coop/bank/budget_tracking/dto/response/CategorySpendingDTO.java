package coop.bank.budget_tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Category spending breakdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpendingDTO {

    private String category;
    private BigDecimal totalAmount;
    private Long transactionCount;
}

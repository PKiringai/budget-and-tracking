package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Category information with statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private String categoryName;
    private Long transactionCount;
    private BigDecimal totalSpending;
    private Boolean hasBudget;
}

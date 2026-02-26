package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Spending insight/recommendation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendingInsightDTO {

    private String type;
    private String message;
    private String recommendation;
    private BigDecimal potentialSavings;
    private String priority;
}

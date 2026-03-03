package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Response DTO for budget information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

//    // Status fields
//    private String statusCode;
//    private String statusDescription;
//    private String messageCode;
//    private String messageDescription;
//    private String messageId;
//
//    // Budget fields
//    private String accountNumber;
//    private String accountName;
//    private String phoneNumber;
//    private String savingsPeriod;
//    private String maturityDate;
//    private String startDate;
//    private String endDate;
//    private String frequency;
//    private String reminder;
//    private String transactionId;

    private Long id;
    private String cifId;
    private String category;
    private BigDecimal budgetAmount;
    private String periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Boolean alertThreshold80;
    private Boolean alertThreshold100;
    private Boolean rolloverEnabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Calculated fields
    private BigDecimal currentSpending;
    private BigDecimal remainingBudget;
    private Integer percentageUsed;
    private Integer daysRemaining;
}

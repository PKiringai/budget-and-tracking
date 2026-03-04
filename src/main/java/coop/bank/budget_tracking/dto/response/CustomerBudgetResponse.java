package coop.bank.budget_tracking.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CustomerBudgetResponse {

    // Budget / account fields
    private String accountNumber;
    private String accountName;
    private String phoneNumber;
    private String savingsPeriod;
    private String maturityDate;
    private String startDate;
    private String endDate;
    private String frequency;
    private String reminder;
    private String transactionId;
}

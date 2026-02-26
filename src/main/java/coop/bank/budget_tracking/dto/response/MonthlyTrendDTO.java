package coop.bank.budget_tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * Monthly income/expense trend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrendDTO {

    private LocalDate month;
    private BigDecimal income;
    private BigDecimal expenses;
}

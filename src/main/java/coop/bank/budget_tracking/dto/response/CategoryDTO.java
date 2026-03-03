package coop.bank.budget_tracking.dto.response;


import coop.bank.budget_tracking.enums.Status;
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

    private Long id;
    private String categoryCode;
    private String categoryName;
    private Status status;
}

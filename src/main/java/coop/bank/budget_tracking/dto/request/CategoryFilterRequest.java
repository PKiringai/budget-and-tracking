package coop.bank.budget_tracking.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for filtering transactions by category
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilterRequest {

    @NotBlank(message = "Customer ID is required")
    private String cifId;

    private String category; // Optional - if null, returns all categories

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "transactionDate";

    @Builder.Default
    private String sortDirection = "DESC";
}

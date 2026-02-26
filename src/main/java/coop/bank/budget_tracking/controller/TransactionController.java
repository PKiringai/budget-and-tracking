package coop.bank.budget_tracking.controller;


import coop.bank.budget_tracking.dto.request.CategoryFilterRequest;
import coop.bank.budget_tracking.dto.response.ApiResponse;
import coop.bank.budget_tracking.dto.response.TransactionResponse;
import coop.bank.budget_tracking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Transaction Controller - REST API for transactions
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transactions", description = "APIs for customer transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * GET /api/v1/transactions/customer/{cifId}
     * Get paginated transactions for a customer
     */
    @GetMapping("/customer/{cifId}")
    @Operation(summary = "Get Customer Transactions",
            description = "Retrieve paginated transactions for a customer")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getCustomerTransactions(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/v1/transactions/customer/{} - page: {}, size: {}",
                cifId, page, size);

        Page<TransactionResponse> transactions = transactionService
                .getTransactionsByCustomer(cifId, page, size);

        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * POST /api/v1/transactions/filter
     * Get transactions with filters
     */
    @PostMapping("/filter")
    @Operation(summary = "Filter Transactions",
            description = "Filter transactions by category and date range")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> filterTransactions(
            @Valid @RequestBody CategoryFilterRequest request) {

        log.info("POST /api/v1/transactions/filter - CIF: {}, Category: {}",
                request.getCifId(), request.getCategory());

        Page<TransactionResponse> transactions = transactionService
                .getTransactionsWithFilter(request);

        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * GET /api/v1/transactions/customer/{cifId}/uncategorized
     * Get uncategorized transactions
     */
    @GetMapping("/customer/{cifId}/uncategorized")
    @Operation(summary = "Get Uncategorized Transactions",
            description = "Retrieve transactions without categories")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getUncategorizedTransactions(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Number of months to look back")
            @RequestParam(defaultValue = "6") int months) {

        log.info("GET /api/v1/transactions/customer/{}/uncategorized", cifId);

        List<TransactionResponse> transactions = transactionService
                .getUncategorizedTransactions(cifId, months);

        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * GET /api/v1/transactions/customer/{cifId}/category/{category}/spending
     * Calculate spending for a category
     */
    @GetMapping("/customer/{cifId}/category/{category}/spending")
    @Operation(summary = "Calculate Category Spending",
            description = "Calculate total spending for a specific category")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateCategorySpending(
            @PathVariable @NotBlank String cifId,
            @PathVariable @NotBlank String category,
            @Parameter(description = "Start date (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startDate,
            @Parameter(description = "End date (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endDate) {

        log.info("GET /api/v1/transactions/customer/{}/category/{}/spending",
                cifId, category);

        BigDecimal spending = transactionService.calculateCategorySpending(
                cifId, category, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(spending));
    }

    /**
     * PATCH /api/v1/transactions/{id}/category
     * Re-categorize a transaction
     */
    @PatchMapping("/{id}/category")
    @Operation(summary = "Re-categorize Transaction",
            description = "Update the category of a transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> recategorizeTransaction(
            @PathVariable Long id,
            @Parameter(description = "New category")
            @RequestParam @NotBlank String category) {

        log.info("PATCH /api/v1/transactions/{}/category - New: {}", id, category);

        TransactionResponse response = transactionService
                .recategorizeTransaction(id, category);

        return ResponseEntity.ok(
                ApiResponse.success("Transaction re-categorized successfully", response));
    }

    /**
     * GET /api/v1/transactions/{id}
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Transaction",
            description = "Retrieve transaction details by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable Long id) {

        log.info("GET /api/v1/transactions/{}", id);

        TransactionResponse response = transactionService.getTransactionById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

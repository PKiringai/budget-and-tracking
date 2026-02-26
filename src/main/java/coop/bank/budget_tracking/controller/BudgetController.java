package coop.bank.budget_tracking.controller;


import coop.bank.budget_tracking.dto.request.BudgetCreateRequest;
import coop.bank.budget_tracking.dto.request.BudgetUpdateRequest;
import coop.bank.budget_tracking.dto.response.ApiResponse;
import coop.bank.budget_tracking.dto.response.BudgetResponse;
import coop.bank.budget_tracking.dto.response.BudgetSummaryResponse;
import coop.bank.budget_tracking.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Budget Controller - REST API for budget management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Budget Management", description = "APIs for managing customer budgets")
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * POST /api/v1/budgets
     * Create a new budget
     */
    @PostMapping
    @Operation(summary = "Create Budget",
            description = "Create a new budget for a customer category")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @Valid @RequestBody BudgetCreateRequest request) {

        log.info("POST /api/v1/budgets - Creating budget for CIF: {}, Category: {}",
                request.getCifId(), request.getCategory());

        BudgetResponse response = budgetService.createBudget(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", response));
    }

    /**
     * PUT /api/v1/budgets/{id}
     * Update existing budget
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Budget",
            description = "Update an existing budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetUpdateRequest request) {

        log.info("PUT /api/v1/budgets/{} - Updating budget", id);

        request.setId(id);
        BudgetResponse response = budgetService.updateBudget(request);

        return ResponseEntity.ok(
                ApiResponse.success("Budget updated successfully", response));
    }

    /**
     * GET /api/v1/budgets/{id}
     * Get budget by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get Budget",
            description = "Retrieve budget details by ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            @PathVariable Long id) {

        log.info("GET /api/v1/budgets/{}", id);

        BudgetResponse response = budgetService.getBudgetById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/budgets/customer/{cifId}
     * Get all active budgets for a customer
     */
    @GetMapping("/customer/{cifId}")
    @Operation(summary = "Get Customer Budgets",
            description = "Retrieve all active budgets for a customer")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getCustomerBudgets(
            @PathVariable @NotBlank String cifId) {

        log.info("GET /api/v1/budgets/customer/{}", cifId);

        List<BudgetResponse> budgets = budgetService.getActiveBudgets(cifId);

        return ResponseEntity.ok(ApiResponse.success(budgets));
    }

    /**
     * GET /api/v1/budgets/customer/{cifId}/summary
     * Get budget summary with spending data
     */
    @GetMapping("/customer/{cifId}/summary")
    @Operation(summary = "Get Budget Summary",
            description = "Get comprehensive budget summary with spending breakdown")
    public ResponseEntity<ApiResponse<BudgetSummaryResponse>> getBudgetSummary(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/v1/budgets/customer/{}/summary from {} to {}",
                cifId, startDate, endDate);

        BudgetSummaryResponse summary = budgetService.getBudgetSummary(
                cifId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * DELETE /api/v1/budgets/{id}
     * Delete (deactivate) budget
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Budget",
            description = "Soft delete budget by setting isActive to false")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable Long id) {

        log.info("DELETE /api/v1/budgets/{}", id);

        budgetService.deleteBudget(id);

        return ResponseEntity.ok(
                ApiResponse.success("Budget deleted successfully", null));
    }

    /**
     * POST /api/v1/budgets/customer/{cifId}/check-alerts
     * Manually trigger budget alert check
     */
    @PostMapping("/customer/{cifId}/check-alerts")
    @Operation(summary = "Check Budget Alerts",
            description = "Manually trigger budget alert evaluation")
    public ResponseEntity<ApiResponse<Void>> checkBudgetAlerts(
            @PathVariable @NotBlank String cifId) {

        log.info("POST /api/v1/budgets/customer/{}/check-alerts", cifId);

        budgetService.checkAndTriggerAlerts(cifId);

        return ResponseEntity.ok(
                ApiResponse.success("Alert check completed", null));
    }
}

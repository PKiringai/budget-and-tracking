package coop.bank.budget_tracking.controller;


import coop.bank.budget_tracking.dto.response.ApiResponse;
import coop.bank.budget_tracking.dto.response.CategoryDTO;
import coop.bank.budget_tracking.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Category Controller - REST API for categories
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories", description = "Category management APIs - uses actual transaction categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * GET /api/v1/categories
     * Get all distinct categories from transactions
     */
    @GetMapping
    @Operation(summary = "Get All Categories",
            description = "Retrieve all distinct categories from transaction data")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        log.info("GET /api/v1/categories");

        List<String> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * GET /api/v1/categories/customer/{cifId}
     * Get categories for specific customer with statistics
     */
    @GetMapping("/customer/{cifId}")
    @Operation(summary = "Get Customer Categories",
            description = "Retrieve categories for a customer with spending statistics")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCustomerCategories(
            @PathVariable @NotBlank String cifId) {

        log.info("GET /api/v1/categories/customer/{}", cifId);

        List<CategoryDTO> categories = categoryService.getCustomerCategories(cifId);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * GET /api/v1/categories/customer/{cifId}/available
     * Get categories available for budgeting
     */
    @GetMapping("/customer/{cifId}/available")
    @Operation(summary = "Get Available Categories",
            description = "Get categories that have transactions for this customer")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableCategories(
            @PathVariable @NotBlank String cifId) {

        log.info("GET /api/v1/categories/customer/{}/available", cifId);

        List<String> categories = categoryService.getAvailableCategories(cifId);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}

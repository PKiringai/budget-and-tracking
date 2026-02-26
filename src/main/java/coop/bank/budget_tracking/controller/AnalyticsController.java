package coop.bank.budget_tracking.controller;


import coop.bank.budget_tracking.dto.response.*;
import coop.bank.budget_tracking.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Analytics Controller - REST API for spending analytics
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Analytics", description = "Spending analytics and insights APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/v1/analytics/customer/{cifId}/spending
     * Get comprehensive spending analytics
     */
    @GetMapping("/customer/{cifId}/spending")
    @Operation(summary = "Get Spending Analytics",
            description = "Get comprehensive spending analysis for a customer")
    public ResponseEntity<ApiResponse<SpendingAnalyticsResponse>> getSpendingAnalytics(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/v1/analytics/customer/{}/spending from {} to {}",
                cifId, startDate, endDate);

        SpendingAnalyticsResponse analytics = analyticsService
                .getSpendingAnalytics(cifId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    /**
     * GET /api/v1/analytics/customer/{cifId}/category-breakdown
     * Get category-wise spending breakdown
     */
    @GetMapping("/customer/{cifId}/category-breakdown")
    @Operation(summary = "Get Category Breakdown",
            description = "Get spending breakdown by category")
    public ResponseEntity<ApiResponse<List<CategorySpendingDTO>>> getCategoryBreakdown(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Start date (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime startDate,
            @Parameter(description = "End date (ISO format)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime endDate) {

        log.info("GET /api/v1/analytics/customer/{}/category-breakdown", cifId);

        List<CategorySpendingDTO> breakdown = analyticsService
                .getCategoryBreakdown(cifId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(breakdown));
    }

    /**
     * GET /api/v1/analytics/customer/{cifId}/monthly-trend
     * Get monthly income/expense trend
     */
    @GetMapping("/customer/{cifId}/monthly-trend")
    @Operation(summary = "Get Monthly Trend",
            description = "Get monthly income and expense trends")
    public ResponseEntity<ApiResponse<List<MonthlyTrendDTO>>> getMonthlyTrend(
            @PathVariable @NotBlank String cifId,
            @Parameter(description = "Number of months")
            @RequestParam(defaultValue = "6") int months) {

        log.info("GET /api/v1/analytics/customer/{}/monthly-trend - {} months",
                cifId, months);

        List<MonthlyTrendDTO> trend = analyticsService.getMonthlyTrend(cifId, months);

        return ResponseEntity.ok(ApiResponse.success(trend));
    }

    /**
     * GET /api/v1/analytics/customer/{cifId}/insights
     * Get personalized spending insights
     */
    @GetMapping("/customer/{cifId}/insights")
    @Operation(summary = "Get Insights",
            description = "Get personalized financial insights and recommendations")
    public ResponseEntity<ApiResponse<List<SpendingInsightDTO>>> getInsights(
            @PathVariable @NotBlank String cifId) {

        log.info("GET /api/v1/analytics/customer/{}/insights", cifId);

        List<SpendingInsightDTO> insights = analyticsService.generateInsights(cifId);

        return ResponseEntity.ok(ApiResponse.success(insights));
    }
}

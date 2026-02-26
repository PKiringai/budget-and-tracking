# Budget & Expense Tracking API - Working Guide

**Base URL:** `http://localhost:8080/api/v1`  
**Version:** 1.0.0  
**Date:** February 26, 2026

---

## 🎯 QUICK START

### What You Need to Know

**API Type:** REST  
**Format:** JSON  
**Authentication:** None (to be added later)  
**Test Customer ID:** `20000322`

### Quick Test

```bash
# 1. Check API is running
curl http://localhost:8080/actuator/health

# 2. Get available categories
curl http://localhost:8080/api/v1/categories/customer/20000322/available

# 3. Get customer budgets
curl http://localhost:8080/api/v1/budgets/customer/20000322
```

---

## 📡 ENDPOINTS OVERVIEW

### Summary

| Category | Count | Purpose |
|----------|-------|---------|
| **Budgets** | 7 | Create, manage budgets |
| **Transactions** | 6 | View, filter transactions |
| **Categories** | 3 | Get spending categories |
| **Analytics** | 4 | Spending insights |
| **TOTAL** | **20** | Complete API |

---

## 💰 BUDGETS (7 endpoints)

### 1. Create Budget

```http
POST /api/v1/budgets
Content-Type: application/json

{
  "cifId": "20000322",
  "category": "Food, drinks",
  "budgetAmount": 15000.00,
  "periodType": "MONTHLY",
  "startDate": "2026-03-01"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "cifId": "20000322",
    "category": "Food, drinks",
    "budgetAmount": 15000.00,
    "currentSpending": 0.00,
    "remainingBudget": 15000.00,
    "percentageUsed": 0,
    "daysRemaining": 4
  }
}
```

**Period Types:** `DAILY`, `WEEKLY`, `MONTHLY`, `QUARTERLY`, `YEARLY`, `CUSTOM`

---

### 2. Get Customer Budgets

```http
GET /api/v1/budgets/customer/{cifId}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "category": "Food, drinks",
      "budgetAmount": 15000.00,
      "currentSpending": 5230.50,
      "remainingBudget": 9769.50,
      "percentageUsed": 35,
      "daysRemaining": 4
    }
  ]
}
```

---

### 3. Get Budget Summary

```http
GET /api/v1/budgets/customer/{cifId}/summary?startDate=2026-03-01&endDate=2026-03-31
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalBudget": 50000.00,
    "totalSpending": 42150.75,
    "remainingBudget": 7849.25,
    "budgetUtilizationPercentage": 84,
    "categoryBreakdown": [
      {
        "category": "Food, drinks",
        "budgetAmount": 15000.00,
        "spending": 12500.00,
        "remaining": 2500.00,
        "percentageUsed": 83,
        "status": "WARNING"
      }
    ]
  }
}
```

**Status:** `ON_TRACK` (green), `WARNING` (orange), `EXCEEDED` (red)

---

### 4. Update Budget

```http
PUT /api/v1/budgets/{id}
Content-Type: application/json

{
  "budgetAmount": 18000.00,
  "alertThreshold80": false
}
```

**Note:** All fields optional, only include what you want to change.

---

### 5. Delete Budget

```http
DELETE /api/v1/budgets/{id}
```

Soft deletes (sets `isActive = false`).

---

### 6. Get Budget by ID

```http
GET /api/v1/budgets/{id}
```

---

### 7. Check Alerts

```http
POST /api/v1/budgets/customer/{cifId}/check-alerts
```

Manually triggers alert evaluation.

---

## 💳 TRANSACTIONS (6 endpoints)

### 1. Get Transactions (Paginated)

```http
GET /api/v1/transactions/customer/{cifId}?page=0&size=20
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "transactionId": "TXN123456789",
        "transactionDate": "2026-02-25T14:30:00+03:00",
        "merchant": "NAIVAS SUPERMARKET",
        "amount": 2350.00,
        "transactionType": "DEBIT",
        "category": "Food, drinks",
        "confidence": 0.95
      }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "pageNumber": 0
  }
}
```

**Pagination:** `page` (0-indexed), `size` (default: 20, max: 100)

---

### 2. Filter Transactions

```http
POST /api/v1/transactions/filter
Content-Type: application/json

{
  "cifId": "20000322",
  "category": "Food, drinks",
  "startDate": "2026-02-01",
  "endDate": "2026-02-28",
  "page": 0,
  "size": 20
}
```

**Note:** `category` is optional (omit for all categories)

---

### 3. Get Uncategorized Transactions

```http
GET /api/v1/transactions/customer/{cifId}/uncategorized?months=6
```

Returns transactions with `category = null`

---

### 4. Calculate Category Spending

```http
GET /api/v1/transactions/customer/{cifId}/category/{category}/spending?startDate=2026-02-01T00:00:00Z&endDate=2026-02-28T23:59:59Z
```

**Example:**
```
GET /api/v1/transactions/customer/20000322/category/Food%2C%20drinks/spending?startDate=2026-02-01T00:00:00Z&endDate=2026-02-28T23:59:59Z
```

**Response:**
```json
{
  "success": true,
  "data": 14250.00
}
```

**Note:** URL-encode category names

---

### 5. Re-categorize Transaction

```http
PATCH /api/v1/transactions/{id}/category?category=Shopping
```

Manually changes transaction category (sets confidence to 1.0)

---

### 6. Get Transaction by ID

```http
GET /api/v1/transactions/{id}
```

---

## 🏷️ CATEGORIES (3 endpoints)

### 1. Get All Categories

```http
GET /api/v1/categories
```

**Response:**
```json
{
  "success": true,
  "data": [
    "Food, drinks",
    "Transport",
    "Shopping",
    "Health",
    "Communication, tech"
  ]
}
```

---

### 2. Get Customer Categories (with stats)

```http
GET /api/v1/categories/customer/{cifId}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "categoryName": "Food, drinks",
      "totalSpending": 45230.50,
      "hasBudget": true
    },
    {
      "categoryName": "Transport",
      "totalSpending": 28500.00,
      "hasBudget": false
    }
  ]
}
```

---

### 3. Get Available Categories

```http
GET /api/v1/categories/customer/{cifId}/available
```

**Use this to populate budget creation dropdowns** - only returns categories with transactions.

---

## 📊 ANALYTICS (4 endpoints)

### 1. Get Spending Analytics

```http
GET /api/v1/analytics/customer/{cifId}/spending?startDate=2026-02-01&endDate=2026-02-28
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalSpending": 38500.00,
    "averageDailySpending": 1375.00,
    "topCategory": "Food, drinks",
    "categoryBreakdown": [
      {
        "category": "Food, drinks",
        "totalAmount": 14250.00
      }
    ],
    "monthlyTrend": [
      {
        "month": "2026-02-01",
        "income": 85000.00,
        "expenses": 38500.00
      }
    ],
    "topMerchants": [
      "NAIVAS SUPERMARKET",
      "CARREFOUR"
    ],
    "insights": [
      {
        "type": "SPEND_PATTERN",
        "message": "Your highest spending category is Food, drinks (37%)",
        "recommendation": "Consider setting a budget",
        "priority": "HIGH"
      }
    ]
  }
}
```

---

### 2. Get Category Breakdown

```http
GET /api/v1/analytics/customer/{cifId}/category-breakdown?startDate=2026-02-01T00:00:00Z&endDate=2026-02-28T23:59:59Z
```

---

### 3. Get Monthly Trend

```http
GET /api/v1/analytics/customer/{cifId}/monthly-trend?months=6
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "month": "2026-02-01",
      "income": 85000.00,
      "expenses": 38500.00
    }
  ]
}
```

---

### 4. Get Insights

```http
GET /api/v1/analytics/customer/{cifId}/insights
```

---

## ⚠️ ERROR RESPONSES

### Standard Format

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2026-02-26T16:30:00+03:00",
  "status": 400
}
```

### Validation Errors

```json
{
  "success": false,
  "message": "Validation failed",
  "status": 400,
  "errors": [
    {
      "field": "budgetAmount",
      "message": "Budget amount must be greater than 0",
      "rejectedValue": -100
    }
  ]
}
```

### Status Codes

- **200** OK - Success (GET, PUT, PATCH, DELETE)
- **201** Created - Success (POST)
- **400** Bad Request - Validation error
- **404** Not Found - Resource not found
- **500** Server Error - Backend error

---

## 🧪 TESTING

### Using cURL

```bash
# Get categories
curl http://localhost:8080/api/v1/categories/customer/20000322/available

# Create budget
curl -X POST http://localhost:8080/api/v1/budgets \
  -H "Content-Type: application/json" \
  -d '{
    "cifId": "20000322",
    "category": "Food, drinks",
    "budgetAmount": 15000,
    "periodType": "MONTHLY",
    "startDate": "2026-03-01"
  }'

# Get budgets
curl http://localhost:8080/api/v1/budgets/customer/20000322

# Get summary
curl "http://localhost:8080/api/v1/budgets/customer/20000322/summary?startDate=2026-03-01&endDate=2026-03-31"

# Get analytics
curl "http://localhost:8080/api/v1/analytics/customer/20000322/spending?startDate=2026-02-01&endDate=2026-02-28"
```

### Using Swagger UI

Open: `http://localhost:8080/swagger-ui.html`

- ✅ Interactive API documentation
- ✅ Try-it-out functionality
- ✅ Request/response examples
- ✅ Schema definitions

---

## 💻 FRONTEND INTEGRATION

### Key Points

**1. Date Formats:**
- Query params: `YYYY-MM-DD` (e.g., "2026-03-01")
- Request/Response: ISO 8601 (e.g., "2026-02-26T16:30:00+03:00")

**2. Currency:**
- Always 2 decimal places: `15000.00`
- Amount in KES (Kenyan Shillings)

**3. Pagination:**
- Page numbers are 0-indexed
- Default size: 20, max: 100
- Check `totalPages` and `last` field

**4. URL Encoding:**
- Encode category names: `Food, drinks` → `Food%2C%20drinks`

**5. Status Colors:**
```
ON_TRACK  → Green  (#10B981) → < 80%
WARNING   → Orange (#F59E0B) → 80-99%
EXCEEDED  → Red    (#EF4444) → ≥ 100%
```

### Sample Flow: Dashboard Load

```javascript
// 1. Get customer budgets
GET /api/v1/budgets/customer/{cifId}

// 2. Get budget summary for current month
GET /api/v1/budgets/customer/{cifId}/summary?startDate=2026-03-01&endDate=2026-03-31

// 3. Get spending analytics
GET /api/v1/analytics/customer/{cifId}/spending?startDate=2026-03-01&endDate=2026-03-31

// 4. Get insights
GET /api/v1/analytics/customer/{cifId}/insights
```

### Sample Flow: Create Budget

```javascript
// 1. Get available categories for dropdown
GET /api/v1/categories/customer/{cifId}/available

// 2. Create budget with user input
POST /api/v1/budgets
{
  "cifId": "20000322",
  "category": "selected_category",
  "budgetAmount": user_input,
  "periodType": "MONTHLY",
  "startDate": "2026-03-01"
}

// 3. Refresh budget list
GET /api/v1/budgets/customer/{cifId}
```

---

## 🔧 BACKEND NOTES

### Running the Application

```bash
# Set environment variables
export DB_USERNAME=yfs_service
export DB_PASSWORD=your_password

# Run
mvn clean spring-boot:run

# Or build JAR
mvn clean package -DskipTests
java -jar target/budget-tracking-0.0.1-SNAPSHOT.jar
```

### Database Connection

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://172.16.19.33:5999/yfsdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

### Key Tables

- `transactions` - Existing transaction data
- `budgets` - Customer budgets
- `budget_alerts` - Budget threshold alerts
- `spending_insights` - AI-generated insights
- `flyway_schema_history` - Migration history

### Flyway Migrations

Located in: `src/main/resources/db/migration/`

```
V1__Create_Budget_Tables.sql  ← Creates new tables
```

### Endpoints Implementation

```
src/main/java/coop/bank/budget_tracking/
├── controller/
│   ├── BudgetController.java
│   ├── TransactionController.java
│   ├── CategoryController.java
│   └── AnalyticsController.java
├── service/
│   ├── BudgetService.java
│   ├── TransactionService.java
│   ├── CategoryService.java
│   └── AnalyticsService.java
├── repository/
│   ├── BudgetRepository.java
│   ├── TransactionRepository.java
│   └── BudgetAlertRepository.java
├── entity/
│   ├── Budget.java
│   ├── Transaction.java
│   └── BudgetAlert.java
└── dto/
    ├── request/
    └── response/
```

---

## 🎯 COMMON USE CASES

### 1. Dashboard Load
```
GET /budgets/customer/{cifId}
GET /budgets/customer/{cifId}/summary?startDate=...&endDate=...
GET /analytics/customer/{cifId}/spending?startDate=...&endDate=...
GET /analytics/customer/{cifId}/insights
```

### 2. Create New Budget
```
GET /categories/customer/{cifId}/available
POST /budgets
```

### 3. View Transactions
```
GET /transactions/customer/{cifId}?page=0&size=20
POST /transactions/filter (with category/date filters)
```

### 4. Budget Analysis
```
GET /budgets/customer/{cifId}/summary?startDate=...&endDate=...
GET /analytics/customer/{cifId}/category-breakdown?startDate=...&endDate=...
```

### 5. Spending Trends
```
GET /analytics/customer/{cifId}/monthly-trend?months=6
GET /analytics/customer/{cifId}/insights
```

---

## 📋 DATA REFERENCE

### Sample Customer ID
```
20000322
```

### Sample Categories
```
"Food, drinks"
"Transport"
"Shopping"
"Health"
"Communication, tech"
"Entertainment"
"Bills & Utilities"
"Other"
```

### Period Types
```
DAILY
WEEKLY
MONTHLY
QUARTERLY
YEARLY
CUSTOM
```

### Transaction Types
```
DEBIT
CREDIT
```

### Budget Status
```
ON_TRACK   - < 80% spent
WARNING    - 80-99% spent
EXCEEDED   - 100%+ spent
```

### Insight Types
```
SPEND_PATTERN
SAVINGS_TIP
BUDGET_RECOMMENDATION
```

### Priority Levels
```
LOW
MEDIUM
HIGH
CRITICAL
```

---

## 🚀 QUICK START CHECKLIST

### Frontend Developers
- [ ] API is running on localhost:8080
- [ ] Test connectivity with Swagger UI
- [ ] Get available categories endpoint
- [ ] Create budget endpoint works
- [ ] Get customer budgets returns data
- [ ] Understand pagination structure
- [ ] Know how to format dates
- [ ] Understand error response format

### Backend Developers
- [ ] Database connected
- [ ] Flyway migrations applied
- [ ] All 20 endpoints working
- [ ] Swagger UI accessible
- [ ] Error handling implemented
- [ ] Logging configured
- [ ] Sample data available for testing

---

## 🔗 RESOURCES

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs
- **Health Check:** http://localhost:8080/actuator/health

---

## 📞 SUPPORT

**Backend Developer:** Patrick Kiringai  
**Email:** pkiringai@co-opbank.co.ke  
**JIRA:** BET-PROJECT

---

## 📌 QUICK TIPS

✅ **Always check `success` field in response**  
✅ **Use Swagger UI for testing first**  
✅ **URL-encode category names**  
✅ **Pagination is 0-indexed**  
✅ **Dates: YYYY-MM-DD for query params**  
✅ **Currency: Always 2 decimal places**  
✅ **Status codes: 200/201 = success, 400 = bad input, 404 = not found**

---

**Last Updated:** February 26, 2026  
**Version:** 1.0.0  
**Status:** Ready for Integration
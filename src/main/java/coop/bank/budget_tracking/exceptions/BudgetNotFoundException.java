package coop.bank.budget_tracking.exceptions;

/**
 * Exception thrown when a budget is not found
 */

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(String message) {
        super(message);
    }

    public BudgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

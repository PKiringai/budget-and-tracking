package coop.bank.budget_tracking.exceptions;

public class InvalidBudgetException extends RuntimeException {
    public InvalidBudgetException(String message) {
        super(message);
    }

    public InvalidBudgetException(String message, Throwable cause) {
        super(message, cause);
    }
}

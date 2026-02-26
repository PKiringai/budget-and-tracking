package coop.bank.budget_tracking.exceptions;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<APIError> handleBudgetNotFound(
            BudgetNotFoundException ex, WebRequest request) {

        log.error("Budget not found: {}", ex.getMessage());

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidBudgetException.class)
    public ResponseEntity<APIError> handleInvalidBudget(
            InvalidBudgetException ex, WebRequest request) {

        log.error("Invalid budget: {}", ex.getMessage());

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<APIError> handleTransactionNotFound(
            TransactionNotFoundException ex, WebRequest request) {

        log.error("Transaction not found: {}", ex.getMessage());

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Validation failed: {}", ex.getMessage());

        List<APIError.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    APIError.ValidationError validationError = new APIError.ValidationError();
                    validationError.setField(fieldError.getField());
                    validationError.setMessage(fieldError.getDefaultMessage());
                    validationError.setRejectedValue(fieldError.getRejectedValue());
                    return validationError;
                })
                .collect(Collectors.toList());

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage("Validation failed");
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIError> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        log.error("Constraint violation: {}", ex.getMessage());

        List<APIError.ValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    APIError.ValidationError validationError = new APIError.ValidationError();
                    validationError.setField(violation.getPropertyPath().toString());
                    validationError.setMessage(violation.getMessage());
                    validationError.setRejectedValue(violation.getInvalidValue());
                    return validationError;
                })
                .collect(Collectors.toList());

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage("Validation failed");
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIError> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: ", ex);

        APIError error = new APIError();
        error.setSuccess(false);
        error.setMessage("An unexpected error occurred. Please try again later.");
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(extractPath(request));
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

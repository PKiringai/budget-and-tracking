package coop.bank.budget_tracking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Generic API response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String statusCode;
    private String statusDescription;
    private String messageCode;
    private String messageDescription;
    private String messageId;

    private T data;
    private List<ValidationError> errors;

    private OffsetDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .statusCode("S_001")
                .statusDescription("SUCCESS")
                .messageCode("00")
                .messageDescription("Operation successful")
                .messageId(UUID.randomUUID().toString())
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .statusCode("S_001")
                .statusDescription("SUCCESS")
                .messageCode("00")
                .messageDescription(message)
                .messageId(UUID.randomUUID().toString())
                .data(data)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(
            String statusCode,
            String messageCode,
            String messageDescription,
            List<ValidationError> errors) {

        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .statusDescription("ERROR")
                .messageCode(messageCode)
                .messageDescription(messageDescription)
                .messageId(UUID.randomUUID().toString())
                .errors(errors)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(
            String statusCode,
            String messageCode,
            String messageDescription) {

        return error(statusCode, messageCode, messageDescription, null);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
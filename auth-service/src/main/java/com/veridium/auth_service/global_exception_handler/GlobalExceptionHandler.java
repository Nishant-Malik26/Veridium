package com.veridium.auth_service.global_exception_handler;

import com.veridium.auth_service.constants.ErrorMessages;
import com.veridium.auth_service.dto.ApiResponse;
import com.veridium.auth_service.dto.ValidationErrorResponseDto;
import com.veridium.auth_service.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

import static com.veridium.auth_service.constants.ValidationMessages.DTO_VALIDATION_FAILED;
import static com.veridium.auth_service.constants.ValidationMessages.PARAM_VALIDATION_FAILED;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleDtoValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponseDto response = new ValidationErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                DTO_VALIDATION_FAILED,
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleParameterValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            // Extracts the specific parameter name from the property path
            String propertyPath = violation.getPropertyPath().toString();
            String parameterName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String errorMessage = violation.getMessage();

            errors.put(parameterName, errorMessage);
        });

        ValidationErrorResponseDto response = new ValidationErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                PARAM_VALIDATION_FAILED,
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = TokenNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenNotFoundException(TokenNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<String>builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler({
            OldPasswordNotCorrect.class,
            NewPasswordCannotBeSameAsOldPassword.class,
            PasswordNewPasswordNotMatch.class
    })
    public ResponseEntity<ApiResponse<Void>> handlePasswordExceptions(RuntimeException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                                                .success(false)
                                                .message(ex.getMessage())
                                                .data(null)
                                                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({BadCredentialsException.class, InvalidCredentialsException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(Exception ex) {
        return ResponseEntity.badRequest()
                             .body(new ApiResponse<>(false, ErrorMessages.EMAIL_OR_PASSWORD_WRONG, null));
    }
}

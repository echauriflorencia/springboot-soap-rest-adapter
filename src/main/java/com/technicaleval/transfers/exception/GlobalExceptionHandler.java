package com.technicaleval.transfers.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.xml.ws.WebServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponse> handleValidationException(Exception ex, HttpServletRequest request) {
        List<ApiErrorDetail> errors = buildValidationErrors(ex, request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            SoapClientException.class,
            WebServiceException.class
    })
    public ResponseEntity<ApiErrorResponse> handleSoapException(Exception ex, HttpServletRequest request) {
        List<ApiErrorDetail> errors = List.of(new ApiErrorDetail(
                "SOAP_CONNECTION_ERROR",
                safeMessage(ex, "SOAP request failed"),
                request.getRequestURI()
        ));
        return buildResponse(HttpStatus.BAD_GATEWAY, "SOAP integration failed", errors);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        List<ApiErrorDetail> errors = List.of(new ApiErrorDetail(
                "INTERNAL_ERROR",
                safeMessage(ex, "Unexpected error"),
                request.getRequestURI()
        ));
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", errors);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, List<ApiErrorDetail> errors) {
        ApiErrorResponse body = new ApiErrorResponse(
                status.name(),
                UUID.randomUUID().toString(),
                message,
                errors
        );
        return ResponseEntity.status(status).body(body);
    }

    private List<ApiErrorDetail> buildValidationErrors(Exception ex, String requestPath) {
        List<ApiErrorDetail> errors = new ArrayList<>();

        if (ex instanceof ConstraintViolationException violationException) {
            for (ConstraintViolation<?> violation : violationException.getConstraintViolations()) {
                errors.add(new ApiErrorDetail(
                        "VALIDATION_ERROR",
                        violation.getMessage(),
                        requestPath
                ));
            }
            return errors;
        }

        if (ex instanceof MissingServletRequestParameterException missingParam) {
            errors.add(new ApiErrorDetail(
                    "MISSING_PARAMETER",
                    "Required request parameter '" + missingParam.getParameterName() + "' is not present",
                    requestPath
            ));
            return errors;
        }

        if (ex instanceof MethodArgumentTypeMismatchException typeMismatch) {
            errors.add(new ApiErrorDetail(
                    "INVALID_PARAMETER",
                    "Invalid value for parameter '" + typeMismatch.getName() + "'",
                    requestPath
            ));
            return errors;
        }

        if (ex instanceof MissingPathVariableException missingPathVariable) {
            errors.add(new ApiErrorDetail(
                    "MISSING_PATH_VARIABLE",
                    "Required path variable '" + missingPathVariable.getVariableName() + "' is not present",
                    requestPath
            ));
            return errors;
        }

        if (ex instanceof MethodArgumentNotValidException notValidException) {
            notValidException.getBindingResult().getFieldErrors().forEach(fieldError -> errors.add(new ApiErrorDetail(
                    "VALIDATION_ERROR",
                    fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid request",
                    requestPath
            )));
            notValidException.getBindingResult().getGlobalErrors().forEach(globalError -> errors.add(new ApiErrorDetail(
                    "VALIDATION_ERROR",
                    globalError.getDefaultMessage() != null ? globalError.getDefaultMessage() : "Invalid request",
                    requestPath
            )));
        }

        if (errors.isEmpty()) {
            errors.add(new ApiErrorDetail("VALIDATION_ERROR", safeMessage(ex, "Invalid request"), requestPath));
        }

        return errors;
    }

    private String safeMessage(Exception ex, String fallback) {
        return ex.getMessage() == null || ex.getMessage().isBlank() ? fallback : ex.getMessage();
    }
}

package com.wipro.pds.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntitlementExhaustedException.class)
    public ResponseEntity<ErrorResponse> handleEntitlementExhausted(EntitlementExhaustedException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "ENTITLEMENT_EXHAUSTED",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "INSUFFICIENT_STOCK",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "BAD_REQUEST",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        ErrorResponse err = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_FAILED",
            msg,
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "ACCESS_DENIED",
            "Access denied: You do not have permission to execute this operation.",
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "INVALID_CREDENTIALS",
            "Invalid username or password.",
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        
        // Oracle specific error mapping
        if (rootMsg != null && rootMsg.contains("ORA-20001")) {
            return handleEntitlementExhausted(new EntitlementExhaustedException("Ration card entitlement quota has been exhausted."), request);
        } else if (rootMsg != null && rootMsg.contains("ORA-20002")) {
            return handleInsufficientStock(new InsufficientStockException("Insufficient stock available in fair-price shop."), request);
        } else if (rootMsg != null && rootMsg.contains("ORA-00001")) {
            ErrorResponse err = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "UNIQUE_CONSTRAINT_VIOLATION",
                "A duplicate record already exists in the system.",
                request.getRequestURI()
            );
            return new ResponseEntity<>(err, HttpStatus.CONFLICT);
        }

        ErrorResponse err = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "DATA_INTEGRITY_VIOLATION",
            "Database constraint violation: " + rootMsg,
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        ErrorResponse err = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred.",
            request.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

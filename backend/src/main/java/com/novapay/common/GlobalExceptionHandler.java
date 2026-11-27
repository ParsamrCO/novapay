package com.novapay.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(body(400, "VALIDATION_ERROR", fields));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleState(IllegalStateException ex) {
        return switch (ex.getMessage()) {
            case "EMAIL_TAKEN" ->
                ResponseEntity.status(HttpStatus.CONFLICT).body(body(409, "EMAIL_TAKEN", null));
            case "BAD_CREDENTIALS" ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(401, "BAD_CREDENTIALS", null));
            case "INVALID_REFRESH" ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(401, "INVALID_REFRESH", null));
            case "ACCOUNT_INACTIVE" ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(body(403, "ACCOUNT_INACTIVE", null));
            case "INVALID_RESET" ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(400, "INVALID_RESET", null));
            case "ACCOUNT_NOT_FOUND", "TX_NOT_FOUND" ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(404, ex.getMessage(), null));
            case "ACCOUNT_FORBIDDEN", "TX_FORBIDDEN" ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(body(403, ex.getMessage(), null));
            case "ACCOUNT_NAME_TAKEN" ->
                ResponseEntity.status(HttpStatus.CONFLICT).body(body(409, ex.getMessage(), null));
            case "ACCOUNT_CLOSED", "ACCOUNT_ALREADY_CLOSED", "ACCOUNT_NONZERO_BALANCE",
                "LEDGER_MIN_ENTRIES", "LEDGER_INVALID_AMOUNT", "LEDGER_IMBALANCE",
                "TX_ALREADY_REVERSED" ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(400, ex.getMessage(), null));
            default ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(400, ex.getMessage(), null));
        };
    }

    private Map<String, Object> body(int status, String code, Object details) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status);
        m.put("code", code);
        if (details != null) m.put("details", details);
        return m;
    }
}
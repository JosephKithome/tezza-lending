package com.tezza.lending.exception;

import com.tezza.lending.shared.RequestContext;
import com.tezza.lending.shared.ResponsePayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponsePayload> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponsePayload.error(
                        RequestContext.requestId(),
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        null));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ResponsePayload> businessRule(BusinessRuleException ex) {
        return ResponseEntity.badRequest()
                .body(ResponsePayload.error(
                        RequestContext.requestId(),
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponsePayload> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (left, right) -> left));
        return ResponseEntity.badRequest()
                .body(ResponsePayload.error(
                        RequestContext.requestId(),
                        "Validation failed",
                        HttpStatus.BAD_REQUEST.value(),
                        errors));
    }
}

package com.example.accesa.exception;

import com.example.accesa.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class FallbackHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, null, "Invalid path or resource: " + ex.getRequestURL())
        );
    }
}
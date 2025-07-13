// src/main/java/com/drip/competitionengine/exception/GlobalExceptionHandler.java
package com.drip.competitionengine.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ---------- 400 Bad Request ---------- */

    @ExceptionHandler({
        NotEnoughParticipantsException.class,
        MethodArgumentTypeMismatchException.class,
        ConstraintViolationException.class
    })
    public ResponseEntity<?> badRequest(Exception ex) {
        return json(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /* ---------- 404 ---------- */

    @ExceptionHandler({
        EntityNotFoundException.class,
        NoHandlerFoundException.class
    })
    public ResponseEntity<?> notFound(Exception ex) {
        return json(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /* ---------- 403 ---------- */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> forbidden(AccessDeniedException ex) {
        return json(HttpStatus.FORBIDDEN, "Forbidden - need admin rights");
    }

    /* ---------- 405 ---------- */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return json(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    /* ---------- 500 ---------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> internal(Exception ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /* ---------- helper ---------- */

    private ResponseEntity<Map<String, Object>> json(HttpStatus st, String msg) {
        return ResponseEntity.status(st).body(Map.of(
                "timestamp", Instant.now(),
                "status",    st.value(),
                "error",     st.getReasonPhrase(),
                "message",   msg
        ));
    }
}

// api/RestExceptionAdvice.java
package com.drip.competitionengine.api;

import com.drip.competitionengine.api.dto.ApiErrorDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice @Slf4j
public class RestExceptionAdvice {

    @ExceptionHandler({ IllegalArgumentException.class,
                        MethodArgumentNotValidException.class })
    public ResponseEntity<ApiErrorDto> badRequest(Exception e){
        return build(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({ IllegalStateException.class })
    public ResponseEntity<ApiErrorDto> unprocessable(Exception e){
        return build(HttpStatus.UNPROCESSABLE_ENTITY, e);
    }

    @ExceptionHandler({ NoSuchElementException.class,
                        EntityNotFoundException.class })
    public ResponseEntity<ApiErrorDto> notFound(Exception e){
        return build(HttpStatus.NOT_FOUND, e);
    }

    private ResponseEntity<ApiErrorDto> build(HttpStatus s, Exception e){
        log.warn("{}. {}", s.value(), e.getMessage());
        return ResponseEntity.status(s)
                .body(new ApiErrorDto(s.value(),
                                      s.getReasonPhrase(),
                                      e.getMessage(),
                                      Instant.now()));
    }
}

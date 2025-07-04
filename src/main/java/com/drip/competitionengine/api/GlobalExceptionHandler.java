package com.drip.competitionengine.api;

import com.drip.competitionengine.api.dto.ReasonErrorDto;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ReasonErrorDto> illegal(IllegalArgumentException ex){
     return ResponseEntity.unprocessableEntity().body(new ReasonErrorDto(ex.getMessage()));
  }
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ReasonErrorDto> notFound(NoSuchElementException ex){
     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReasonErrorDto(ex.getMessage()));
  }
}

package com.drip.competitionengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)   // 400
public class NotEnoughParticipantsException extends RuntimeException {
    public NotEnoughParticipantsException() {
        super("Need ≥2 participants");
    }
}

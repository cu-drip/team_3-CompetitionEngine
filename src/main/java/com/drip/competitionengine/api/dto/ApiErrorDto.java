// api/dto/ApiErrorDto.java
package com.drip.competitionengine.api.dto;

import java.time.Instant;

public record ApiErrorDto(
        int    status,
        String error,
        String message,
        Instant timestamp
) { }

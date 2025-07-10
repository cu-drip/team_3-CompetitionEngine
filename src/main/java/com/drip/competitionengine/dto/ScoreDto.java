// api/dto/ScoreDto.java
package com.drip.competitionengine.dto;

import java.util.UUID;

public record ScoreDto(
        UUID   matchId,
        UUID   participantId,
        int    points
) { }

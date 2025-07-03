// api/dto/ScoreDto.java
package com.drip.competitionengine.api;

import java.util.UUID;

public record ScoreDto(
        UUID   matchId,
        UUID   participantId,
        int    points
) { }

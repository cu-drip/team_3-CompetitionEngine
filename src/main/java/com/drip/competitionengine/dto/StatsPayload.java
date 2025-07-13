// src/main/java/com/drip/competitionengine/stats/dto/StatsPayload.java
package com.drip.competitionengine.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class StatsPayload {
    private UUID   tournamentId;
    private String sport;           // football | boxing | …
    private String tournamentType;  // olympic | round_robin | …
    private List<StatsMatch> matches;
}


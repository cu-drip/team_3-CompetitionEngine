package com.drip.competitionengine.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StatsMatch {
    private UUID                 matchId;
    private List<StatsParticipant> participants;
}

package com.drip.competitionengine.dto;

import com.drip.competitionengine.model.MatchStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class MatchDtoOut {
    @JsonProperty("id")
    private UUID   matchId;
    private UUID   tournamentId;
    private Instant plannedStartTime;
    private Instant plannedEndTime;
    private Instant startedAt;
    private Instant finishedAt;

    private List<MatchParticipantDto> participants;
    private UUID winner;
    private MatchStatus status;
    private List<UUID> parentMatches;

    /* служебное поле для GET /matches/{id} */
    private String canonical;
}

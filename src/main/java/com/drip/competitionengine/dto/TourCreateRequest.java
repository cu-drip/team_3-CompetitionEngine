package com.drip.competitionengine.dto;

import com.drip.competitionengine.model.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class TourCreateRequest {

    private UUID id;
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Sport sport;

    @NotNull
    private TournamentType typeTournament;

    @NotNull
    private GroupType typeGroup;

    @NotNull @Future
    private Instant startTime;

    @NotNull @Min(1)
    private Integer maxParticipants;

    @NotNull
    private String place;
    @NotNull
    private List<UUID> participants;
    @NotNull
    private UUID organizerId;
}

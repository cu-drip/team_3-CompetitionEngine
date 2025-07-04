package com.drip.competitionengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TournamentRegistrationKey implements java.io.Serializable {
  @Column(name = "tournament_id")
  UUID tournamentId;
  @Column(name = "participant_id")
  UUID participantId;
  @Column(name = "participant_type")
  ParticipantType participantType;
}
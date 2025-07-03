package com.drip.competitionengine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name="tournament_registrations")
@Data @NoArgsConstructor @AllArgsConstructor
public class TournamentRegistration {
  @Column(name = "tournament_id")
  @EmbeddedId TournamentRegistrationKey id;
  @Column(name = "sport")
  @Enumerated(EnumType.STRING) Sport sport;
  @Column(name = "registered_at")
  Instant registeredAt;
  @Column(name = "status")
  @Enumerated(EnumType.STRING) RegistrationStatus status;
}
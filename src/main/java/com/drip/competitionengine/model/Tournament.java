package com.drip.competitionengine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="tournaments")
@Data @NoArgsConstructor @AllArgsConstructor
public class Tournament {
  @Column(name = "id")
  @Id UUID id;
  @Column(name = "title")
  String title;
  @Column(name = "description")
  String description;
  @Column(name = "sport")
  @Enumerated(EnumType.STRING) Sport sport;
  @Column(name = "type_tournament")
  @Enumerated(EnumType.STRING) TournamentType typeTournament;
  @Column(name = "type_group")
  @Enumerated(EnumType.STRING) GroupType typeGroup;
  @Column(name = "matches_number")
  Integer matchesNumber;
  @Column(name = "start_time")
  Instant startTime;
  @Column(name = "created_at")
  Instant createdAt;
  @Column(name = "entry_cost")
  Double entryCost;
  @Column(name = "max_participants")
  Integer maxParticipants;
  @Column(name = "registration_deadline")
  Instant registrationDeadline;
  @Column(name = "place")
  String place;
  @Column(name = "organizer_id")
  UUID organizerId;
  boolean distributed = false; // helper flag
}

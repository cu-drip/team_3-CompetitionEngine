package com.drip.competitionengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {
  UUID matchId;
  UUID tournamentId;
  LocalDateTime startedAt;
  LocalDateTime createdAt;
  Integer position;
  UUID participant1;
  UUID participant2;
  Integer p1Points;
  Integer p2Points;
  UUID winner;
}
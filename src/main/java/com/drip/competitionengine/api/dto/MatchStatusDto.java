package com.drip.competitionengine.api.dto;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor
public class MatchStatusDto {
  Instant startedAt;
  UUID participant1;
  UUID participant2;
  Integer p1Points;
  Integer p2Points;
  UUID winner;
}
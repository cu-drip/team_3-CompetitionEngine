package com.drip.competitionengine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="matches")
@Data @NoArgsConstructor @AllArgsConstructor
public class Match {
  @Id @Column(name="match_id")
  UUID id;
  @Column(name = "tour_id")
  UUID tourId;
  @Column(name = "started_at")
  Instant startedAt;
  @Column(name = "created_at")
  Instant createdAt;
  @Column(name = "position")
  Integer position;
  @Column(name = "partition1_id")
  UUID partition1Id;
  @Column(name = "partition2_id")
  UUID partition2Id;
  @Column(name = "partition1_points")
  Integer partition1Points;
  @Column(name = "partition2_points")
  Integer partition2Points;
  @Column(name = "winner_id")
  UUID winnerId;
  /**
   * Устанавливает очки для указанной партии и возвращает новое значение.
   *
   * @param partitionId идентификатор partition1 или partition2
   * @param points      сколько очков назначить
   * @return            новое значение очков для этой партии
   * @throws IllegalArgumentException если partitionId не совпадает ни с одной из партий
   */
  public int setPartitionPointsById(UUID partitionId, int points) {
    if (partitionId == null) {
      throw new IllegalArgumentException("partitionId must not be null");
    }

    if (partitionId.equals(partition1Id)) {
      this.partition1Points = points;
      return this.partition1Points;
    } else if (partitionId.equals(partition2Id)) {
      this.partition2Points = points;
      return this.partition2Points;
    } else {
      throw new IllegalArgumentException(
              "partitionId " + partitionId + " not found in this match");
    }
  }

  /**
   * Возвращает текущее количество очков для указанной партии.
   *
   * @param partitionId идентификатор partition1 или partition2
   * @return            очки соответствующей партии (может быть null, если ещё не выставлены)
   * @throws IllegalArgumentException если partitionId не совпадает ни с одной из партий
   */
  public Integer getPartitionPointsById(UUID partitionId) {
    if (partitionId == null) {
      throw new IllegalArgumentException("partitionId must not be null");
    }

    if (partitionId.equals(partition1Id)) {
      return partition1Points;
    } else if (partitionId.equals(partition2Id)) {
      return partition2Points;
    } else {
      throw new IllegalArgumentException(
              "partitionId " + partitionId + " not found in this match");
    }
  }

  public boolean isActive() { return startedAt!=null && winnerId==null; }
}
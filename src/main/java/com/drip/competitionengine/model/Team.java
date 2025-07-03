package com.drip.competitionengine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
  @Id
  @Column(name = "id")
  UUID id;
  @Column(name = "name")
  String name;
  @Column(name = "created_at")
  Instant createdAt;
  @Column(name = "avatar")
  String avatar;
}

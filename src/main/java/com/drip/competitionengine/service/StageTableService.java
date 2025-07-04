package com.drip.competitionengine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @RequiredArgsConstructor
class StageTableService {
  private final JdbcTemplate jdbc;

  /**
   * Creates table "stage_<tourId>" if not exists. Columns:
   *  started_at TIMESTAMP,
   *  is_started BOOLEAN,
   *  match_id   UUID PK,
   *  stage      INT,
   *  team1      UUID,
   *  team2      UUID,
   *  winner     UUID
   */
  @Transactional
  public void createStageTable(UUID tourId){
     String tbl = "stage_"+tourId.toString().replace("-", "");
     jdbc.execute("CREATE TABLE IF NOT EXISTS " + tbl + " ("+
             "match_id UUID PRIMARY KEY, " +
             "started_at TIMESTAMP, " +
             "is_started BOOLEAN, " +
             "stage INT, " +
             "team1 UUID, " +
             "team2 UUID, " +
             "winner UUID)");
  }
}
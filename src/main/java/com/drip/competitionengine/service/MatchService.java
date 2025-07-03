package com.drip.competitionengine.service;
import com.drip.competitionengine.model.*;
import com.drip.competitionengine.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class MatchService {
  private final MatchRepository matchRepo;

  @Transactional
  public void startMatch(UUID matchId) {
      Match m = matchRepo.findById(matchId)
              .orElseThrow(() -> new IllegalArgumentException("Match not found"));
      if (m.getStartedAt()!=null) throw new IllegalStateException("Already started");
      m.setStartedAt(Instant.now());
  }

  @Transactional(readOnly=true)
  public Match getStatus(UUID matchId){
      return matchRepo.findById(matchId)
              .orElseThrow(() -> new NoSuchElementException("Match not found"));
  }

  @Transactional
  public int addPoints(UUID matchId, UUID participantId, int n){
      Match m=getActive(matchId);
      m.setPartitionPointsById(participantId, m.getPartitionPointsById(participantId) + n);
      return m.getPartitionPointsById(participantId);
  }
  @Transactional
  public int removePoints(UUID matchId, UUID participantId, int n){
      Match m=getActive(matchId);
      m.setPartitionPointsById(participantId, m.getPartitionPointsById(participantId) - n);
      return m.getPartitionPointsById(participantId);
  }
  @Transactional
  public void setWinner(UUID matchId,UUID winner){
      Match m=getActive(matchId);
      m.setWinnerId(winner);
  }

  private Match getActive(UUID id){
      Match m=matchRepo.findById(id).orElseThrow();
      if(!m.isActive()) throw new IllegalStateException("Match not active");
      return m;
  }
}

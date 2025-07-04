package com.drip.competitionengine.service;

import com.drip.competitionengine.model.*;
import com.drip.competitionengine.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class TournamentService {
  private final TournamentRepository tourRepo;
  private final TournamentRegistrationRepository regRepo;
  private final MatchRepository matchRepo;

  /**
   * Distribute teams for matches: placeholder – just marks tour as distributed
   * and inserts dummy record into matches table (1 match with two first teams).
   */
  @Transactional
  public void distribute(UUID tourId) {
      Tournament t = tourRepo.findById(tourId)
              .orElseThrow(() -> new IllegalArgumentException("Tour not found"));
      if (t.isDistributed()) throw new IllegalStateException("Tour already distributed");

      List<TournamentRegistration> accepted = regRepo.findByIdTournamentIdAndStatus(tourId, RegistrationStatus.ACCEPTED);
      if (accepted.size() < 2) throw new IllegalStateException("Not enough participants");

      // simplistic: first two become a match
      Match m = new Match(UUID.randomUUID(), tourId, null, Instant.now(), 1,
              accepted.get(0).getId().getParticipantId(),
              accepted.get(1).getId().getParticipantId(), 0,0,null);
      matchRepo.save(m);

      t.setDistributed(true);
      tourRepo.save(t);

      // TODO: create dedicated table tourId as per spec (requires dynamic SQL)
  }
}

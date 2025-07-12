package com.drip.competitionengine.repo;

import com.drip.competitionengine.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {}

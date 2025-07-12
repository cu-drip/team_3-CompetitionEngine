package com.drip.competitionengine.repo;

import com.drip.competitionengine.model.Match;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA-репозиторий для сущности Match.
 *
 *  •  findByTournamentId(UUID)        – вернуть все матчи турнира без сортировки
 *  •  findByTournamentId(UUID, Sort)  – тот же запрос, но с ORDER BY, например
 *     Sort.by("position").ascending()
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    List<Match> findByTournamentId(UUID tournamentId);

    List<Match> findByTournamentId(UUID tournamentId, Sort sort);

    @Query("""
           select m
           from Match m
           join m.parentMatches p
           where m.tournament.id = :tourId
             and p = :parentId
           """)
    List<Match> findChildren(@Param("tourId")  UUID tourId,
                             @Param("parentId") UUID parentId);

}

package com.drip.competitionengine.repo;
import com.drip.competitionengine.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration,TournamentRegistrationKey>{
    List<TournamentRegistration> findByIdTournamentIdAndStatus(UUID tourId, RegistrationStatus status);
}
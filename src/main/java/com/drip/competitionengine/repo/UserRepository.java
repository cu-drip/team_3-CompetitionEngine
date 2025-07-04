package com.drip.competitionengine.repo;
import com.drip.competitionengine.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User,UUID>{}

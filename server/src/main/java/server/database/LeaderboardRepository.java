package server.database;

import commons.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Player, Long> {
    List<Player> findTop5ByOrderByScoreDesc();
}


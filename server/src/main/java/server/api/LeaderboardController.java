package server.api;

import commons.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.LeaderboardRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final Random random;
    private final LeaderboardRepository repo;

    /**
     * Constructor of LeaderboardController
     *
     * @param random the Random to be used
     * @param repo   the ActivityRepository to be used
     */
    public LeaderboardController(Random random, LeaderboardRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Returns a Player list containing all Players
     *
     * @return a Player list containing all Players
     */
    @GetMapping(path = {"", "/"})
    public List<Player> getAll() {
        return repo.findAll();
    }

    /**
     * Returns the top 5 Players in the game
     *
     * @return the top 5 Players in the game
     */
    @GetMapping(path = "/top5")
    public List<Player> getTopFive() {
        return repo.findTop5ByOrderByScoreDesc();
    }

    /**
     * Updates the score of a Player if player already exists in the database.
     * Otherwise, saves the player.
     *
     * @param player the Player which gets the score
     * @return ResponseEntity.ok(saved) if the Player is valid,
     * ResponseEntity.badRequest().build() otherwise
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Player> addScore(@RequestBody Player player) {

        if (isNullOrEmpty(player.nickname)) {
            return ResponseEntity.badRequest().build();
        }
        ResponseEntity<Long> result = findByUsername(player.nickname);
        if (result.getStatusCode().equals(HttpStatus.OK)) {
            Player repoPlayer = repo.getById(result.getBody());
            if (player.score > repoPlayer.score) {
                repoPlayer.score = player.score;
            }
            return ResponseEntity.ok(repo.save(repoPlayer));
        } else {
            return ResponseEntity.ok(repo.save(player));
        }
    }

    /**
     * finds a player based on the username
     *
     * @param username the username of the player to look for
     * @return ResponseEntity.ok(player.id) if the player is found,
     * returns ResponseEntity.notFound().build() if otherwise
     */
    @GetMapping(path = {"/{username}"})
    public ResponseEntity<Long> findByUsername(@PathVariable("username") String username) {
        List<Player> list = repo.findAll();
        for (Player player : list) {
            if (player.nickname.equals(username)) {
                return ResponseEntity.ok(player.id);
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Checks if a String is null or empty
     *
     * @param s the String to be checked
     * @return true iff s equals null
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

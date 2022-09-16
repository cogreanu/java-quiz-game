package server.api;

import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import server.database.LeaderboardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext
public class LeaderboardRepositoryTest {
    @Autowired
    LeaderboardRepository repo;

    Player p1;
    Player p2;
    Player p3;
    Player p4;
    Player p5;

    @BeforeEach
    public void setup() {
        p1 = new Player("nickname1");
        p2 = new Player("nickname2");
        p3 = new Player("nickname3");
        p4 = new Player("nickname4");
        p5 = new Player("nickname5");
    }


    @Test
    public void zeroActivitiesTest() {
        List<Player> activities = repo.findAll();
        assertThat(activities).hasSize(0);
    }

    @Test
    public void saveOnePlayerTest() {
        Player saved = repo.save(p1);
        assertThat(saved).isEqualTo(p1);

        List<Player> expected = new ArrayList<>();
        expected.add(p1);

        List<Player> actual = repo.findAll();

        assertThat(actual).hasSize(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findAllTest() {
        repo.save(p1);
        repo.save(p2);
        repo.save(p3);

        List<Player> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);

        List<Player> actual = repo.findAll();
        assertThat(actual).hasSize(3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deleteByIdTest() {
        repo.save(p1);
        Player playerToDelete = repo.save(p2);
        repo.save(p3);

        assertThat(repo.findAll()).hasSize(3);

        repo.deleteById(playerToDelete.id);

        List<Player> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p3);

        List<Player> actual = repo.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deleteByIllegalIdTest() {
        repo.save(p1);
        repo.save(p2);
        repo.save(p3);

        try {
            repo.deleteById(0L);
            fail("Test should throw exception when deleting non-existent ID");
        } catch (EmptyResultDataAccessException e) {
            // Thrown by JPA when accessing illegal index.
            // Proceed to test the rest of the database remains intact:
            List<Player> actual = repo.findAll();

            List<Player> expected = new ArrayList<>();
            expected.add(p1);
            expected.add(p2);
            expected.add(p3);

            assertThat(actual).hasSize(3);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    public void existsByIdTrueTest() {
        Player saved = repo.save(p1);
        assertThat(repo.existsById(saved.id)).isTrue();
    }

    @Test
    public void existsByIdFalseTest() {
        repo.save(p1);
        assertThat(repo.existsById(0L)).isFalse();
    }

    @Test
    public void findByIdTest() {
        Player expected = repo.save(p1);

        Optional<Player> actual = repo.findById(expected.id);
        assertThat(actual).isNotEmpty();
        assertThat(actual).contains(expected);
    }

    @Test
    public void findByIdFailTest() {
        Optional<Player> actual = repo.findById(0L);
        assertThat(actual).isEmpty();
    }

    @Test
    public void getTop5OrderedByScoreTest() {
        p1.score = 300;
        p2.score = 200;
        p3.score = 600;
        p4.score = 500;
        p5.score = 100;

        repo.save(p1);
        repo.save(p2);
        repo.save(p3);
        repo.save(p4);
        repo.save(p5);

        List<Player> actual = repo.findTop5ByOrderByScoreDesc();

        List<Player> expected = new ArrayList<>();
        expected.add(p3);
        expected.add(p4);
        expected.add(p1);
        expected.add(p2);
        expected.add(p5);

        assertThat(actual).hasSize(5);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getTop5OrderedByScoreLessThan5Test() {

        p1.score = 300;
        p2.score = 200;
        p3.score = 500;

        repo.save(p1);
        repo.save(p2);
        repo.save(p3);

        List<Player> actual = repo.findTop5ByOrderByScoreDesc();

        List<Player> expected = new ArrayList<>();
        expected.add(p3);
        expected.add(p1);
        expected.add(p2);

        assertThat(actual).hasSize(3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getTop5OrderedByScoreMoreThan5Test() {
        Player p6 = new Player("nickname6");
        Player p7 = new Player("nickname7");

        p1.score = 300;
        p2.score = 200;
        p3.score = 600;
        p4.score = 500;
        p5.score = 100;
        p6.score = 700;
        p7.score = 150;

        repo.save(p1);
        repo.save(p2);
        repo.save(p3);
        repo.save(p4);
        repo.save(p5);
        repo.save(p6);
        repo.save(p7);

        List<Player> actual = repo.findTop5ByOrderByScoreDesc();

        List<Player> expected = new ArrayList<>();
        expected.add(p6);
        expected.add(p3);
        expected.add(p4);
        expected.add(p1);
        expected.add(p2);

        assertThat(actual).hasSize(5);
        assertThat(actual).isEqualTo(expected);
    }


}

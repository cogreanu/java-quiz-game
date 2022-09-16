package server.api;

import commons.Activity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import server.database.ActivityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext
public class ActivityRepositoryTest {
    @Qualifier("activityRepository")
    @Autowired
    ActivityRepository repo;

    Activity act1;
    Activity act2;
    Activity act3;

    @BeforeEach
    public void setup() {

        act1 = new Activity("ID1", "/image/1", "Title1", 100L, "wikipedia1.com");
        act2 = new Activity("ID2",  "/image/2", "Title2", 200L, "wikipedia2.com");
        act3 = new Activity("ID3",  "/image/3", "Title3", 300L, "wikipedia3.com");
        act1.setQuestionId(1L);
        act2.setQuestionId(2L);
        act3.setQuestionId(3L);
    }

    @Test
    public void zeroActivitiesTest() {
        List<Activity> activities = repo.findAll();
        assertThat(activities).hasSize(0);
    }

    @Test
    public void saveOneActivityTest() {
        Activity saved = repo.save(act1);
        assertThat(saved).isEqualTo(act1);

        List<Activity> expected = new ArrayList<>();
        expected.add(act1);

        List<Activity> actual = repo.findAll();

        assertThat(actual).hasSize(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findAllTest() {
        repo.save(act1);
        repo.save(act2);
        repo.save(act3);

        List<Activity> expected = new ArrayList<>();
        expected.add(act1);
        expected.add(act2);
        expected.add(act3);

        List<Activity> actual = repo.findAll();
        assertThat(actual).hasSize(3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deleteByIdTest() {
        repo.save(act1);
        repo.save(act2);
        repo.save(act3);

        assertThat(repo.findAll()).hasSize(3);

        repo.deleteById(act2.getQuestionId());

        List<Activity> expected = new ArrayList<>();
        expected.add(act1);
        expected.add(act3);

        List<Activity> actual = repo.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deleteByIllegalIdTest() {
        repo.save(act1);
        repo.save(act2);
        repo.save(act3);

        try {
            repo.deleteById(4L);
            fail("Test should throw exception when deleting non-existent ID");
        } catch (EmptyResultDataAccessException e) {
            // Thrown by JPA when accessing illegal index.
            // Proceed to test the rest of the database remains intact:
            List<Activity> actual = repo.findAll();

            List<Activity> expected = new ArrayList<>();
            expected.add(act1);
            expected.add(act2);
            expected.add(act3);

            assertThat(actual).hasSize(3);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    public void existsByIdTrueTest() {
        Activity a = repo.save(act1);
        assertThat(repo.existsById(a.getQuestionId())).isTrue();
    }

    @Test
    public void existsByIdFalseTest() {
        repo.save(act1);
        assertThat(repo.existsById(0L)).isFalse();
    }

    @Test
    public void findByIdTest() {
        Activity expected = repo.save(act1);

        Optional<Activity> actual = repo.findById(expected.getQuestionId());
        assertThat(actual).isNotEmpty();
        assertThat(actual).contains(expected);
    }

    @Test
    public void findByIdFailTest() {
        Optional<Activity> actual = repo.findById(0L);
        assertThat(actual).isEmpty();
    }

}

package server.api;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.ActivityService;
import server.services.DatabaseInitializer;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class ActivityControllerTest {

    private TestActivityRepository repo;
    private ActivityService qs;
    private ActivityController sut;
    private DatabaseInitializer dbInitializer;

    @BeforeEach
    public void setup() {
        repo = new TestActivityRepository();
        qs = new ActivityService(new Random(), repo);
        dbInitializer = new DatabaseInitializer(repo);
        sut = new ActivityController(repo, qs, dbInitializer);
    }

    @Test
    public void cannotAddNullActivity() {
        var actual = sut.add(new Activity());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void addingNonNullActivity() {
        var actual = sut.add(new Activity("ID", "Path", "Title", 1L, "Source"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void addingNullFieldsActivity() {
        var actual = sut.add(new Activity(null, "Path", "Title", 1L, "Source"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        actual = sut.add(new Activity("ID", null, "Title", 1L, "Source"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        actual = sut.add(new Activity("ID", "Path", null, 1L, "Source"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        actual = sut.add(new Activity("ID", "Path", "Title", 1L, null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void getAllActivity() {
        assertEquals(sut.getAll(), new ArrayList<Activity>());
        Activity activity = new Activity("ID", "Path", "Title", 1L, "Source");
        sut.add(activity);
        Activity getActivity = sut.getAll().get(0);
        activity.setQuestionId(0L);
        assertEquals(activity, getActivity);
        sut.add(new Activity("ID", "Path", "Title", 10L, "Source"));
        Activity getActivity2 = sut.getAll().get(1);
        assertNotEquals(activity, getActivity2);
    }

    @Test
    public void putActivity() {
        Activity activity1 = new Activity("ID", "Path", "Title", 1L, "Source");
        Activity activity2 = new Activity("ID2", "Path2", "Title2", 12L, "Source2");
        sut.add(activity1);
        activity1.setQuestionId(0L);
        var resultMSG = sut.put(activity2, 0);
        activity2.setQuestionId(0L);
        Activity getActivity = sut.getAll().get(0);
        assertEquals(OK, resultMSG.getStatusCode());
        assertEquals(1, repo.findAll().size());
        assertNotEquals(activity1, getActivity);
        assertEquals(activity2, getActivity);
    }

    @Test
    public void deleteActivity() {
        Activity activity = new Activity("ID", "Path", "Title", 1L, "Source");
        sut.add(activity);
        var resultMSG = sut.deleteActivity(0);
        assertEquals(OK, resultMSG.getStatusCode());
        assertEquals(0, sut.getAll().size());
    }

    @Test
    public void getRandomNActivities() {
        sut.add(new Activity("ID", "Path", "Title", 1L, "Source"));
        sut.add(new Activity("ID", "Path", "Title", 1L, "Source"));
        sut.add(new Activity("ID", "Path", "Title", 1L, "Source"));
        sut.add(new Activity("ID", "Path", "Title", 1L, "Source"));
        assertEquals(BAD_REQUEST, sut.getRandomN(0).getStatusCode());
        assertEquals(3, sut.getRandomN(3).getBody().size());
    }

    @Test
    public void getEqual() {

    }
}

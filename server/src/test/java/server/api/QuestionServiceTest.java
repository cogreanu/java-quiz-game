package server.api;

import commons.Activity;
import commons.Question;
import commons.QuestionTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.QuestionService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class QuestionServiceTest {

    private List<Activity> activities;

    @BeforeEach
    public void initialisation() {
        activities = new ArrayList<>();
        activities.add(new Activity("ID1", "/image/1", "Title1", 1000L, "wikipedia1.com"));
        activities.add(new Activity("ID2", "/image/2", "Title2", 2000L, "wikipedia2.com"));
        activities.add(new Activity("ID3", "/image/3", "Title3", 4000L, "wikipedia3.com"));
    }

    @Test
    public void validateAnswerMostExpensiveQ() {
        assertEquals(1000, QuestionService.validateAnswer(new Question(activities, QuestionTypes.mostExp), 4000));
        assertEquals(0, QuestionService.validateAnswer(new Question(activities, QuestionTypes.mostExp), 2000));
    }

    @Test
    public void validateAnswerEstimateQ() {
        assertEquals(0, QuestionService.validateAnswer(new Question(activities, QuestionTypes.estimate), 500));
        assertEquals(1000, QuestionService.validateAnswer(new Question(activities, QuestionTypes.estimate), 1000));
        assertEquals(0, QuestionService.validateAnswer(new Question(activities, QuestionTypes.estimate), 1500));
        assertEquals(500, QuestionService.validateAnswer(new Question(activities, QuestionTypes.estimate), 750));
        assertEquals(750, QuestionService.validateAnswer(new Question(activities, QuestionTypes.estimate), 875));
    }

    @Test
    public void validateAnswerMultipleQ() {
        assertEquals(1000, QuestionService.validateAnswer(new Question(activities, QuestionTypes.multiple), 1000));
        assertEquals(0, QuestionService.validateAnswer(new Question(activities, QuestionTypes.multiple), 2000));
    }

    @Test
    public void validateAnswerAlternativeQ() {
        assertEquals(1000, QuestionService.validateAnswer(new Question(activities, QuestionTypes.alternative), 1000));
        assertEquals(0, QuestionService.validateAnswer(new Question(activities, QuestionTypes.alternative), 2000));
    }
}

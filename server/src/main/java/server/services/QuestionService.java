package server.services;

import commons.Activity;
import commons.Question;
import commons.QuestionTypes;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@DependsOn("dbInitializer")
public class QuestionService {

    ActivityService activityService;
    Random random;

    public QuestionService(ActivityService activityService, Random random) {
        this.activityService = activityService;
        this.random = random;
    }

    /**
     * <p>Returns a {@link List} of randomly generated objects of {@link Question} type</p>
     *
     * @param N how many questions should be generated
     * @return the {@link List} of generated questions
     */
    public List<Question> generateNQuestions(int N, String gameType) {
        try {
            if (N <= 0) {
                throw new IllegalArgumentException("The number of requested question must be greater than 0");
            }
            int upperbound;
            if (gameType.equals("singleplayer")) {
                upperbound = 4;
            } else {
                upperbound = 1;
            }

            List<Question> questionList = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                questionList.add(switch (random.nextInt(upperbound)) {
                    case 0 -> mostExpensiveQ();
                    case 1 -> getMultipleChoiceQ();
                    case 2 -> getEstimateQ();
                    case 3 -> getAlternativeQ();
                    default -> throw new IllegalStateException("Unexpected value");
                });
            }

            return questionList;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // default to returning 1 question
            return generateNQuestions(1, gameType);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Question mostExpensiveQ() {
        return new Question(activityService.getRandomN(3), QuestionTypes.mostExp);
    }

    private Question getMultipleChoiceQ() {
        return new Question(activityService.getRandomN(3), QuestionTypes.multiple);
    }

    private Question getEstimateQ() {
        return new Question(activityService.getRandomN(1), QuestionTypes.estimate);
    }

    private Question getAlternativeQ() {
        return new Question(activityService.getAlternative(), QuestionTypes.alternative);
    }

    /**
     * check's if the answer is correct and returns the score to be added
     *
     * @param question for which question to check the answers for
     * @param answer   the answer, consumption, to check (in watt/hour)
     * @return returns the score that should be added to player
     */
    public static long validateAnswer(Question question, int answer) {
        int incrementScore = 1000;
        List<Activity> activities = question.getActivityList();
        switch (question.getType()) {
            case mostExp:
                for (Activity activity : activities) {
                    if (answer < activity.consumptionInWh) {
                        return 0;
                    }
                }
                return incrementScore;
            case estimate:
                // returns the score scaled to how close the answer was
                // if they are equal the score is 100% * incrementScore
                // if the answer deviates with more than 50% * the correct
                // answer then the resulting score is 0.
                // prevent the formula from calculating a negative score
                double correctness = answer;
                if (answer > activities.get(0).consumptionInWh) {
                    correctness = -1 * answer + 2 * activities.get(0).consumptionInWh;
                }
                long score = (long) (incrementScore * (((2 * correctness) / (activities.get(0).consumptionInWh)) - 1));
                return Math.max(0, score);
            case multiple:
            case alternative:
                // assuming that activity 0 is the correct answer
                if (activities.get(0).consumptionInWh == answer) {
                    return incrementScore;
                }
                break;
            default:
                System.out.println("Error validating answer");
        }
        return 0;
    }
}

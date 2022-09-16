package commons;

import java.util.List;
import java.util.Objects;

/**
 * POJO class that stores a list of activities
 */
public class Question {
    private List<Activity> activityList;
    private QuestionTypes type;

    public Question() {

    }

    public Question(List<Activity> activityList, QuestionTypes type) {
        this.activityList = activityList;
        this.type = type;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public QuestionTypes getType() {
        return type;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public void setType(QuestionTypes type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return Objects.equals(activityList, question.activityList) && type == question.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityList, type);
    }

    @Override
    public String toString() {
        return "Question{" +
                "activityList=" + activityList +
                ", type=" + type +
                '}';
    }
}

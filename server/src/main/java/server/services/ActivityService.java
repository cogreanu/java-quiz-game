package server.services;

import commons.Activity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import server.database.ActivityRepository;

import java.util.*;

@Service
public class ActivityService {

    Random random;
    ActivityRepository repo;

    /**
     * Constructor of ActivityService
     *
     * @param random the Random to be used
     * @param repo   the ActivityRepository to be used
     */
    public ActivityService(Random random, ActivityRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Returns an Activity list containing N random Activities
     *
     * @param N the amount of Activities to be selected
     * @return an Activity list containing N random Activities
     * @throws IllegalArgumentException when N is not between 0 and repo.count()
     */
    public List<Activity> getRandomN(int N) throws IllegalArgumentException {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }
        if (N > repo.count()) {
            throw new IllegalArgumentException("N must not exceed activity count");
        }

        List<Activity> activities = repo.findAll();
        Set<Integer> chosenIndices = new HashSet<>();
        int repoSize = (int) repo.count();

        while (N != 0) {
            int index = random.nextInt(repoSize);

            if (!chosenIndices.contains(index)) {
                chosenIndices.add(index);
                N--;
            }
        }

        return chosenIndices.stream().map(activities::get).toList();
    }

    /**
     * gets 4 activities, 2 activities with equal consumption and 2 random activities
     *
     * @return list of 4 activities
     */
    public List<Activity> getAlternative() {
        List<Activity> output = new ArrayList<>();
        List<Activity> sortedList = repo.findAll(Sort.by(Sort.Direction.ASC, "consumptionInWh"));
        while (output.size() != 2) {                                    // loops until 2 equal activities have been found
            int index = random.nextInt((int) repo.count());
            Activity mainAct = sortedList.get(index);
            Activity aboveAct = sortedList.get(index + 1);
            Activity belowAct = sortedList.get(index - 1);
            if (Objects.equals(mainAct.consumptionInWh, aboveAct.consumptionInWh) && !similarT(mainAct, aboveAct)) {
                output.add(mainAct);
                output.add(aboveAct);
            } else if (Objects.equals(mainAct.consumptionInWh, belowAct.consumptionInWh) && !similarT(mainAct, belowAct)) {
                output.add(mainAct);
                output.add(belowAct);
            }
        }
        while (output.size() < 4) {                                     // loop to get 2 random and different activities
            Activity activity = getRandomN(1).get(0);
            boolean similar = false;
            for (Activity act : output) {
                if (similarT(act, activity) || Objects.equals(activity.consumptionInWh, act.consumptionInWh)) {
                    similar = true;
                }
            }
            if (!similar) {
                output.add(activity);
            }
        }
        return output;
    }

    /**
     * *Method which finds 3 random neighbouring Activities
     *
     * @return List with 3 Activities
     * @throws IllegalArgumentException when not enough entries are present
     */
    public List<Activity> getThreeQuestions() throws IllegalArgumentException {

        List<Activity> output = new ArrayList<>();              //list to contain the 3 activities to be returned
        List<Activity> sortedList = repo.findAll(Sort.by(Sort.Direction.ASC, "consumptionInWh"));       //sorts database into list
        int size = sortedList.size();                   //size of the sorted list with all the activities
        if (size < 3) throw new IllegalArgumentException("Not enough entries");
        int firstActivityIndex = new Random().nextInt(size);        //random index to find first activity

        output.add(sortedList.get(firstActivityIndex));                 //add first activity

        boolean has2 = false;               //checks whether second activity has been added
        Long currConWh = output.get(0).consumptionInWh;        //value to compare so that 3 activities with different consumption can be found
        try {
            int i = firstActivityIndex + 1;
            while (true) {                  //looks for activity with higher consumption
                if (sortedList.get(i).consumptionInWh.equals(currConWh)) {
                    output.add(sortedList.get(i));
                    if (!has2) {
                        has2 = true;
                        currConWh = sortedList.get(i).consumptionInWh;
                    } else {
                        break;
                    }
                }
                i++;
            }
        } catch (IndexOutOfBoundsException e) {

            //If there is no second or third activity with a higher distinct consumption, activities with a lower
            //consumption will be attempted to be found
            try {
                int i = firstActivityIndex - 1;
                currConWh = output.get(0).consumptionInWh;
                while (true) {
                    if (sortedList.get(i).consumptionInWh.equals(currConWh)) {
                        output.add(sortedList.get(i));
                        if (!has2) {
                            has2 = true;
                            currConWh = sortedList.get(i).consumptionInWh;
                        } else {
                            break;
                        }
                    }
                    i--;
                }
            } catch (IndexOutOfBoundsException f) {
                throw new IllegalArgumentException("Not enough distinct entries");
            }
        }

        return output;
    }

    /**
     * Checks whether 2 activities have the same title
     *
     * @param act1 activity to get the title from
     * @param act2 activity to get the title from
     * @return whether 2 activities have the same title
     */
    public boolean similarT(Activity act1, Activity act2) {
        String title1 = act1.title.toLowerCase();
        String title2 = act2.title.toLowerCase();
        return title1.contains(title2) || title2.contains(title1);
    }
}

package server.api;

import commons.Activity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityRepository;
import server.services.ActivityService;
import server.services.DatabaseInitializer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository repo;
    private final ActivityService qs;
    private final DatabaseInitializer dbInitializer;

    /**
     * Constructor of ActivityController
     *
     * @param repo the ActivityRepository to be used
     * @param qs   the ActivityService to be used
     */
    public ActivityController(ActivityRepository repo, ActivityService qs, DatabaseInitializer dbInitializer) {
        this.repo = repo;
        this.qs = qs;
        this.dbInitializer = dbInitializer;
    }

    /**
     * Returns the entire Activity list of the ActivityController
     *
     * @return an Activity list containing every Activity
     */
    @GetMapping(path = {"", "/"})
    public List<Activity> getAll() {
        return repo.findAll();
    }

    /**
     * Get a specific Activity from the ActivityRepository
     *
     * @param questionId the questionId of the Activity
     * @return return ResponseEntity<Activity> if the Activity is successfully found,
     * Badrequest otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable("id") long questionId) {
        if (repo.existsById(questionId)) {
            try {
                Activity activity = repo.findById(questionId).get();
                return ResponseEntity.ok(activity);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Deletes every current Activity and reads in a json file containing new
     * ones and adds them to the database as an Activity
     * <p>
     * Note: There may be a better way to do this, but it is difficult because
     * IDs are randomly generated, and only "question_id" will match
     * Future improvements could make this step better but for now its good enough
     *
     * @return ResponseEntity.ok(" SUCCESS : Database successfully reset. ")
     */
    @PutMapping(path = {"/reset"})
    public ResponseEntity<String> resetActivities() {
        System.out.println("Resetting activities in database.");

        List<Activity> all = repo.findAll();
        for (Activity act : all) {
            repo.deleteById(act.getQuestionId());
        }
        dbInitializer.run("");
        return ResponseEntity.ok("SUCCESS: Database successfully reset.");
    }

    /**
     * Adds an Activity to the ActivityRepository
     *
     * @param activity the Activity to be added
     * @return ResponseEntity.ok(saved) if everything worked,
     * ResponseEntity.badRequest().build() if one of the fields in Activity is null
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Activity> add(@RequestBody Activity activity) {
        if (isNullOrEmpty(activity.imagePath) || isNullOrEmpty(activity.id) || isNullOrEmpty(activity.source) || isNullOrEmpty(activity.title)) {
            return ResponseEntity.badRequest().build();
        }

        Activity saved = repo.save(new Activity(activity.id, activity.imagePath, activity.title, activity.consumptionInWh, activity.source));
        return ResponseEntity.ok(saved);
    }

    /**
     * Changes an Activity in the ActivityRepository
     *
     * @param newActivity the new Activity
     * @param questionId  the questionId of the Activity to be changed
     * @return ResponseEntity.badRequest().build() if none of the fields of newActivity is
     * null, ResponseEntity.badRequest().build() otherwise
     */
    @PutMapping(path = {"/{questionId}"})
    public ResponseEntity<Activity> put(@RequestBody Activity newActivity, @PathVariable long questionId) {
        if (isNullOrEmpty(newActivity.id) || isNullOrEmpty(newActivity.source) || isNullOrEmpty(newActivity.title)
                || isNullOrEmpty(newActivity.imagePath)) {
            return ResponseEntity.badRequest().build();
        }

        return repo.findById(questionId)
                .map(a -> {
                    a.id = newActivity.id;
                    a.source = newActivity.source;
                    a.title = newActivity.title;
                    a.imagePath = newActivity.imagePath;
                    a.consumptionInWh = newActivity.consumptionInWh;
                    return ResponseEntity.ok(repo.save(a));
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Delete an Activity from the ActivityRepository
     *
     * @param questionId the questionId of the Activity to be deleted
     * @return return ResponseEntity.ok().build() if the Activity is successfully deleted,
     * ResponseEntity.badRequest().build() otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Activity> deleteActivity(@PathVariable("id") long questionId) {
        if (repo.existsById(questionId)) {
            try {
                repo.deleteById(questionId);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Returns a ResponseEntity with an Activity list containing N random Activities
     *
     * @param N the amount of Activities to be selected
     * @return a ResponseEntity with an Activity list containing N random Activities
     */
    @GetMapping(path = {"/rnd{N}"})
    public ResponseEntity<List<Activity>> getRandomN(@PathVariable("N") int N) {
        try {
            return ResponseEntity.ok(qs.getRandomN(N));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Gets 3 activity for which the consumption is close to each other
     * @return a responseEntity with a list of activities that are close together
     */
    @GetMapping(path = {"/q/3close"})
    public ResponseEntity<List<Activity>> get3close() {
        try {
            return ResponseEntity.ok(qs.getThreeQuestions());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * GET route for retrieving images related to the activities.
     *
     * @param imgFolder folder number of image
     * @param imgName   name of image
     * @return Bytearray of the image
     */
    @GetMapping(path = {"/img/{imgFolder}/{imgName}"}, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImg(@PathVariable("imgFolder") String imgFolder, @PathVariable("imgName") String imgName) {
        try {
            InputStream inn = new FileInputStream("src/main/java/server/database/activity-bank/" + imgFolder + "/" + imgName);
            return ResponseEntity.ok(StreamUtils.copyToByteArray(inn));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GETs 2 activities with equal consumption
     *
     * @return list with the 2 activities
     */
    @GetMapping(path = {"/equal"})
    public List<Activity> getEqual() {
        return qs.getAlternative();
    }

    /**
     * Checks if a String is null or empty
     *
     * @param s the String to be checked
     * @return true iff s equals null
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
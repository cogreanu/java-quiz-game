package server.services;

import commons.Activity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import server.database.ActivityRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.FileSystems;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private ActivityRepository repo;
    private final String fileSep = FileSystems.getDefault().getSeparator();
    private String path = "./src/main/java/server/database/activity-bank/activities.json".replace("/", fileSep);

    /**
     * Constructor for the DatabaseInitializer
     *
     * @param repo containing the ActivityRepository used in the initialisation
     */

    @Autowired
    public DatabaseInitializer(ActivityRepository repo) {
        this.repo = repo;
    }

    /**
     * Constructor for the DatabaseInitializer
     *
     * @param repo containing the ActivityRepository used in the initialisation
     * @param path path for json file to read from in format folder/file
     */

    public DatabaseInitializer(ActivityRepository repo, String path) {
        this.repo = repo;
        this.path = path.replace("/", fileSep);
    }

    /**
     * The method that gets called by the commandLineRunner that actually initializes the database
     *
     * @param args part of the standard method signature required by the CommandLineRunner
     */
    @Override
    public void run(String... args) {
        try {
            if (repo.findAll().size() != 0) {
                System.out.println("Database already initialized, initialization not necessary");
                return;
            } else {
                JSONTokener parser = new JSONTokener(new FileReader(path));
                JSONArray activities = new JSONArray(parser);
                // goes through every JSONObject found by the parser and adds a new activity with the parameters of the JSONObject
                for (int i = 0; i < activities.length(); i++) {
                    JSONObject activity = activities.getJSONObject(i);
                    repo.save(new Activity(activity.getString("id"), activity.getString("image_path"),
                            activity.getString("title"), activity.getLong("consumption_in_wh"),
                            activity.getString("source")));
                }
            }
            System.out.println("Imported JSON file");
        } catch (DataIntegrityViolationException | FileNotFoundException | JSONException e) {
            System.out.println("activies.json file could not be found or parsed");
        }
    }
}

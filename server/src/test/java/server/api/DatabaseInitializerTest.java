package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.services.DatabaseInitializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseInitializerTest {
    private TestActivityRepository repo;
    private DatabaseInitializer dbInitializer;
    private int dbSize = 1687; //if sizeCheck fails double check if the number of entries in activities.json hasn't changed

    @BeforeEach
    public void setup() {
        repo = new TestActivityRepository();
        dbInitializer = new DatabaseInitializer(repo, "./src/main/java/server/database/activity-bank/activities.json");
        dbInitializer.run("");
    }

    @Test
    public void sizeCheck() {
        assertEquals(dbSize, repo.findAll().size());
    }


//    @Test
//    public void containsFirstActivity() {
//        Activity firstActivity = new Activity("0","61/gaming_pc.jpg","Using a gaming pc for one hour",
//        400L, "https://computerinfobits.com/how-much-energy-do-gaming-computers-use/");
//        firstActivity.setId(0L);
//        Activity testActivity = repo.getById(0L);
//        assertEquals(testActivity,firstActivity);
//    }


}

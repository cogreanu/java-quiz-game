package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTest {
    private Activity SOME_ACTIVITY;
    private Activity ANOTHER_ACTIVITY;

    @BeforeEach
    public void setup() {
        SOME_ACTIVITY = new Activity("1", "/img/1-1.jpg",
                "How many Wh does a 5 min shower use?", 100L,
                "wikipedia.com/someLink");
    }

    @Test
    public void checkConstructor() {
        assertNotNull(SOME_ACTIVITY);
    }

    @Test
    public void equalsHashCode() {
        var ANOTHER_ACTIVITY = new Activity("1", "/img/1-1.jpg",
                "How many Wh does a 5 min shower use?", 100L,
                "wikipedia.com/someLink");
        assertEquals(SOME_ACTIVITY, ANOTHER_ACTIVITY);
        assertEquals(SOME_ACTIVITY.hashCode(), ANOTHER_ACTIVITY.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var ANOTHER_ACTIVITY = new Activity("2", "/img/1-2.jpg",
                "How many Wh does a 5 min toaster use?", 50L,
                "wikipedia.com/anotherLink");
        assertNotEquals(SOME_ACTIVITY, ANOTHER_ACTIVITY);
        assertNotEquals(SOME_ACTIVITY.hashCode(), ANOTHER_ACTIVITY.hashCode());
    }
}

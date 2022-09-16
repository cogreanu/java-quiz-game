package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.database.ActivityRepository;
import server.services.ActivityService;
import server.services.DatabaseInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
public class MockActivityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    ActivityController activityController;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ActivityService activityService;

    @MockBean
    DatabaseInitializer databaseInitializer;

    Activity act1;
    String jsonAct1;
    Activity act2;
    Activity act3;

    @BeforeEach
    public void init() {
        activityController = new ActivityController(activityRepository, activityService, databaseInitializer);

        act1 = new Activity("ID1", "/image/1", "Title1", 100L, "wikipedia1.com");
        act2 = new Activity("ID2", "/image/2", "Title2", 200L, "wikipedia2.com");
        act3 = new Activity("ID3", "/image/3", "Title3", 300L, "wikipedia3.com");
        act1.setQuestionId(1L);
        act2.setQuestionId(2L);
        act3.setQuestionId(3L);

        JSONObject activityToSend = new JSONObject();
        activityToSend
                .put("questionId", act1.getQuestionId())
                .put("id", "ID1")
                .put("title", "Title1")
                .put("source", "wikipedia1.com")
                .put("image_path", "/image/1")
                .put("consumption_in_wh", 100L);

        jsonAct1 = activityToSend.toString();
    }

    @Test
    public void getAllTest() throws Exception {
        List<Activity> activityList = new ArrayList<>();

        activityList.add(act1);
        activityList.add(act2);

        when(activityRepository.findAll()).thenReturn(activityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("ID1"))
                .andExpect(jsonPath("$[0].image_path").value("/image/1"))
                .andExpect(jsonPath("$[1].title").value("Title2"))
                .andExpect(jsonPath("$[1].consumption_in_wh").value(200L));
    }

    @Test
    public void getZeroActivitiesTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void postNewActivityTest() throws Exception {
        when(activityRepository.save(any(Activity.class))).thenReturn(act1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/activities").content(jsonAct1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ID1"))
                .andExpect(jsonPath("$.image_path").value("/image/1"));
    }

    @Test
    public void postEmptyActivityBadRequestTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/activities").content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postNewActivityMissingParamsBadRequestTest() throws Exception {
        JSONObject badActivityJson = new JSONObject(jsonAct1);
        badActivityJson.put("image_path", "");
        badActivityJson.put("id", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/activities").content(badActivityJson.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putNewActivityTest() throws Exception {
//        when(activityRepository.findById(any(Long.class))).thenReturn(Optional.of(act2));
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/activities/" + act2.getQuestionId()).content(jsonAct1)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value("ID1"))
//                .andExpect(jsonPath("$.image_path").value("/image/1"));

        // NOTE:
        // Cant get this to work as I dont know how to tell Mockito what to return from lambdas.
    }

    @Test
    public void putNewActivityBadRequestTest() throws Exception {
        when(activityRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/activities/100").content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteActivityTest() throws Exception {
        when(activityRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteActivityDoesNotExistTest() throws Exception {
        when(activityRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/activities/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRandom2ActivityTest() throws Exception {
        List<Activity> activityList = new ArrayList<>();
        activityList.add(act2);
        activityList.add(act3);
        when(activityService.getRandomN(anyInt())).thenReturn(activityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/rnd2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("ID2"))
                .andExpect(jsonPath("$[0].image_path").value("/image/2"))
                .andExpect(jsonPath("$[1].title").value("Title3"))
                .andExpect(jsonPath("$[1].consumption_in_wh").value(300L));
    }

    @Test
    public void getRandomTooManyActivityTest() throws Exception {
        when(activityService.getRandomN(any(Integer.class))).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/rnd10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRandomZeroActivityTest() throws Exception {
        when(activityService.getRandomN(any(Integer.class))).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/rnd0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getClose3ActivityTest() throws Exception {
        List<Activity> activityList = new ArrayList<>();
        activityList.add(act1);
        activityList.add(act2);
        activityList.add(act3);
        when(activityService.getThreeQuestions()).thenReturn(activityList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/q/3close")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value("ID1"))
                .andExpect(jsonPath("$[0].image_path").value("/image/1"))
                .andExpect(jsonPath("$[1].title").value("Title2"))
                .andExpect(jsonPath("$[1].consumption_in_wh").value(200L))
                .andExpect(jsonPath("$[2].source").value("wikipedia3.com"));
    }

    @Test
    public void getImageTest() throws Exception {
//        byte[] img = new byte[1];
//        when(activityController.getImg(any(String.class),any(String.class)))
//                .thenReturn(new ResponseEntity<>(img, HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/img/01/img1.jpeg")
//                        .contentType(MediaType.IMAGE_JPEG))
//                .andExpect(status().isOk());

        // NOT COMPLETE TEST
        // No idea how to test this...
    }

    @Test
    public void getImageNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/activities/img/01/img1.jpeg")
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isNotFound());
    }
}

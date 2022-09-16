package server.api;

import commons.Question;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.services.QuestionService;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    QuestionService qs;

    public QuestionController(QuestionService qs) {
        this.qs = qs;
    }

    /**
     * Gets a question based for singleplayer using questionService
     * @return returns that question
     */
    @GetMapping(path = {"", "/"})
    public Question generateOneQuestion() {
        return qs.generateNQuestions(1, "singleplayer").get(0);
    }
}

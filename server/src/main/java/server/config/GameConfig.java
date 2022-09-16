package server.config;

import org.springframework.context.annotation.*;
import server.object.MultiplayerGame;
import server.services.QuestionService;

@Configuration
@Lazy
@DependsOn("dbInitializer")
public class GameConfig {

    private final QuestionService qs;

    public GameConfig(QuestionService qs) {
        this.qs = qs;
    }

    @Bean
    @Lazy(true)
    @Scope("prototype")
    public MultiplayerGame getMultiplayerGame() {
        return new MultiplayerGame(qs.generateNQuestions(20,"multiplayer"));
    }
}

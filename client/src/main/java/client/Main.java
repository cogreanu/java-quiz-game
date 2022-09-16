/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.*;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * All the stages and their controllers are linked together
     *
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

//        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
//        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var home = FXML.load(HomeScreenCtrl.class, "client", "scenes", "HomeScreen.fxml");
        var nickname = FXML.load(NicknameCtrl.class, "client", "scenes", "Nickname.fxml");
        var mostExpensive = FXML.load(MostExpensiveQSingleplayerCtrl.class, "client", "scenes", "MostExpensiveQSingleplayer.fxml");
        var globalLB = FXML.load(GlobalLeaderboardCtrl.class, "client", "scenes", "GlobalLeaderboard.fxml");
        var mostExpensiveMultiplayer = FXML.load(MostExpensiveQMultiplayerCtrl.class, "client", "scenes", "MostExpensiveQMultiplayer.fxml");
        var admin = FXML.load(AdminCtrl.class, "client", "scenes", "Admin.fxml");
        var addActivities = FXML.load(AddActivitiesCtrl.class, "client", "scenes", "AddActivities.fxml");
        var helpScreen = FXML.load(HelpScreenCtrl.class, "client", "scenes", "HowToPlay.fxml");
        var lobby = FXML.load(LobbyCtrl.class, "client", "scenes", "Lobby.fxml");
        var currentLB = FXML.load(CurrentLeaderboardCtrl.class, "client", "scenes", "CurrentLeaderboard.fxml");
        var estimateSingleplayerQ = FXML.load(EstimateQSingleplayerCtrl.class, "client", "scenes", "EstimateQSingleplayer.fxml");
        var multipleQSinglePlayer = FXML.load(MultipleQSingleplayerCtrl.class, "client", "scenes", "MultipleQSingleplayer.fxml");
        var alternativeQSingleplayer = FXML.load(AlternativeQSingleplayerCtrl.class, "client", "scenes", "AlternativeQSingleplayer.fxml");


        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, home, nickname, mostExpensive, globalLB, mostExpensiveMultiplayer,
                admin, addActivities, lobby, helpScreen, estimateSingleplayerQ, multipleQSinglePlayer,
                currentLB, alternativeQSingleplayer);
    }
}
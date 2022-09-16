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
package client.scenes;


import client.utils.ServerUtils;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;

public class MainCtrlTest {

    private MainCtrl sut;
    private ServerUtils utils;
    // private Player p;
    private Stage stage;
    private HomeScreenCtrl hctrl;
    private String s;
    private Parent parent; // parent needs to be initialized since the root can't be null, still trying to figure out how
    private NicknameCtrl nctrl;
    private TestParent tp;
    private TestScenes ts;
    private Pair<HomeScreenCtrl, Parent> home;
    private Pair<NicknameCtrl, Parent> nickname;

    @BeforeEach
    public void setup() {

        //  sut = new MainCtrl();
        nctrl = new NicknameCtrl(sut, utils);
        tp = new TestParent();
        ts = new TestScenes(parent);
        home = new Pair<>(hctrl, tp);
        nickname = new Pair<>(nctrl, tp);

    }
/*
The tests for the Mainctrl will be done by system testing, by the implementation of a users guide
 */
    /*@Test
    public void createPlayerTest() {
         s = "nickname";
        sut.initialize(stage,home, nickname );
        sut.createPlayer(s);
        assertEquals("nickname",p.nickName);
    }

    @Test
    public void showHomeTest(){
         s = "Home screen";
        sut.showHome();
        assertEquals(s, stage.getTitle());
    }*/


}
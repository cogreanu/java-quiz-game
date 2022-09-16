package client.scenes;

import javax.inject.Inject;

public class HelpScreenCtrl {

    MainCtrl mct;

    @Inject
    public HelpScreenCtrl(MainCtrl mct) {
        this.mct = mct;
    }

    public void exit() {
        mct.showHome();
    }
}

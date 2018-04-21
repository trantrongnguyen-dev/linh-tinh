package fga.bu22.android.home.controller;

import android.os.Message;

/**
 * Created by CTC_TRAINING on 4/19/2018.
 */

public class BaseState {
    protected EditTimeTableController mController;

    public BaseState(EditTimeTableController controller){
        mController = controller;
    }

    public void handleMsg(Message msg){
        // do somethings
    }
}

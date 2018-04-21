package fga.bu22.android.home.controller;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import fga.bu22.android.database.DatabaseHelper;
import fga.bu22.android.home.view.MainActivity;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class EditTimeTableController {

    public static final String TAG = EditTimeTableController.class.getSimpleName();

    public static final int LOAD_DATA_STATE = 1;
    public static final int DROP_STATE_ADD_NEW_ITEM = 2;
    public static final int DROP_STATE_REPLACE = 3;
    public static final int SAVE_DATA_STATE_ADD_LESSON = 4;

    private MainActivity mMainActivity;

    private DatabaseHelper mDatabaseHelper;

    private SparseArray<BaseState> mListState;

    private MsgHandler mMsgHander;

    private BaseState mCurrentState;

    public EditTimeTableController(MainActivity activity) {
        mMsgHander = new MsgHandler(this);
        mMainActivity = activity;
        mListState = initState();

        mDatabaseHelper = new DatabaseHelper(activity);

        mCurrentState = mListState.get(LOAD_DATA_STATE);
    }

    private SparseArray<BaseState> initState() {
        SparseArray<BaseState> states = new SparseArray<>();
        states.put(LOAD_DATA_STATE, new LoadDataState(this));
        states.put(DROP_STATE_ADD_NEW_ITEM, new DropState(this));
        states.put(DROP_STATE_REPLACE, new DropState(this));
        states.put(SAVE_DATA_STATE_ADD_LESSON, new SaveDataState(this));

        return states;
    }

    public MainActivity getMainActivity() {
        return mMainActivity;
    }

    public void sendMessage(Message msg) {
        mMsgHander.sendMessage(msg);
    }

    private void handlerMsg(Message msg) {
        transitionToState(msg.what);
        mCurrentState.handleMsg(msg);
    }

    private void transitionToState(int key) {
        mCurrentState = mListState.get(key);
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    /**
     * Ultility provides send message and handle message.
     */
    private static class MsgHandler extends Handler {

        private EditTimeTableController mController;

        public MsgHandler(EditTimeTableController controller) {
            super();
            mController = controller;
        }

        @Override
        public void handleMessage(Message msg) {
            mController.handlerMsg(msg);
        }
    }
}

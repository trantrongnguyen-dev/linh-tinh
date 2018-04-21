package fga.bu22.android.home.controller;

import android.os.Message;

import fga.bu22.android.home.adapter.TimeTableAdapter;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.models.TimeTableModel;

/**
 * Created by CTC_TRAINING on 4/19/2018.
 */

public class DropState extends BaseState {

    TimeTableModel mTimeTableModel;

    public DropState(EditTimeTableController controller) {
        super(controller);
        mTimeTableModel = controller.getMainActivity().getTimeTableModel();
    }

    @Override
    public void handleMsg(Message msg) {
        super.handleMsg(msg);
        switch (msg.what) {
            case EditTimeTableController.DROP_STATE_ADD_NEW_ITEM:
                if (msg.obj != null) {
                    if (msg.obj instanceof Lesson) {
                        Lesson lesson = (Lesson) msg.obj;
                        int postion = msg.arg1;
                        mTimeTableModel.updateTimeTable(new TimeTable(lesson.getName(), 1, 1, postion));
                    }
                }
                break;
            case EditTimeTableController.DROP_STATE_REPLACE:
                if (msg.obj != null) {
                    TimeTable timeTable = (TimeTable) msg.obj;
                    int newPosition = msg.arg1;
                    mTimeTableModel.replaceItemTimeTable(timeTable, newPosition);
                }
                break;
            default:
                break;
        }
    }
}

package fga.bu22.android.home.controller;


import android.os.Message;
import android.widget.Toast;

import java.util.List;

import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTableModel;

public class SaveDataState extends BaseState {

    private static final String TAG = SaveDataState.class.getSimpleName();

    TimeTableModel mTimeTableModel;

    public SaveDataState(EditTimeTableController controller) {
        super(controller);
        mTimeTableModel = controller.getMainActivity().getTimeTableModel();
    }

    @Override
    public void handleMsg(Message msg) {
        super.handleMsg(msg);
        switch (msg.what) {
            case EditTimeTableController.SAVE_DATA_STATE_ADD_LESSON:
                Lesson lesson = (Lesson) msg.obj;
                if (!mController.getDatabaseHelper().isExist(lesson) && mController.getDatabaseHelper().getAllLesson().size() <= 15) {
                    mController.getDatabaseHelper().addLesson(lesson);
                } else {
                    Toast.makeText(mController.getMainActivity().getApplicationContext(), "You can't not add new Lesson. Please check again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Lesson> lessons = mController.getDatabaseHelper().getAllLesson();
                mTimeTableModel.setLessonList(lessons);
                break;
            default:
                break;
        }
    }
}

package fga.bu22.android.home.controller;

import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import fga.bu22.android.home.view.MainActivity;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.models.TimeTableModel;

/**
 * Created by CTC_TRAINING on 4/19/2018.
 */

public class LoadDataState extends BaseState {

    private static final String TAG = LoadDataState.class.getSimpleName();

    TimeTableModel mTimeTableModel;

    public LoadDataState(EditTimeTableController controller) {
        super(controller);
        mTimeTableModel = controller.getMainActivity().getTimeTableModel();
    }

    @Override
    public void handleMsg(Message msg) {
        super.handleMsg(msg);
        if (msg.obj != null) {
            switch (msg.obj.toString()) {
                case MainActivity.OBJ_LOAD_DATA_TIME_TABLE:
                    int week = mController.getMainActivity().getWeek();
                    int year = mController.getMainActivity().getYear();

                    List<TimeTable> listTimeTable = mController.getDatabaseHelper().getAllTimeTableByWeek(week, year);

                    ArrayList<TimeTable> ttDatasource = new ArrayList<>();

                    for (int i = 0; i < 49; i++) {
                        ttDatasource.add(new TimeTable());
                    }

                    for (TimeTable timeTable : listTimeTable) {
                        ttDatasource.set(timeTable.getPosition(), timeTable);
                    }

                    mTimeTableModel.setTimeTableList(ttDatasource);
                    break;
                case MainActivity.OBJ_LOAD_DATA_LESSON:
                    List<Lesson> lessons = mController.getDatabaseHelper().getAllLesson();

                    ArrayList<Lesson> lessonsData = new ArrayList<>();

                    for (int i = 0; i < 15; i++) {
                        lessonsData.add(new Lesson());
                    }

                    for (int i = 0; i < lessons.size(); i++) {
                        lessonsData.set(i, lessons.get(i));
                    }
                    mTimeTableModel.setLessonList(lessonsData);
                    break;
                default:
                    break;
            }
        }
    }
}

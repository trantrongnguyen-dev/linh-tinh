package fga.bu22.android.home.controller;


import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
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
                if (mTimeTableModel.addLesson(lesson)) {
                    Toast.makeText(mController.getMainActivity().getApplicationContext(), "Add success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mController.getMainActivity().getApplicationContext(), "You can't not add new Lesson. Please check again!", Toast.LENGTH_SHORT).show();
                }
                break;
            case EditTimeTableController.SAVE_DATA_STATE_REPLACE_LESSON:
                ArrayList<String> listName = (ArrayList<String>) msg.obj;
                if (listName != null) {
                    mTimeTableModel.replaceLessonName(listName.get(0), listName.get(1));
                }
                break;
            case EditTimeTableController.SAVE_DATA_STATE_SAVE_ALL_DB:
                new UpdateLessonTask().execute();
                new UpdateTimetableTask().execute();
                break;
            default:
                break;
        }
    }

    private boolean checkExist(Lesson lesson, List<Lesson> listLesson) {
        for (Lesson ls : listLesson) {
            if (lesson.getName().equals(ls.getName())) {
                return true;
            }
        }
        return false;
    }

//    private boolean checkExistTimeTable(TimeTable timeTable, List<Lesson> listLesson) {
//        for (Lesson ls : listLesson) {
//            if (lesson.getName().equals(ls.getName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    private class UpdateTimetableTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<TimeTable> timeTableList = mController.getMainActivity().getTimeTableModel().getTimeTableList();
            List<Lesson> lessonList = mController.getMainActivity().getTimeTableModel().getLessonList();
            List<TimeTable> timeTables = mController.getDatabaseHelper().getAllTimeTable();

            for (TimeTable timeTable : timeTables) {
                for (Lesson lesson : lessonList) {
                    Log.d(TAG, "doInBackground: " + lesson.getOldName());
                    if (lesson.getOldName() != null) {
                        if (lesson.getOldName().equals(timeTable.getLessonName())) {
                            int a = mController.getDatabaseHelper().updateTimeTableByLesson(lesson, timeTable);
                            Log.d(TAG, "doInBackground: a  " + a);
                        }
                    }
                }
            }

            for (TimeTable timeTable : timeTableList) {

                if (("").equals(timeTable.getLessonName())) {
                    mController.getDatabaseHelper().deleteTimeTable(timeTable.getPosition(), timeTable.getWeek(), timeTable.getYear());
                }

                if (mController.getDatabaseHelper().isExistTimeTable(timeTable)) {
                    mController.getDatabaseHelper().updateTimeTable(timeTable);
                } else {
                    mController.getDatabaseHelper().addTimeTable(timeTable);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mController.getMainActivity().getTimeTableModel().updateAllToDB();
        }
    }

    private class UpdateLessonTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<Lesson> lessonList = mController.getMainActivity().getTimeTableModel().getLessonList();

            for (Lesson lesson1 : lessonList) {
                if (lesson1.getName() != null) {
                    if (!mController.getDatabaseHelper().isExist(lesson1)) {
                        mController.getDatabaseHelper().addLesson(lesson1);
                    }
                }
            }

            List<Lesson> lessonListDB = mController.getDatabaseHelper().getAllLesson();
            for (Lesson lessonDB : lessonListDB) {
                if (!checkExist(lessonDB, lessonList)) {
                    mController.getDatabaseHelper().deleteLesson(lessonDB);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mController.getMainActivity().getTimeTableModel().updateAllToDB();
        }
    }

}

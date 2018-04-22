package fga.bu22.android.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class TimeTableModel implements Serializable {

    public static final String EVENT_LOAD_LESSON_LIST = "EVENT_LOAD_LESSON_LIST";
    public static final String EVENT_LOAD_TIMETABLE = "EVENT_LOAD_TIMETABLE";
    public static final String EVENT_UPDATE_TIMETABLE = "EVENT_UPDATE_TIMETABLE";
    public static final String EVENT_REPLACE_ITEM_TIMETABLE = "EVENT_REPLACE_ITEM_TIMETABLE";
    public static final String EVENT_DELETE_ITEM_TIMETABLE = "EVENT_DELETE_ITEM_TIMETABLE";
    public static final String EVENT_DELETE_LESSON = "EVENT_DELETE_LESSON";
    public static final String EVENT_UPDATE_ALL_TO_DB = "EVENT_UPDATE_ALL_TO_DB";

    public static final String TIMETABLE = "TIMETABLE";

    private static TimeTableModel mTimeTableModel = null;

    private List<Lesson> mLessonList;

    private Lesson mLesson;

    private List<TimeTable> mTimeTableList;

    private PropertyChangeSupport mPropertyChangeSupport;

    public TimeTableModel() {
        this.mPropertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setPropertyChangeSupportListenner(PropertyChangeListener listenner) {
        mPropertyChangeSupport.addPropertyChangeListener(listenner);
    }

    public List<Lesson> getLessonList() {
        return mLessonList;
    }

    public void setLessonList(List<Lesson> mLessonList) {
        this.mLessonList = mLessonList;
        mPropertyChangeSupport.firePropertyChange(EVENT_LOAD_LESSON_LIST, null, mLessonList);
    }

    public List<TimeTable> getTimeTableList() {
        return mTimeTableList;
    }

    public void setTimeTableList(List<TimeTable> mTimeTableList) {
        this.mTimeTableList = mTimeTableList;
        mPropertyChangeSupport.firePropertyChange(EVENT_LOAD_TIMETABLE, null, mTimeTableList);
    }

    public void updateTimeTable(TimeTable timeTable) {
        mTimeTableList.set(timeTable.getPosition(), timeTable);
        mPropertyChangeSupport.firePropertyChange(EVENT_UPDATE_TIMETABLE, null, mTimeTableList);
    }

    public boolean addLesson(Lesson lesson) {
        boolean check = false;
        for (int i = 0; i < mLessonList.size(); i++) {
            if (!lesson.getName().equals(mLessonList.get(i).getName())) {
                check = true;
            } else {
                check = false;
            }
        }
        if (check) {
            mLessonList.add(lesson);
        } else {
            mPropertyChangeSupport.firePropertyChange(EVENT_LOAD_LESSON_LIST, null, mLessonList);
            return false;
        }
        mPropertyChangeSupport.firePropertyChange(EVENT_LOAD_LESSON_LIST, null, mLessonList);
        return true;
    }

    public void replaceItemTimeTable(TimeTable timeTable, int newPosition, int week, int year) {
        mTimeTableList.set(newPosition, new TimeTable(timeTable.getLessonName(), week, year, newPosition));
        mTimeTableList.set(timeTable.getPosition(), new TimeTable());
        mPropertyChangeSupport.firePropertyChange(EVENT_REPLACE_ITEM_TIMETABLE, null, mTimeTableList);
    }

    public void deleteItemTimeTable(TimeTable timeTable) {
        mTimeTableList.set(timeTable.getPosition(), new TimeTable());
        mPropertyChangeSupport.firePropertyChange(EVENT_DELETE_ITEM_TIMETABLE, null, mTimeTableList);
    }

    public void deleteLesson(Lesson lesson) {
        mLessonList.remove(lesson);

        //Delete timeTable item have lesson
        ArrayList<Integer> itemPosition = new ArrayList<>();
        for (int i = 0; i < mTimeTableList.size(); i++) {
            TimeTable timeTable = mTimeTableList.get(i);
            if (timeTable.getLessonName() != null) {
                if (timeTable.getLessonName().equals(lesson.getName())) {
                    itemPosition.add(i);
                }
            }
        }

        for (Integer item : itemPosition) {
            mTimeTableList.set(item, new TimeTable());
        }

        mPropertyChangeSupport.firePropertyChange(EVENT_DELETE_ITEM_TIMETABLE, null, mTimeTableList);
        mPropertyChangeSupport.firePropertyChange(EVENT_DELETE_LESSON, null, mLessonList);
    }

    public void replaceLessonName(String oldName, String newName) {
        for (Lesson lesson : mLessonList) {
            if (lesson.getName().equals(oldName)) {
                lesson.setName(newName);
            }
        }

        for (TimeTable timeTable : mTimeTableList) {
            if (timeTable.getLessonName() != null) {
                if (timeTable.getLessonName().equals(oldName)) {
                    timeTable.setLessonName(newName);
                }
            }
        }

        mPropertyChangeSupport.firePropertyChange(EVENT_DELETE_ITEM_TIMETABLE, null, mTimeTableList);
        mPropertyChangeSupport.firePropertyChange(EVENT_DELETE_LESSON, null, mLessonList);
    }

    public void updateAllToDB() {
        mPropertyChangeSupport.firePropertyChange(EVENT_UPDATE_ALL_TO_DB, "", "Update  Success");

    }
}

package fga.bu22.android.models;

import android.os.Bundle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class TimeTableModel {

    public static final String EVENT_LOAD_LESSON_LIST = "EVENT_LOAD_LESSON_LIST";
    public static final String EVENT_LOAD_TIMETABLE = "EVENT_LOAD_TIMETABLE";
    public static final String EVENT_UPDATE_TIMETABLE = "EVENT_UPDATE_TIMETABLE";
    public static final String EVENT_REPLACE_ITEM_TIMETABLE = "EVENT_REPLACE_ITEM_TIMETABLE";

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
    public boolean addLesson(Lesson lesson){
        mLessonList.add(lesson);
        mPropertyChangeSupport.firePropertyChange(EVENT_LOAD_LESSON_LIST, null, mLessonList);
        return true;
    }

    public void replaceItemTimeTable(TimeTable timeTable, int newPosition) {
        mTimeTableList.set(newPosition, new TimeTable(timeTable.getLessonName(), 1, 1,newPosition));
        mTimeTableList.set(timeTable.getPosition(), new TimeTable());
        mPropertyChangeSupport.firePropertyChange(EVENT_REPLACE_ITEM_TIMETABLE, null, mTimeTableList);
    }
}

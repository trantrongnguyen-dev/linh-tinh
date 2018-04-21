package fga.bu22.android.models;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class TimeTable {

    private String lessonName;

    private int week;

    private int year;

    private int position;

    public TimeTable(String lessonName, int week, int year, int position) {
        this.lessonName = lessonName;
        this.week = week;
        this.year = year;
        this.position = position;
    }

    public TimeTable() {

    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

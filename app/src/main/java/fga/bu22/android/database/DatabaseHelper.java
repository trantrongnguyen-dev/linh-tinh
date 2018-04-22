package fga.bu22.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;

/**
 * Created by CTC_TRAINING on 4/17/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //nguyen nguyehn nencrnguyen69
    public static final String DATABASE_NAME = "Timetable_database";
    public static final int DATABASE_VERSON = 4;
    public static final String TIMETABLE_TABLE = "tbl_Timetable";
    public static final String TIMETABLE_ID = "timetableID";
    public static final String TIMETABLE_WEEK = "timetableWeek";
    public static final String TIMETABLE_YEAR = "timetableYear";
    public static final String TIMETABLE_POSITION = "timetablePosition";


    public static final String LESSON_TABLE = "tbl_Lesson";
    public static final String LESSON_ID = "lessonID";
    public static final String LESSON_NAME = "lessonName";


    public static final String CREATE_LESSON = "CREATE TABLE  " + LESSON_TABLE + " ( " + LESSON_ID + " INTEGER PRIMARY KEY , " + LESSON_NAME + " TEXT " + ")";
    public static final String CREATE_TIMETABLE = "CREATE TABLE  " + TIMETABLE_TABLE + " ( " + TIMETABLE_ID + " INTEGER PRIMARY KEY , "  + LESSON_NAME + " TEXT , "
            + TIMETABLE_POSITION + " INTEGER , " + TIMETABLE_WEEK + " INTEGER , " + TIMETABLE_YEAR + "INTEGER " + ")";


    public static final String TAG = DatabaseHelper.class.getSimpleName();
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSON);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LESSON);
        db.execSQL(CREATE_TIMETABLE);
        Log.d(TAG, "onCreate: "+DATABASE_VERSON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LESSON_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE_TABLE);
        Log.d(TAG, "onUpgrade: "+DATABASE_VERSON);
        onCreate(db);
    }

    public void addLesson(Lesson lesson) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LESSON_NAME, lesson.getName());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(LESSON_TABLE, null, values);

        // Đóng kết nối database.
        db.close();
    }


    public List<Lesson> getAllLesson() {

        List<Lesson> listLesson = new ArrayList<Lesson>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LESSON_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Lesson lesson = new Lesson();
                lesson.setName(cursor.getString(1));


                // Thêm vào danh sách.
                listLesson.add(lesson);
            } while (cursor.moveToNext());
        }

        // return note list
        return listLesson;
    }

    public int updateLesson(Lesson lesson) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LESSON_NAME, lesson.getName());

        // updating row
        return db.update(LESSON_TABLE, values, LESSON_NAME + " = ?",
                new String[]{lesson.getName()});
    }

    public void deleteLesson(Lesson lesson) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LESSON_TABLE, LESSON_NAME + " = ?",
                new String[]{lesson.getName()});
        db.close();
    }

    public void addTimeTable(TimeTable timeTable) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LESSON_NAME, timeTable.getLessonName());
        values.put(TIMETABLE_POSITION, timeTable.getPosition());
        values.put(TIMETABLE_WEEK, timeTable.getWeek());
        values.put(TIMETABLE_YEAR, timeTable.getYear());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TIMETABLE_TABLE, null, values);


        // Đóng kết nối database.
        db.close();
    }

    public List<TimeTable> getAllTimeTableByWeek(int week, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<TimeTable> timeTableList = new ArrayList<>();
        Cursor cursor = db.query(TIMETABLE_TABLE,
                new String[]{TIMETABLE_ID, LESSON_NAME, TIMETABLE_POSITION, TIMETABLE_WEEK,TIMETABLE_YEAR},
                TIMETABLE_WEEK + "=? AND " + TIMETABLE_YEAR + " =? ",
                new String[]{String.valueOf(week)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TimeTable timeTable = new TimeTable();
                timeTable.setLessonName(cursor.getString(1));
                timeTable.setPosition(Integer.parseInt(cursor.getString(2)));
                timeTable.setWeek(Integer.parseInt(cursor.getString(3)));
                timeTable.setYear(Integer.parseInt(cursor.getString(4)));

                // Thêm vào danh sách.
                timeTableList.add(timeTable);
            } while (cursor.moveToNext());
        }
        return timeTableList;
    }

    public List<TimeTable> getAllTimeTable() {

        List<TimeTable> listTimeTables = new ArrayList<TimeTable>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TIMETABLE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                TimeTable timeTable = new TimeTable();
                timeTable.setLessonName(cursor.getString(1));
                timeTable.setPosition(Integer.parseInt(cursor.getString(2)));
                timeTable.setWeek(Integer.parseInt(cursor.getString(3)));
                timeTable.setYear(Integer.parseInt(cursor.getString(4)));


                // Thêm vào danh sách.
                listTimeTables.add(timeTable);
            } while (cursor.moveToNext());
        }

        // return note list
        return listTimeTables;
    }

    public int updateTimeTable(TimeTable timeTable) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LESSON_NAME, timeTable.getLessonName());
        values.put(TIMETABLE_POSITION, timeTable.getPosition());
        values.put(TIMETABLE_WEEK, timeTable.getWeek());
        values.put(TIMETABLE_YEAR, timeTable.getYear());

        // updating row
        return db.update(TIMETABLE_TABLE, values,
                TIMETABLE_POSITION + " = ? AND " + TIMETABLE_WEEK + "=? AND" + TIMETABLE_YEAR + "=?",
                new String[]{String.valueOf(timeTable.getPosition()), String.valueOf(timeTable.getWeek()), String.valueOf(timeTable.getYear())});
    }

    public boolean deleteTimeTable(TimeTable timeTable) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            sqLiteDatabase.delete(TIMETABLE_TABLE, TIMETABLE_WEEK + "= ? AND"
                    + TIMETABLE_YEAR + " =? ", new String[]{String.valueOf(timeTable.getWeek()), String.valueOf(timeTable.getYear())});
        } catch (Exception e) {
            Log.d(TAG, "delete error: ");
            return false;
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        return true;
    }

    public boolean isExist(Lesson lesson) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LESSON_TABLE, new String[]{LESSON_ID,
                        LESSON_NAME}, LESSON_NAME + "=?",
                new String[]{lesson.getName()}, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }

    public boolean isExistTimeTable(TimeTable timeTable) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TIMETABLE_TABLE, new String[]{TIMETABLE_ID,
                        LESSON_NAME, TIMETABLE_POSITION, TIMETABLE_WEEK, TIMETABLE_YEAR},
                TIMETABLE_POSITION + " =? AND " + TIMETABLE_WEEK + " =? AND " + TIMETABLE_YEAR + " =? ",
                new String[]{String.valueOf(timeTable.getPosition()), String.valueOf(timeTable.getWeek()), String.valueOf(timeTable.getYear())},
                null, null, null, null);
        if (cursor.getCount() <= 0) {
            return false;
        }
        return true;
    }
}

package fga.bu22.android.home.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import fga.bu22.android.R;
import fga.bu22.android.database.DatabaseHelper;
import fga.bu22.android.editlesson.EditLessonNameActivity;
import fga.bu22.android.home.adapter.LessonAdapter;
import fga.bu22.android.home.adapter.TimeTableAdapter;
import fga.bu22.android.home.controller.EditTimeTableController;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.models.TimeTableModel;
import fga.bu22.android.util.Logger;

public class MainActivity extends AppCompatActivity {

    public static final String OBJ_LOAD_DATA_TIME_TABLE = "EVENT_LOAD_DATA_TIME_TABLE";
    public static final String OBJ_LOAD_DATA_LESSON = "EVENT_LOAD_DATA_LESSON";
    public static final String INTENT_LESSON_NAME = "INTENT_LESSON_NAME";
    public static final String INTENT_LESSON_LIST = "INTENT_LESSON_LIST";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SHARED_PREFERENCES_WEEK_OF_YEAR = "SHARED_PREFERENCES_WEEK_OF_YEAR";
    private static final String SAVE_PREFERENCE_WEEK = "SAVE_PREFERENCE_WEEK";
    private static final String SAVE_PREFERENCE_YEAR = "SAVE_PREFERENCE_YEAR";
    private int yearDialog, monthDialog, dayDialog;

    private RelativeLayout mRelativeTimeTable;

    private GridView mGridTimeTable;
    private GridView mGridLesson;

    private LessonAdapter mLessonAdapter;
    private TimeTableAdapter mTimeTableAdapter;

    private TimeTableModel mTimeTableModel;

    TextView dateStartTv;
    TextView dateEndTv;

    private ImageView mImgPrev;
    private ImageView mImgNext;

    private TextView mTxtPeriod;
    private ImageView mImgRecycleBin;

    private ImageView mImgAddLesson;
    private Button mBtnEditLessonName;
    private Button mBtnOk;
    private Button mBtnCancel;

    private Animation mAnimZoomIn, mAnimZoomOut;

    private EditTimeTableController mEditTimeTableController;

    private DatabaseHelper mDatabaseHelper;

    private List<TimeTable> mTimeTableList = new ArrayList<>();
    private List<Lesson> mLessonList = new ArrayList<>();

    private boolean mIsEditingLessonName = false;
    private boolean mIsDragToDelete = false;
    private boolean mIsModify = false;

    public TimeTableModel getTimeTableModel() {
        return mTimeTableModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("CHINH", "onCreate: ");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        yearDialog = calendar.get(Calendar.YEAR);
        monthDialog = calendar.get(Calendar.MONTH);
        dayDialog = calendar.get(Calendar.DAY_OF_MONTH);

        initViews();
        initModel();
        registerViewListenner();
        initController();
    }


    private void initController() {
        mEditTimeTableController = new EditTimeTableController(this);

        if (isFirstLauncher()) {
            Calendar now = Calendar.getInstance();
            Date date = now.getTime();
            int week = now.get(Calendar.WEEK_OF_YEAR);
            int years = now.get(Calendar.YEAR);
            saveWeekAndYear(week, years);
        }

        mTxtPeriod.setText(getWeek() + "");
        //Load data lesson
        Message message = new Message();
        message.what = EditTimeTableController.LOAD_DATA_STATE;
        message.obj = OBJ_LOAD_DATA_LESSON;
        mEditTimeTableController.sendMessage(message);

        //Load data timetable
        message = new Message();
        message.what = EditTimeTableController.LOAD_DATA_STATE;
        message.obj = OBJ_LOAD_DATA_TIME_TABLE;
        mEditTimeTableController.sendMessage(message);
    }

    private void initModel() {
        mTimeTableModel = new TimeTableModel();

        mTimeTableModel.setPropertyChangeSupportListenner(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                onUpdateModel(propertyChangeEvent);
            }
        });
    }

    public int getWeek() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WEEK_OF_YEAR, Context.MODE_PRIVATE);
        int week = sharedPreferences.getInt(SAVE_PREFERENCE_WEEK, -1);
        return week;
    }

    public int getYear() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WEEK_OF_YEAR, Context.MODE_PRIVATE);
        int year = sharedPreferences.getInt(SAVE_PREFERENCE_YEAR, -1);
        return year;
    }

    private void onUpdateModel(PropertyChangeEvent propertyChangeEvent) {
        switch (propertyChangeEvent.getPropertyName()) {
            case TimeTableModel.EVENT_LOAD_LESSON_LIST:
                mLessonList.clear();
                mLessonList.addAll(mTimeTableModel.getLessonList());
                mLessonAdapter.notifyDataSetChanged();

                mIsModify = false;
                break;
            case TimeTableModel.EVENT_LOAD_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);

                mIsModify = false;
                break;
            case TimeTableModel.EVENT_UPDATE_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);

                mIsModify = true;
                break;
            case TimeTableModel.EVENT_REPLACE_ITEM_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);

                mIsModify = true;
                break;
            case TimeTableModel.EVENT_DELETE_ITEM_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);

                mIsModify = true;
                break;
            case TimeTableModel.EVENT_DELETE_LESSON:
                mLessonList.clear();
                mLessonList.addAll(mTimeTableModel.getLessonList());

                mLessonAdapter.notifyDataSetChanged();

                mIsModify = true;
                break;
            case TimeTableModel.EVENT_UPDATE_ALL_TO_DB:
                Toast.makeText(MainActivity.this, propertyChangeEvent.getNewValue().toString(), Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    private void initViews() {
        mGridTimeTable = findViewById(R.id.grid_time_table);
        mGridLesson = findViewById(R.id.grid_list_lesson);
        mImgPrev = findViewById(R.id.img_prev);
        mImgNext = findViewById(R.id.img_next);
        mTxtPeriod = findViewById(R.id.txt_period);
        mImgRecycleBin = findViewById(R.id.img_recycle_bin);
        mImgAddLesson = findViewById(R.id.img_add_lesson);
        mBtnEditLessonName = findViewById(R.id.btn_edit_lesson_name);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mRelativeTimeTable = findViewById(R.id.relative_time_table);

        mTimeTableAdapter = new TimeTableAdapter(this, mTimeTableList);
        if (mGridTimeTable != null) {
            Toast.makeText(this, "asg", Toast.LENGTH_SHORT).show();
            mGridTimeTable.setAdapter(mTimeTableAdapter);

        }
        mLessonAdapter = new LessonAdapter(this, mLessonList);
        if (mGridLesson != null) {
            mGridLesson.setAdapter(mLessonAdapter);
        }

        mAnimZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.delete_anim_zoom_in);
        mAnimZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.delete_anim_zoom_out);
    }

    private void registerViewListenner() {
        initGridLessonListenner();
        initGridTimeTableListenner();
        initBtnOkListener();
        initBtnCancelListener();
        initBtnEditLessonName();
        initImgAddLessonListener();
        initGridLessonClickListenner();
        initPrevListenner();
        initNextListenner();
        initPeriodListener();
    }

    private void initPeriodListener() {
        mTxtPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(MainActivity.this, myDateListener3, yearDialog, monthDialog, dayDialog);
                datePickerDialog.show();
            }
        });
    }

    private void initNextListenner() {
        mImgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsModify) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Calendar");
                    builder.setMessage("Bạn có muốn lưu thay đổi không?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadDataByNextButton();
                        }
                    });
                    builder.setNegativeButton("Được", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Message message = new Message();
                            message.what = EditTimeTableController.SAVE_DATA_STATE_SAVE_ALL_DB;
                            mEditTimeTableController.sendMessage(message);
                            loadDataByNextButton();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    loadDataByNextButton();
                }
            }
        });
    }

    private void initPrevListenner() {
        mImgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsModify) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Calendar");
                    builder.setMessage("Bạn có muốn lưu thay đổi không?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadDataByPrevButton();
                        }
                    });
                    builder.setNegativeButton("Được", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Message message = new Message();
                            message.what = EditTimeTableController.SAVE_DATA_STATE_SAVE_ALL_DB;
                            mEditTimeTableController.sendMessage(message);
                            loadDataByPrevButton();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    loadDataByPrevButton();
                }
            }
        });
    }

    private void initGridLessonClickListenner() {
        mGridLesson.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!mIsEditingLessonName) {
                    return;
                }

                Intent intent = new Intent(MainActivity.this, EditLessonNameActivity.class);
                intent.putExtra(INTENT_LESSON_NAME, mTimeTableModel.getLessonList().get(i).getName());
                intent.putExtra(INTENT_LESSON_LIST, mTimeTableModel);

                startActivityForResult(intent, EditLessonNameActivity.CODE_EDIT_LESON_NAME_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EditLessonNameActivity.CODE_EDIT_LESON_NAME_ACTIVITY:
                if (data != null) {
                    String oldLessonName = data.getStringExtra(EditLessonNameActivity.EXTRA_OLD_LESSON_NAME);
                    String newLessonName = data.getStringExtra(EditLessonNameActivity.EXTRA_NEW_LESSON_NAME);

                    ArrayList<String> listName = new ArrayList<>();
                    listName.add(oldLessonName);
                    listName.add(newLessonName);

                    Message msg = new Message();
                    msg.what = EditTimeTableController.SAVE_DATA_STATE_REPLACE_LESSON;
                    msg.obj = listName;
                    mEditTimeTableController.sendMessage(msg);
                }
        }
    }

    private void initImgAddLessonListener() {
        mImgAddLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_add_lesson, null);

                final EditText edtLessonName = dialogView.findViewById(R.id.edtLessonName);

                dialogBuilder.setView(dialogView);

                dialogBuilder.setTitle("Add lesson");
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = edtLessonName.getText().toString();
                        Lesson lesson = new Lesson(name);
                        Message message = new Message();
                        message.what = EditTimeTableController.SAVE_DATA_STATE_ADD_LESSON;
                        message.obj = lesson;
                        mEditTimeTableController.sendMessage(message);
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });
    }

    private void initBtnEditLessonName() {
        mBtnEditLessonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditingLessonName) {
                    mIsEditingLessonName = false;
                    mGridTimeTable.setEnabled(true);
                    mRelativeTimeTable.setAlpha(1f);
                    mBtnEditLessonName.setText(getResources().getString(R.string.btn_edit_lesson_name));
                    mBtnCancel.setEnabled(true);
                    mBtnOk.setEnabled(true);
                    mBtnOk.setAlpha(1f);
                    mBtnCancel.setAlpha(1f);
                } else {
                    mIsEditingLessonName = true;
                    mGridTimeTable.setEnabled(false);
                    mRelativeTimeTable.setAlpha(0.4f);
                    mBtnEditLessonName.setText(getResources().getString(R.string.btn_cancel_edit_lesson_name));
                    mBtnCancel.setEnabled(false);
                    mBtnOk.setEnabled(false);
                    mBtnOk.setAlpha(0.4f);
                    mBtnCancel.setAlpha(0.4f);
                }
            }
        });
    }

    private void initBtnCancelListener() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to do something
                finish();
            }
        });
    }

    private void initBtnOkListener() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to do something
                Message message = new Message();
                message.what = EditTimeTableController.SAVE_DATA_STATE_SAVE_ALL_DB;
                mEditTimeTableController.sendMessage(message);

                mIsModify = false;
            }
        });
    }

    private void initGridTimeTableListenner() {
        mGridTimeTable.setOnTouchListener(new View.OnTouchListener() {
            int mXTouch, mYTouch, curPosition;
            View mItemTouch;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        mXTouch = (int) event.getX();
                        mYTouch = (int) event.getY();

                        curPosition = mGridTimeTable.pointToPosition(mXTouch, mYTouch);

                        if ((curPosition > TimeTableAdapter.MAX_COLUMN) && (curPosition % TimeTableAdapter.MAX_COLUMN) != 0) {

                            TimeTable timeTable = mTimeTableAdapter.getItem(curPosition);
                            Log.d(TAG, "onTouch: " + timeTable.getLessonName());
                            if (timeTable.getLessonName() == null) {
                                break;
                            }

                            mItemTouch = mGridTimeTable.getChildAt(curPosition);
                            mItemTouch.startDrag(null, new View.DragShadowBuilder(mItemTouch) {

                                @Override
                                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                                    super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

                                    // Drag listener for recycle bin
                                    mImgRecycleBin.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            switch (event.getAction()) {
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    v.startAnimation(mAnimZoomIn);
                                                    mIsDragToDelete = true;
                                                    break;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    v.setBackgroundColor(Color.TRANSPARENT);
                                                    mIsDragToDelete = false;
                                                    v.startAnimation(mAnimZoomOut);
                                                    break;
                                                case DragEvent.ACTION_DROP:
                                                    Message msg = new Message();

                                                    msg.what = EditTimeTableController.DROP_STATE_DELETE_ITEM;
                                                    msg.obj = mTimeTableAdapter.getItem(curPosition);
                                                    mEditTimeTableController.sendMessage(msg);
                                                    mIsDragToDelete = true;
                                                    v.startAnimation(mAnimZoomOut);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            return true;
                                        }
                                    });

                                    for (int i = 0; i < 49; i++) {
                                        if (i > TimeTableAdapter.MAX_COLUMN && i % TimeTableAdapter.MAX_COLUMN != 0) {
                                            final int positionDrop = i;

                                            mGridTimeTable.getChildAt(i).setOnDragListener(new View.OnDragListener() {
                                                @Override
                                                public boolean onDrag(View v, DragEvent event) {
                                                    switch (event.getAction()) {
                                                        case DragEvent.ACTION_DRAG_ENTERED:
                                                            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                            break;
                                                        case DragEvent.ACTION_DRAG_EXITED:
                                                            v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.boder_grid));
                                                            v.setVisibility(View.VISIBLE);
                                                            break;
                                                        case DragEvent.ACTION_DRAG_ENDED:
                                                            v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.boder_grid));
                                                            break;
                                                        case DragEvent.ACTION_DROP:
                                                            Message message = new Message();
                                                            message.what = EditTimeTableController.DROP_STATE_REPLACE;
                                                            message.obj = mTimeTableAdapter.getItem(curPosition);
                                                            message.arg1 = positionDrop;
                                                            mEditTimeTableController.sendMessage(message);
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    return true;
                                                }
                                            });
                                        }
                                    }
                                }

                            }, mItemTouch, 0);

                            mItemTouch.setAlpha(0);

                            mItemTouch.setOnDragListener(new View.OnDragListener() {
                                @Override
                                public boolean onDrag(View v, DragEvent event) {
                                    if (event.getAction() == DragEvent.ACTION_DRAG_ENDED && !(v instanceof ImageView)) {
                                        if (mIsDragToDelete) {
                                            mItemTouch.setVisibility(View.GONE);
                                            mIsDragToDelete = false;
                                        } else {
                                            mItemTouch.setAlpha(1f);
                                            mIsDragToDelete = false;
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
    }

    private void initGridLessonListenner() {
        if (mGridLesson != null) {
            mGridLesson.setOnTouchListener(new View.OnTouchListener() {
                int mXTouch, mYTouch, curPosition;
                View mItemTouch;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIsEditingLessonName) {
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mXTouch = (int) event.getX();
                            mYTouch = (int) event.getY();

                            curPosition = mGridLesson.pointToPosition(mXTouch, mYTouch);
                            if (curPosition == GridView.INVALID_POSITION) {
                                return false;
                            }

                            mItemTouch = mGridLesson.getChildAt(curPosition);


                            mItemTouch.startDrag(null, new View.DragShadowBuilder(mItemTouch) {
                                @Override
                                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                                    super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

                                    if (mIsEditingLessonName || mGridTimeTable.getChildCount() < 36) {
                                        return;
                                    }

                                    // Drag listener for recycle bin
                                    mImgRecycleBin.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            switch (event.getAction()) {
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    v.startAnimation(mAnimZoomIn);
                                                    mIsDragToDelete = true;
                                                    break;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    v.setBackgroundColor(Color.TRANSPARENT);
                                                    mIsDragToDelete = false;
                                                    v.startAnimation(mAnimZoomOut);
                                                    break;
                                                case DragEvent.ACTION_DROP:
                                                    Message msg = new Message();

                                                    msg.what = EditTimeTableController.DROP_STATE_DELETE_LESSON;
                                                    msg.obj = mLessonAdapter.getItem(curPosition);
                                                    mEditTimeTableController.sendMessage(msg);
                                                    mIsDragToDelete = true;
                                                    v.startAnimation(mAnimZoomOut);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            return true;
                                        }
                                    });

                                    for (int i = 0; i < 49; i++) {
                                        if (i > TimeTableAdapter.MAX_COLUMN && i % TimeTableAdapter.MAX_COLUMN != 0) {
                                            final int finalPositon = i;
                                            mGridTimeTable.getChildAt(i).setOnDragListener(new View.OnDragListener() {
                                                @Override
                                                public boolean onDrag(View v, DragEvent event) {

                                                    switch (event.getAction()) {
                                                        case DragEvent.ACTION_DRAG_ENTERED:
                                                            v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                            break;
                                                        case DragEvent.ACTION_DRAG_ENDED:
                                                            v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.boder_grid));
                                                            break;
                                                        case DragEvent.ACTION_DRAG_EXITED:
                                                            v.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.boder_grid));
                                                            v.setVisibility(View.VISIBLE);
                                                            break;
                                                        case DragEvent.ACTION_DROP:
                                                            Log.d(TAG, "onDrag: ACTION_DROp");
                                                            showDialogRegister(curPosition, finalPositon);
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    return true;
                                                }
                                            });
                                        }
                                    }
                                }
                            }, mItemTouch, 0);

                            mItemTouch.setOnDragListener(new View.OnDragListener() {
                                @Override
                                public boolean onDrag(View v, DragEvent event) {
                                    ;
                                    if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                                        mItemTouch.setAlpha(1);
                                    }
                                    return true;
                                }
                            });
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }

    private void showDialogRegister(final int cur, final int finall) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_register, null);

        dateStartTv = dialogView.findViewById(R.id.date_start_tv);
        dateEndTv = dialogView.findViewById(R.id.date_end_tv);
        ImageView dateStartImg = dialogView.findViewById(R.id.date_start_img);
        ImageView dateEndImg = dialogView.findViewById(R.id.date_end_img);

        dialogBuilder.setView(dialogView);

        dateStartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, myDateListener, yearDialog, monthDialog, dayDialog);
                dialog.show();

            }
        });

        dateEndImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, myDateListener1, yearDialog, monthDialog, dayDialog);
                dialog.show();
            }
        });


        dialogBuilder.setTitle("Register cycle");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Lesson lesson = mLessonAdapter.getItem(cur);
                Calendar calendar = Calendar.getInstance();
                ArrayList<TimeTable> timeTableList = null;
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                if (dateStartTv.getText().equals("") || dateEndTv.getText().equals("")) {
                    Message message = new Message();
                    message.what = EditTimeTableController.DROP_STATE_ADD_NEW_ITEM;
                    message.obj = lesson;
                    message.arg1 = finall;
                    mEditTimeTableController.sendMessage(message);
                } else {
                    String startDate = dateStartTv.getText().toString();
                    String endDate = dateEndTv.getText().toString();
                    try {
                        Date start = df.parse(startDate);
                        Date end = df.parse(endDate);
                        calendar.setTime(start);
                        int fromWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                        int fromYear = calendar.get(Calendar.YEAR);
                        int maxWeek = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
                        calendar.setTime(end);
                        int toWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                        int toYear = calendar.get(Calendar.YEAR);

                        if (fromYear == toYear) {
                            if (fromWeek < toWeek) {
                                timeTableList = new ArrayList<>();
                                for (int i = fromWeek; i <= toWeek; i++) {
                                    Log.d(TAG, "onClick: for " + i);
                                    timeTableList.add(new TimeTable(lesson.getName(), i, fromYear, finall));
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "nhap sai roi may", Toast.LENGTH_SHORT).show();
                            }
                        } else if (fromYear < toYear) {
                            timeTableList = new ArrayList<>();
                            for (int i = fromWeek; i <= maxWeek; i++) {

                                timeTableList.add(new TimeTable(lesson.getName(), i, fromYear, finall));
                            }
                            for (int i = 1; i <= toWeek; i++) {
                                timeTableList.add(new TimeTable(lesson.getName(), i, toYear, finall));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "nhap sai roi may", Toast.LENGTH_SHORT).show();
                        }
                        Message message = new Message();
                        message.what = EditTimeTableController.DROP_STATE_ADD_NEW_ITEM;
                        message.obj = timeTableList;
                        mEditTimeTableController.sendMessage(message);
                        Log.d(TAG, "onClickDialog: " + timeTableList.get(1).getWeek());


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {

                    dateStartTv.setText(new StringBuilder().append(arg1).append("/")
                            .append(arg2 + 1).append("/").append(arg3));
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener1 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {

                    dateEndTv.setText(new StringBuilder().append(arg1).append("/")
                            .append(arg2 + 1).append("/").append(arg3));
                }
            };

    private DatePickerDialog.OnDateSetListener myDateListener3 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      final int arg1, int arg2, int arg3) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(arg1, arg2, arg3);
                    final int week = calendar.get(Calendar.WEEK_OF_YEAR);

                    if (mIsModify) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Calendar");
                        builder.setMessage("Bạn có muốn lưu thay đổi không?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadDataByLabel(week, arg1);
                            }
                        });
                        builder.setNegativeButton("Được", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Message message = new Message();
                                message.what = EditTimeTableController.SAVE_DATA_STATE_SAVE_ALL_DB;
                                mEditTimeTableController.sendMessage(message);
                                loadDataByLabel(week, arg1);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        loadDataByLabel(week, arg1);
                    }


                }
            };

    private void saveWeekAndYear(int week, int year) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WEEK_OF_YEAR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVE_PREFERENCE_WEEK, week);
        editor.putInt(SAVE_PREFERENCE_YEAR, year);
        editor.commit();
    }

    private boolean isFirstLauncher() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WEEK_OF_YEAR, Context.MODE_PRIVATE);
        int week = sharedPreferences.getInt(SAVE_PREFERENCE_WEEK, -1);
        if (week == -1) {
            return true;
        }
        return false;
    }

    private void loadDataByNextButton() {
        if (getWeek() != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, getYear());
            int maxWeek = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);

            if (getWeek() + 1 > maxWeek) {
                saveWeekAndYear(1, getYear() + 1);
                mTxtPeriod.setText("1");
            } else {
                Log.d(TAG, "onClick: " + (getWeek() + 1));
                saveWeekAndYear(getWeek() + 1, getYear());
                mTxtPeriod.setText(getWeek() + "");
            }

            Message message = new Message();
            message.what = EditTimeTableController.LOAD_DATA_STATE;
            message.obj = OBJ_LOAD_DATA_TIME_TABLE;
            mEditTimeTableController.sendMessage(message);
        }
    }

    private void loadDataByPrevButton() {
        if (getWeek() - 1 > 0) {
            Logger.d(TAG, "onClick: " + (getWeek() - 1));
            saveWeekAndYear(getWeek() - 1, getYear());
            mTxtPeriod.setText(getWeek() + "");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, getYear());
            int maxWeek = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
            saveWeekAndYear(maxWeek, getYear() - 1);
            mTxtPeriod.setText(getWeek());
        }

        Message message = new Message();
        message.what = EditTimeTableController.LOAD_DATA_STATE;
        message.obj = OBJ_LOAD_DATA_TIME_TABLE;
        mEditTimeTableController.sendMessage(message);
    }

    private void loadDataByLabel(int week, int year) {
        saveWeekAndYear(week, year);
        mTxtPeriod.setText(getWeek() + "");

        Message message = new Message();
        message.what = EditTimeTableController.LOAD_DATA_STATE;
        message.obj = OBJ_LOAD_DATA_TIME_TABLE;
        mEditTimeTableController.sendMessage(message);
    }

}

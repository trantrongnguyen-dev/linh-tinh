package fga.bu22.android.home.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import fga.bu22.android.R;
import fga.bu22.android.editlesson.EditLessonNameActivity;
import fga.bu22.android.home.adapter.LessonAdapter;
import fga.bu22.android.home.adapter.TimeTableAdapter;
import fga.bu22.android.home.controller.EditTimeTableController;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.models.TimeTableModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String OBJ_LOAD_DATA_TIME_TABLE = "EVENT_LOAD_DATA_TIME_TABLE";
    public static final String OBJ_LOAD_DATA_LESSON = "EVENT_LOAD_DATA_LESSON";

    private GridView mGridTimeTable;
    private GridView mGridLesson;

    private LessonAdapter mLessonAdapter;
    private TimeTableAdapter mTimeTableAdapter;

    private TimeTableModel mTimeTableModel;

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

    private List<TimeTable> mTimeTableList = new ArrayList<>();
    private List<Lesson> mLessonList = new ArrayList<>();
    private boolean mIsEditingLessonName = false;
    private boolean mIsDragToDelete = false;

    public TimeTableModel getTimeTableModel() {
        return mTimeTableModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        registerViewListenner();

        initModel();
        initController();
    }

    private void initController() {
        mEditTimeTableController = new EditTimeTableController(this);

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

    private void onUpdateModel(PropertyChangeEvent propertyChangeEvent) {
        switch (propertyChangeEvent.getPropertyName()) {
            case TimeTableModel.EVENT_LOAD_LESSON_LIST:
                mLessonList.clear();
                mLessonList.addAll(mTimeTableModel.getLessonList());

                mLessonAdapter.notifyDataSetChanged();
                break;
            case TimeTableModel.EVENT_LOAD_TIMETABLE:
                mTimeTableList.clear();
                mTimeTableList.addAll(mTimeTableModel.getTimeTableList());

                mTimeTableAdapter.notifyDataSetChanged();
                break;
            case TimeTableModel.EVENT_UPDATE_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);
                break;
            case TimeTableModel.EVENT_REPLACE_ITEM_TIMETABLE:
                mTimeTableAdapter = new TimeTableAdapter(MainActivity.this, (ArrayList<TimeTable>) propertyChangeEvent.getNewValue());
                mGridTimeTable.setAdapter(mTimeTableAdapter);
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
    }

    private void initBtnEditLessonName() {
        mBtnEditLessonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditLessonNameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initBtnCancelListener() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //to do something
            }
        });

    }

    private void initBtnOkListener() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to do something
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
//                                                    Message msg = new Message();
//
//                                                    msg.what = EventInfo.EVENT_DELETE_ITEM_TIMETABLE_UI;
//                                                    msg.obj = mTimetableAdapter.getItem(curPosition);
//
//                                                    //  mController.transitionToState(MainController.KEY_STATE_EDIT_TIME_TABLE);
//                                                    mController.sendMessage(msg);
//                                                    mIsDragToDelete = true;
//                                                    v.startAnimation(animZoomOut);
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
                                                            Lesson lesson = mLessonAdapter.getItem(curPosition);

                                                            Message message = new Message();
                                                            message.what = EditTimeTableController.DROP_STATE_ADD_NEW_ITEM;
                                                            message.obj = lesson;
                                                            message.arg1 = finalPositon;
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

}

package fga.bu22.android.editlesson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fga.bu22.android.R;
import fga.bu22.android.home.view.MainActivity;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTableModel;

public class EditLessonNameActivity extends AppCompatActivity {

    public static final String EXTRA_OLD_LESSON_NAME = "EXTRA_OLD_LESSON_NAME";
    public static final int CODE_EDIT_LESON_NAME_ACTIVITY = 1;
    public static final String EXTRA_NEW_LESSON_NAME = "EXTRA_NEW_LESSON_NAME";
    private TextView mTxtLessonName;
    private EditText mEdtLessonName;
    private Button mBtnOk, mBtnCancel;

    private String mOldLessonName;
    private String mNewLessonName;
    private TimeTableModel mTimeTableModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lesson_name);
        initViews();
        initData();
        registerViewListenner();
    }

    private void initData() {
        mOldLessonName = getIntent().getStringExtra(MainActivity.INTENT_LESSON_NAME);
        mTimeTableModel = (TimeTableModel) getIntent().getSerializableExtra(MainActivity.INTENT_LESSON_LIST);

        if (mOldLessonName != null) {
            mTxtLessonName.setText(mOldLessonName);
        }
    }

    private void registerViewListenner() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEdtLessonName.getText().toString().isEmpty()) {
                    if (!isExistName(mEdtLessonName.getText().toString())) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_NEW_LESSON_NAME, mEdtLessonName.getText().toString());
                        intent.putExtra(EXTRA_OLD_LESSON_NAME, mOldLessonName);
                        setResult(CODE_EDIT_LESON_NAME_ACTIVITY, intent);
                        finish();
                    } else {
                        Toast.makeText(EditLessonNameActivity.this, getResources().getString(R.string.exist_lesson_name), Toast.LENGTH_LONG).show();
                        mEdtLessonName.setText("");
                    }
                }
            }
        });
    }

    private void initViews() {
        mTxtLessonName = findViewById(R.id.txt_lesson_name);
        mEdtLessonName = findViewById(R.id.edit_lesson_name);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnCancel = findViewById(R.id.btn_cancel);
    }

    private boolean isExistName(String name) {
        ArrayList<Lesson> listLesson = (ArrayList<Lesson>) mTimeTableModel.getLessonList();
        for (Lesson lesson : listLesson) {
            if (name.equalsIgnoreCase(lesson.getName())) {
                return true;
            }
        }
        return false;
    }
}

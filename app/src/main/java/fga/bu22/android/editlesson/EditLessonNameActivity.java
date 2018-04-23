package fga.bu22.android.editlesson;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import fga.bu22.android.R;
import fga.bu22.android.home.view.MainActivity;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTableModel;

public class EditLessonNameActivity extends AppCompatActivity {

    public static final String EXTRA_OLD_LESSON_NAME = "EXTRA_OLD_LESSON_NAME";
    public static final int CODE_EDIT_LESON_NAME_ACTIVITY = 1;
    public static final String EXTRA_NEW_LESSON_NAME = "EXTRA_NEW_LESSON_NAME";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mTxtLessonName;
    private EditText mEdtLessonName;
    private Button mBtnOk, mBtnCancel;
    private ImageView mImgVoice;

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
                } else{
                    Toast.makeText(EditLessonNameActivity.this, getResources().getString(R.string.empty_lesson_name), Toast.LENGTH_LONG).show();
                }
            }
        });
        mImgVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechToText();
            }
        });
    }

    private void initViews() {
        mTxtLessonName = findViewById(R.id.txt_lesson_name);
        mEdtLessonName = findViewById(R.id.edit_lesson_name);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mImgVoice = findViewById(R.id.img_voice);
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
    private void speechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mEdtLessonName.setText(result.get(0));
                }
                break;
            }

        }
    }
}

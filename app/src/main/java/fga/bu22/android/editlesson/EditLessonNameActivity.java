package fga.bu22.android.editlesson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fga.bu22.android.R;

public class EditLessonNameActivity extends AppCompatActivity {
    private TextView mTxtLessonName;
    private EditText mEdtLessonName;
    private Button mBtnOk, mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lesson_name);
        initViews();
        registerViewListenner();
    }

    private void registerViewListenner() {
         {
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //to do something
                }
            });

            mBtnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //to do something
                }
            });
        }

    }

    private void initViews() {
        mTxtLessonName = findViewById(R.id.txt_lesson_name);
        mEdtLessonName = findViewById(R.id.edit_lesson_name);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnCancel = findViewById(R.id.btn_cancel);
    }
}

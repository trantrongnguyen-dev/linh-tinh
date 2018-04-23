package fga.bu22.android.home.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fga.bu22.android.R;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.util.Constant;

/**
 * Created by VuDuc on 4/18/2018.
 */

public class TimeTableAdapter extends BaseAdapter {

    public static final int MAX_COLUMN = 7;

    private List<TimeTable> mListTimeTable;
    private LayoutInflater mLayoutInflater;

    private int mCountRowTable;

    public TimeTableAdapter(Context context, List<TimeTable> listTimeTable) {
        mListTimeTable = listTimeTable;
        mLayoutInflater = LayoutInflater.from(context);
        mCountRowTable = 1;
    }

    @Override
    public int getCount() {
        return mListTimeTable == null ? 0 : mListTimeTable.size();
    }

    @Override
    public TimeTable getItem(int i) {
        if (mListTimeTable == null || i >= mListTimeTable.size())
            return null;
        return mListTimeTable.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_lesson, viewGroup, false);

            holder = new ViewHolder();
            holder.txtLessonName = view.findViewById(R.id.txt_lesson_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        initColumnHeader(i, holder.txtLessonName);
        initRowHeader(i, holder.txtLessonName);
        initData(i, holder.txtLessonName);

        return view;
    }

    private void initData(int i, TextView txtLessonName) {
        if (i > MAX_COLUMN && i % MAX_COLUMN != 0) {
            txtLessonName.setText(getItem(i).getLessonName());
        }
    }

    private void initColumnHeader(int i, TextView txtLessonName) {
        if (i > 0 && i < MAX_COLUMN) {
            txtLessonName.getRootView().setBackgroundColor(ContextCompat.getColor(txtLessonName.getContext(), R.color.timetable_header_bg));
            txtLessonName.setTextColor(ContextCompat.getColor(txtLessonName.getContext(), R.color.textIcons));
            txtLessonName.setText(Constant.WEEKDAY[i - 1]);
        }
    }

    private void initRowHeader(int i, TextView txtLessonName) {
        if (i > 0 && i % MAX_COLUMN == 0) {
            txtLessonName.getRootView().setBackgroundColor(ContextCompat.getColor(txtLessonName.getContext(), R.color.timetable_header_bg));
            txtLessonName.setTextColor(ContextCompat.getColor(txtLessonName.getContext(), R.color.textIcons));
            txtLessonName.setText(Constant.ROW_HEADER_PREFIX + mCountRowTable);
            mCountRowTable++;
        }
    }

    class ViewHolder {
        TextView txtLessonName;
    }
}

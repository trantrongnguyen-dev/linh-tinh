package fga.bu22.android.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fga.bu22.android.R;
import fga.bu22.android.models.Lesson;

/**
 * Created by VuDuc on 4/18/2018.
 */

public class LessonAdapter extends BaseAdapter {

    private List<Lesson> mListLesson;
    private LayoutInflater mLayoutInflater;

    public LessonAdapter(Context context, List<Lesson> listLesson) {
        mListLesson = listLesson;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mListLesson == null ? 0 : mListLesson.size();
    }

    @Override
    public Lesson getItem(int i) {
        if (mListLesson == null || i >= mListLesson.size())
            return null;
        return mListLesson.get(i);
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
        holder.txtLessonName.setText(mListLesson.get(i).getName());

        return view;
    }

    class ViewHolder {
        TextView txtLessonName;
    }
}

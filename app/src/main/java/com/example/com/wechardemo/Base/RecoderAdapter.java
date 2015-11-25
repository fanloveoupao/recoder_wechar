package com.example.com.wechardemo.Base;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.com.wechardemo.R;

import java.util.ArrayList;

/**
 * Created by bruse on 15/11/25.
 */
public class RecoderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Recoder> data;
    private LayoutInflater inflater;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    public RecoderAdapter(Context context, ArrayList<Recoder> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mMaxItemWidth = (int) (metrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (metrics.widthPixels * 0.15f);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_recoder, null);
            holder.textView = (TextView) convertView.findViewById(R.id.id_recoder_time);
            holder.view = convertView.findViewById(R.id.id_recoder_bg);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        //四舍五入
        holder.textView.setText(String.valueOf((int) Math.round(data.get(position).getTime())));
        //设置宽度
        ViewGroup.LayoutParams params = holder.view.getLayoutParams();
        params.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * data.get(position).getTime()));
        return convertView;
    }

    class Holder {
        TextView textView;
        View view;
    }
}

package com.example.com.wechardemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.com.wechardemo.R;

/**
 * Created by bruse on 15/11/22.
 */
public class DialogManage {
    //显示的dialog
    private Dialog mDialog;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLabel;
    //保持上下文的引用
    private Context mContext;

    public DialogManage(Context mContext) {
        this.mContext = mContext;

    }

    public void showRecoder() {
        //设定弹出框的风格
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recoder, null);
        mDialog.setContentView(view);
        //TODO 进行测试直接 mDialog.setContentView(R.layout.dialog_recoder)
        //下面进行初始化
        mIcon = (ImageView) mDialog.findViewById(R.id.dialog_recoder_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.dialog_recoder_vioce);
        mLabel = (TextView) mDialog.findViewById(R.id.dialog_recoder_label);
        mDialog.show();
    }

    public void recoding() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mVoice.setImageResource(R.drawable.v1);
            mLabel.setText("手指上滑 取消发送");
        }

    }

    public void wantTocancel() {
//        mIcon.setVisibility(View.VISIBLE);
        mVoice.setVisibility(View.GONE);
//        mLabel.setVisibility(View.VISIBLE);
        mIcon.setImageResource(R.drawable.cancel);
        mLabel.setText("松开手指 取消发送");
    }

    public void tooShort() {
        mIcon.setVisibility(View.VISIBLE);
        mVoice.setVisibility(View.GONE);
        mLabel.setVisibility(View.VISIBLE);
        mIcon.setImageResource(R.drawable.voice_to_short);
        mLabel.setText("时间太短");
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    //通过level更新voice
    public void updateVoiceLecel(int level) {
        int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
        mVoice.setImageResource(resId);
    }
}

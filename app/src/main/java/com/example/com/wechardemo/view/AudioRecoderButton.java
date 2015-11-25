package com.example.com.wechardemo.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.com.wechardemo.R;

import java.util.logging.LogRecord;


/**
 * Created by bruse on 15/11/21.
 */
public class AudioRecoderButton extends Button implements AudioManager.AudioStateListener {
    //三个状态
    private static final int STATE_NORMAL = 1;   //默认状态
    private static final int STATE_RECORDING = 2; //录音状态
    private static final int STATE_WANT_CANCEL = 3;
    //是否开始录音
    private boolean isRecoding = false;
    //当前的状态
    private int CurState = STATE_NORMAL;
    //判断是否越界
    private static final int DISTANCE_Y_CANCEL = 50;
    //弹出框
    private DialogManage mDialogManage;
    //音频
    private AudioManager mAudioManage;
    //进行计时
    private float mTime;
    //是否处罚longclick
    private boolean mReady;

    public AudioRecoderButton(Context context) {
        this(context, null);
    }

    public AudioRecoderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManage = new DialogManage(getContext());
        //声明
        String dir = Environment.getExternalStorageDirectory() + "/bruse_wechar";
        mAudioManage = AudioManager.getInstance(dir);
        mAudioManage.setAudioStateListener(this);
        //长按按钮
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManage.prepareAudio();
                return false;
            }
        });
    }

    /*
    * 录音完成后的回调的接口
    * */
    public interface onAudioFinishRecoderListener {
        //录音时长
        void onFinish(float seconds, String filePath);
    }

    private onAudioFinishRecoderListener audioListener;

    public void setOnAudioFinishRecoderListener(onAudioFinishRecoderListener audioListener) {
        this.audioListener = audioListener;
    }

    private static final int MSG_AUDIO_PREPARE = 0x11;
    private static final int MSG_VOICE_CHANGE = 0x12;
    private static final int MSG_DIALOG_MISS = 0x13;
    /*
    * 获取音量的线程
    * */
    private Runnable mGetVoiceRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecoding) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHnadle.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    private Handler mHnadle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUDIO_PREPARE:
                    //TODO 真正的显示在audio end prepare 之后
                    mDialogManage.showRecoder();
                    isRecoding = true;
                    //开启线程进行获取音量

                    new Thread(mGetVoiceRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManage.updateVoiceLecel(mAudioManage.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_MISS:
                    mDialogManage.dismissDialog();
                    break;
            }
        }
    };

    @Override
    public void WellPrepared() {
        mHnadle.sendEmptyMessage(MSG_AUDIO_PREPARE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //按住说话
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                //松手取消
                if (isRecoding) {
                    if (wantTocancel(x, y)) {
                        //根据坐标来判断是否要取消
                        changeState(STATE_WANT_CANCEL);
                        Log.i("Tag", "出界。。");
                    } else {
                        Log.i("TAG", " 回来");
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecoding || mTime < 0.6f) {
                    //执行了准备但是时间太短
                    mDialogManage.tooShort();
                    mAudioManage.cancelAudio();
                    //延迟
                    mHnadle.sendEmptyMessageAtTime(MSG_DIALOG_MISS, 1300);
                } else if (CurState == STATE_RECORDING) {
                    mDialogManage.dismissDialog();
                    //release
                    mAudioManage.releaseAudio();
                    //callBack
                    if (audioListener != null) {
                        audioListener.onFinish(mTime, mAudioManage.getCurrentFilePath());
                    }
                } else if (CurState == STATE_WANT_CANCEL) {
                    mDialogManage.dismissDialog();
                    mAudioManage.cancelAudio();
                    //cancel
                }
                reset();
                break;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }

    //重置到开始的状态
    private void reset() {
        isRecoding = false;
        mReady = false;
        changeState(STATE_NORMAL);
        mTime = 0;
    }

    private boolean wantTocancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (CurState != state) {
            CurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recoder_normal);
                    setText(R.string.str_notmal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recoder);
                    setText(R.string.str_recoding);
                    if (isRecoding) {
                        //TODO Dialog显示 ,Dialog.recoding
                        mDialogManage.showRecoder();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recoder);
                    setText(R.string.str_cancel);
                    //TODO Dialog的改变,Dialog,wanttocancel
                    mDialogManage.wantTocancel();
                    break;
            }
        }
    }


}

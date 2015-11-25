# recoder_wechar
#项目核心代码：
#路径的声明
        String dir = Environment.getExternalStorageDirectory() + "/bruse_wechar";
        mAudioManage = AudioManager.getInstance(dir);
#
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
#音量问题:
    private String genernalName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int max) {
        if (isPrepared) {
            //振幅的最大值mMediaRecoder.getMaxAmplitude()1-32767
            try {
//                return 4;
                return (int) max * mMediaRecoder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }
  #音频动画
  <?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:drawable="@drawable/v_anim1"
        android:duration="300"></item>
    <item
        android:drawable="@drawable/v_anim2"
        android:duration="300"></item>
    <item
        android:drawable="@drawable/v_anim3"
        android:duration="300"></item>
</animation-list>
#麦克风动画
  //通过level更新voice
    public void updateVoiceLecel(int level) {
        int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
        mVoice.setImageResource(resId);
    }
#采用线程Handler
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
     @Override
    public void WellPrepared() {
        mHnadle.sendEmptyMessage(MSG_AUDIO_PREPARE);
    }

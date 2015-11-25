package com.example.com.wechardemo.view;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by bruse on 15/11/22.
 */
public class AudioManager {
    private MediaRecorder mMediaRecoder;
    private String mDir;
    private String mCurrentFilePath;
    private static AudioManager Instance;
    private boolean isPrepared;

    public AudioManager(String dir) {
        mDir = dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 回调表示准备完毕
     */
    public interface AudioStateListener {
        void WellPrepared();
    }

    public AudioStateListener mListener;

    public void setAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public static AudioManager getInstance(String dir) {

        if (Instance == null) {
            //同步
            synchronized (AudioManager.class) {
                if (Instance == null) {
                    Instance = new AudioManager(dir);
                }
            }
        }
        return Instance;
    }

    public void prepareAudio() {

        try {
            //创建文件夹,路径
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();
            //文件名称
            String fileName = genernalName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecoder = new MediaRecorder();
            //设置输出文件
            mMediaRecoder.setOutputFile(file.getAbsolutePath());
            //设置音频的音频源为麦克风
            mMediaRecoder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频的格式
            mMediaRecoder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频的编码
            mMediaRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //进行准备
            mMediaRecoder.prepare();

            mMediaRecoder.start();
            //准备工作结束
            isPrepared = true;
            //通知按钮可以进行录音了
            if (mListener != null) {
                mListener.WellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //随机生成一个名称
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

    public void releaseAudio() {
        mMediaRecoder.stop();
        mMediaRecoder.release();
        mMediaRecoder = null;
    }

    public void cancelAudio() {
        releaseAudio();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }
}

package com.example.com.wechardemo;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.com.wechardemo.Base.Recoder;
import com.example.com.wechardemo.Base.RecoderAdapter;
import com.example.com.wechardemo.view.AudioRecoderButton;
import com.example.com.wechardemo.view.MediaManager;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private ListView listView;
    private RecoderAdapter adapter;
    private ArrayList<Recoder> arrayList;
    private AudioRecoderButton mAudioRecoderButton;
    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        去掉标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.list_news);
        mAudioRecoderButton = (AudioRecoderButton) findViewById(R.id.btn_recoding);
        arrayList = new ArrayList<Recoder>();
        adapter = new RecoderAdapter(this, arrayList);
        listView.setAdapter(adapter);
        mAudioRecoderButton.setOnAudioFinishRecoderListener(new AudioRecoderButton.onAudioFinishRecoderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recoder recoder = new Recoder();
                recoder.setFilePath(filePath);
                recoder.setTime(seconds);
                arrayList.add(recoder);
                adapter.notifyDataSetChanged();
                listView.setSelection(arrayList.size() - 1);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO 动画播放
                //一次播放一个
                if (animView != null) {
                    //停止动画
                    animView.setBackgroundResource(R.drawable.adj);
                    animView = null;
                }
                //使用帧动画播放
                animView = view.findViewById(R.id.id_recoder_anim);
                //设置播放动画
                animView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
                //播放
                anim.start();
                //TODO 音频播放
                MediaManager.playSounds(arrayList.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}

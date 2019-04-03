package com.example.kanki.simonsays;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.sip.SipAudioCall;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;

import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {
    int i;
    View contenview;
    private String ChoicesNumOfTimes[] = {"1", "2", "3", "4"};
    private int NumOfTimes=3;
    private boolean waza[]={true,true,true,true};
    private CheckBox CBwaza[]=new CheckBox[ChoicesNumOfTimes.length];
    private MediaPlayer mediaPlayer;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Where","OnCreate");
        // 音量調整を端末のボタンに任せる
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_home);
        contenview=findViewById(R.id.activity_home);
    }
    public void GoSetting(View view){
        Log.i("Where","GoSetting");
        setContentView(R.layout.setting);
        contenview=findViewById(R.id.setting);
        Resources resources=getResources();
        for (int i = 0; i < 4/*技の個数*/; ++i) {
            CBwaza[i]=findViewById(resources.obtainTypedArray(R.array.id_skills).getResourceId(i, 0));
        }
        resources.obtainTypedArray(R.array.id_skills).recycle();
        for(i=0;i<4/*技の個数*/;i++){
            CBwaza[i].setChecked(waza[i]);
        }

        Spinner spinner =findViewById(R.id.numspinner);
        // ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ChoicesNumOfTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//
        // spinner に adapter をセット
        spinner.setAdapter(adapter);
        // リスナーを登録
        spinner.setSelection(NumOfTimes,false);//animateをfalseしておかないと落ちる
        //0から始まる
        AdapterView.OnItemSelectedListener ISListener = null;
        ISListener = new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Where", "onItemSelected");
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                NumOfTimes = valueOf(item);
                Log.i("Where", "回数" + NumOfTimes);
            }

                //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Where", "onNothingSelected");
            }
        };
        spinner.setOnItemSelectedListener(ISListener);
    }
    public void GoHome(View view){
        Log.i("Where","GoHome");
        if(mediaPlayer!=null) {
            audioStop();
        }
        if(contenview.getId()==R.id.setting) {
            for (i = 0; i < 4/*技の個数*/; i++)
                waza[i] = CBwaza[i].isChecked();
        }
        setContentView(R.layout.activity_home);
        contenview=findViewById(R.id.activity_home);
    }
    public void GoGame(View view){
        Log.i("Where","GoGame");
        setContentView(R.layout.activity_game);
        contenview=findViewById(R.id.activity_game);
        runnable.run();
     //   audioInstruct();
    }

    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            audioInstruct();
            Log.i("Where","postDelayed");
            handler.postDelayed(this, 10000);

        }
    };
    private void audioInstruct(){
        final String filename="Simonsays.mp3";
        Log.i("Where", "audioInstruct");
        audioPlay(filename);
     //   mediaPlayer.setOnCompletionListener();
    }

    private boolean audioSetup(String filename){
        Log.i("Where","audioSetup");
        boolean fileCheck = false;
        // インタンスを生成
        mediaPlayer = new MediaPlayer();
        // assetsから mp3 ファイルを読み込み
        try(AssetFileDescriptor afdescripter = getAssets().openFd(filename)){
            // MediaPlayerに読み込んだ音楽ファイルを指定
            mediaPlayer.setDataSource(afdescripter.getFileDescriptor(),afdescripter.getStartOffset(),afdescripter.getLength());
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.prepare();
            fileCheck = true;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return fileCheck;
    }
    private void audioPlay(final String filename) {
        Log.i("Where","audioPlay:"+filename);
        if (mediaPlayer == null) {
            Log.i("Where","medianull");
            // audio ファイルを読出し
            if (audioSetup(filename)){
            }
            else{
                return;
            }
        }
        /*else{
            // 繰り返し再生する場合
            mediaPlayer.stop();
            mediaPlayer.reset();
            // リソースの解放
            mediaPlayer.release();
        }*/
        // 再生する
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioStop();
                switch(filename){
                    case "Simonsays.mp3":
                        // mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener());
                        audioPlay(ChooseSkill()+".mp3");
                        break;
                    case "カスケード.mp3":
                    case "ハーフシャワー.mp3":
                    case "ピルエット.mp3":
                    case "シャワー.mp3":
                        audioPlay("do.mp3");
                        break;
                    case "do.mp3":
                        //   Log.i("Where","notify");
                        //     notify();
                        break;
                }
            }
        });
        Log.i("Where","start");
      /*  try{
            Log.i("Where","wait");
            Thread.sleep(10000);
        }catch (InterruptedException e){
            Log.i("Where", "InterruptedException");
        }*/


    }

    private String ChooseSkill(){
        TextView TVinstruct;
        int r;
        String[] skills = getResources().getStringArray(R.array.string_skills);
        TVinstruct =findViewById(R.id.instruct);
        Random rnd=new Random();
        do {
            r = rnd.nextInt(4);
        }while(!waza[r]);
        TVinstruct.setText(skills[r] + "をしてください");
        return skills[r];
    }

    private void audioStop() {
        // 再生終了
        mediaPlayer.stop();
        // リセット
        mediaPlayer.reset();
        // リソースの解放
        mediaPlayer.release();
        mediaPlayer = null;
    }

}

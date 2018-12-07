package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import database.DatabaseWrapper;

public class PracticeActivity extends AppCompatActivity {

    private ArrayList<Item> myItems;
    private ArrayList<Integer> positionShuffle;
    private int[] curShuffle;
    private Button[] myButtons;
    private TextView mainText;
    private int countVar;
    private int roundVar;
    private Handler roundHandler;
    private Handler delayExitHandler;
    private Handler promptHandler;
    private boolean buttonsActive;
    private MetricsPractice myBox;
    private int tarBase;
    private int grpBase;
    private CountDownFragment countDown;
    private long startTime;
    private DatabaseWrapper myWrap;
    private int outcome;
    private MediaPlayer myMp=new MediaPlayer();
    private long promptDelay;

    private boolean playAudBool=false;
    private boolean showPinyinBool=true;
    private boolean showEnglishBool=true;
    private boolean delayTextBool=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initValues();

        ImageView sBut=(ImageView)findViewById(R.id.audBut);
        if(grpBase<=3){
            sBut.setVisibility(View.VISIBLE);
        }else {
            sBut.setVisibility(View.INVISIBLE);
        }
        roundHandler.postDelayed(delayedStart,1000);
        startTime=System.currentTimeMillis();
    }

    private Runnable delayedStart=new Runnable(){
        public void run(){
            roundHandler.post(round);
            countDown.startTimer();
        }
    };

    public void onStop(){
        super.onStop();
        long endTime=System.currentTimeMillis();
        long passedTime=(endTime-startTime)/1000;
        myWrap.updateStats(countVar,passedTime,-1,-1);
        roundHandler.removeCallbacksAndMessages(null);
        promptHandler.removeCallbacksAndMessages(null);
        delayExitHandler.removeCallbacksAndMessages(null);
    }

    public void onDestroy(){
        super.onDestroy();
        myWrap.closeDatabase();
        myMp.release();
        roundHandler.removeCallbacksAndMessages(null);
        delayExitHandler.removeCallbacksAndMessages(null);
    }

    private void initValues(){
        myBox=new MetricsPractice();
        countDown=(CountDownFragment)getSupportFragmentManager().findFragmentById(R.id.activityPracticeFragment);
        Intent myInt=this.getIntent();
        tarBase=myInt.getIntExtra("contentSelected",0);
        grpBase=myInt.getIntExtra("groupSelected",0);
        myWrap=new DatabaseWrapper(this,grpBase,tarBase);
        myItems=myWrap.getItems();
        roundHandler=new Handler();
        delayExitHandler=new Handler();
        promptHandler=new Handler();
        mainText=(TextView)findViewById(R.id.activityPracticeText);
        mainText.setText("");
        myButtons=new Button[7];
        positionShuffle=new ArrayList<Integer>();
        for(int i=0;i<7;i++){
            int myIn=getResources().getIdentifier("activityPracticeButton"+i,"id",getPackageName());
            myButtons[i]=(Button)findViewById(myIn);
            myButtons[i].setVisibility(View.INVISIBLE);
            positionShuffle.add(i);
        }
        countVar=-1;
    }

    private Runnable round=new Runnable(){
        public void run(){
            countVar++;
            curShuffle=myBox.getRoundVar();
            roundVar=curShuffle[0];
            showPrompt();
            Collections.shuffle(positionShuffle);
            setButtons();
            if(playAudBool)audClick(null);
        }
    };

    private void showPrompt(){
        if(promptDelay>0)mainText.setText("");
        promptHandler.removeCallbacksAndMessages(null);
        promptHandler.postDelayed(setTextFields,promptDelay);
    }

    private Runnable setTextFields=new Runnable(){
        public void run(){
            String myPinzi=myItems.get(roundVar).getPinzi();
            String myEngzi=myItems.get(roundVar).getEngzi();
            if(showEnglishBool){
                if(showPinyinBool){
                    mainText.setText(Html.fromHtml(myPinzi+"<br><b>"+myEngzi+"</b>"));
                }else{
                    mainText.setText(Html.fromHtml("<b>"+myEngzi+"</b>"));
                }
            }else{
                if(showPinyinBool){
                    mainText.setText(Html.fromHtml(myPinzi));
                }else{
                    mainText.setText("");
                }
            }
        }
    };

    private void hitCorrect(int i){
        countDown.addTime(75);
        promptHandler.removeCallbacksAndMessages(null);
        if(myBox.timeDelay>300){
            mainText.setText("Correct");
            mainText.setText(Html.fromHtml("<b>Correct</b>"));
            for(Button myBut:myButtons) myBut.setVisibility(View.INVISIBLE);
            myButtons[i].setVisibility(View.VISIBLE);
        }
        myBox.hit();
        if(!myBox.FINISHED){
            if(myBox.SDMODE){
                countDown.altTimerInc(true);
            }else{
                countDown.altTimerInc(false);
            }
            if(myBox.SDFLAG){
                beginSDMode();
            }else{
                roundHandler.postDelayed(round,myBox.timeDelay);
            }
        }else{
            countDown.stopTimer();
            myWrap.incPracticeProgress();
            mainText.setText(Html.fromHtml("<b>Well Done!</b>"));
            outcome=0;
            endGame();
        }
    }

    private void beginSDMode(){
        countDown.addTime(300);
        myBox.SDFLAG=false;
        roundHandler.postDelayed(round,5000);
        for(int i=0;i<7;i++){
            myButtons[i].setVisibility(View.INVISIBLE);
        }
        mainText.setText(Html.fromHtml("<b>Sudden Death Mode Begins!</b>"));
        Toast myToast=Toast.makeText(getApplicationContext(), "This is sudden death mode", 0);
        myToast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        myToast.show();
        Toast myToast2=Toast.makeText(getApplicationContext(), "One mistake will end this round", 0);
        myToast2.setGravity(Gravity.CENTER_VERTICAL,0,0);
        myToast2.show();
    }

    private void hitWrong(int i){
        myButtons[i].setVisibility(View.INVISIBLE);
        myBox.miss();
        if(myBox.SDMODE){
            myWrap.incPracticeProgress();
            mainText.setText(Html.fromHtml("<b>Sudden Death!</b>"));
            countDown.stopTimer();
            outcome=3;
            endGame();
        }else{
            buttonsActive=true;
        }
    }

    private void setButtons(){
        for(int i=0;i<7;i++){
            Integer myIn=curShuffle[i];
            Integer myIn2=positionShuffle.get(i);
            String myStr=myItems.get(myIn).getHanzi();
            myButtons[myIn2].setText(myStr);
            myButtons[i].setVisibility(View.VISIBLE);
        }
        buttonsActive=true;
    }

    public void buttonClick(View v){
        if(buttonsActive){
            for(int i=0;i<7;i++){
                if(v==myButtons[i]){
                    buttonsActive=false;
                    if(i==positionShuffle.get(0)){
                        hitCorrect(i);
                    }else{
                        hitWrong(i);
                    }
                }
            }
        }
    }

    public void audClick(View v){
        if(grpBase<4) {
            String grpString = "group_" + grpBase;
            String tarString = "tar_set_" + tarBase;
            String roundStr = "audio/" + grpString + "/" + tarString + "/s_" + roundVar + ".mp3";
            try {
                AssetFileDescriptor afd = this.getAssets().openFd(roundStr);
                myMp.reset();
                myMp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                myMp.prepare();
                myMp.start();
            } catch (Exception e) {
            }
        }
    }

    public void settingsClick(View v){
        Intent myInt=new Intent(getApplicationContext(),SettingsActivityPractice.class);
        myInt.putExtra("playAudBool",playAudBool);
        myInt.putExtra("showPinyinBool",showPinyinBool);
        myInt.putExtra("showEnglishBool",showEnglishBool);
        myInt.putExtra("delayTextBool",delayTextBool);
        startActivityForResult(myInt,0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        boolean altBool=data.getBooleanExtra("altBool",false);
        if(resultCode==0){
            if(altBool){
                Boolean oldPlayAudBool=playAudBool;
                playAudBool=data.getBooleanExtra("playAudBool",false);
                showPinyinBool=data.getBooleanExtra("showPinyinBool",false);
                showEnglishBool=data.getBooleanExtra("showEnglishBool",false);
                delayTextBool=data.getBooleanExtra("delayTextBool",false);
                if(playAudBool&&!oldPlayAudBool)audClick(null);
                setButtons();
                if(delayTextBool){
                    promptDelay=1000;
                }else{
                    promptDelay=0;
                }
                showPrompt();
            }
        }
    }

    public void timeOut(){
        this.runOnUiThread(timeOut2);
    }

    private Runnable timeOut2=new Runnable(){
        public void run(){
            buttonsActive=false;
            mainText.setText(Html.fromHtml("<b>Time over</b>"));
            if(myBox.SDMODE){
                myWrap.incPracticeProgress();
                outcome=2;
            }else{
                outcome=1;
            }
            endGame();
            roundHandler.removeCallbacks(round);
        }
    };

    private void endGame(){
        for(int i=0;i<7;i++){
            myButtons[i].setVisibility(View.INVISIBLE);
        }
        delayExitHandler.postDelayed(endGameExit, 2000);
    }

    private Runnable endGameExit=new Runnable(){
        public void run(){
            long endTime=System.currentTimeMillis();
            long passedTime=(endTime-startTime)/1000;
            int myProg=myBox.getProg();
            Intent myInt=new Intent(getApplicationContext(),EndActivity.class);
            myInt.putExtra("duration", passedTime);
            myInt.putExtra("outcome",outcome);
            myInt.putExtra("myProg",myProg);
            myInt.putExtra("activity",1);
            myInt.putExtra("contentSelected", tarBase);
            myInt.putExtra("groupSelected", grpBase);
            myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(myInt);
            overridePendingTransition(0,0);
            finish();
        }
    };
}

package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import database.DatabaseWrapper;

public class ReviewActivity extends AppCompatActivity {

    private ArrayList<Item> myItems;
    private TextView revealedLine0;
    private TextView revealedLine1;
    private TextView topText;
    private Button butGood;
    private Button butAgain;
    private TextView roundText;
    private Handler roundHandler;
    private int roundInt;
    private int countInt;
    private boolean showBool;
    private int[] itemOrderArr=new int[30];
    private int viewsCount;
    private int tarBase;
    private int grpBase;
    private long startTime;
    private int repeatsCount;
    private DatabaseWrapper myWrap;
    private MediaPlayer myMp=new MediaPlayer();

    private boolean playAudBool=false;
    private boolean showPinyinBool=true;
    private boolean showEnglishBool=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initValues();

        ImageView sBut=(ImageView)findViewById(R.id.audBut);
        if(grpBase<=3){
            sBut.setVisibility(View.VISIBLE);
        }else {
            sBut.setVisibility(View.INVISIBLE);
        }

        roundHandler.post(round);
    }
    @Override
    protected void onResume(){
        super.onResume();
        startTime=System.currentTimeMillis();
    }

    private void initValues(){
        Intent myInt=this.getIntent();
        tarBase=myInt.getIntExtra("contentSelected",0);
        grpBase=myInt.getIntExtra("groupSelected",0);
        myWrap=new DatabaseWrapper(this,grpBase,tarBase);
        myItems=myWrap.getItems();
        butGood=(Button)findViewById(R.id.activityReviewButGood);
        butAgain=(Button)findViewById(R.id.activityReviewButAgain);
        revealedLine0=(TextView)findViewById(R.id.activityReviewRevealedLine0);
        revealedLine1=(TextView)findViewById(R.id.activityReviewRevealedLine1);
        topText=(TextView)findViewById(R.id.activityReviewTopText);
        roundText=(TextView)findViewById(R.id.activityReviewRoundText);
        butGood.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        butAgain.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        roundText.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        roundHandler=new Handler();
        countInt=-1;
        repeatsCount=0;
        itemOrderArr=ranSeq();
        viewsCount=0;
    }

    private Runnable round=new Runnable(){
        public void run(){
            countInt++;
            if(countInt<30){
                newRound();
            }else{
                endGame();
            }
        }
    };

    private void newRound(){
        roundInt=itemOrderArr[countInt];
        String hStr=myItems.get(roundInt).getHanzi();
        String pStr=myItems.get(roundInt).getPinzi();
        String eStr=myItems.get(roundInt).getEngzi();
        topText.setText(hStr);
        revealedLine0.setText(pStr);
        revealedLine1.setText(eStr);
        hideAnswer();
        String rStr=(countInt+1)+"/30";
        roundText.setText(rStr);
        if(playAudBool)audClick(null);
    }

    private void endGame(){
        myWrap.incReviewProgress();
        long endTime=System.currentTimeMillis();
        long passedTime=(endTime-startTime)/1000;
        Intent myInt=new Intent(getApplicationContext(),EndActivity.class);
        myInt.putExtra("contentSelected", tarBase);
        myInt.putExtra("groupSelected", grpBase);
        myInt.putExtra("duration", passedTime);
        myInt.putExtra("repeats", repeatsCount);
        myInt.putExtra("activity",2);
        myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myInt);
        overridePendingTransition(0,0);
        finish();
    }

    private void hideAnswer(){
        showBool=false;
        butGood.setText("Show");
        butAgain.setVisibility(View.INVISIBLE);
        revealedLine0.setVisibility(View.INVISIBLE);
        revealedLine1.setVisibility(View.INVISIBLE);
    }

    private void showAnswer(){
        viewsCount++;
        showBool=true;
        butGood.setText("Good");
        butAgain.setVisibility(View.VISIBLE);
        revealedLine0.setVisibility(View.VISIBLE);
        revealedLine1.setVisibility(View.VISIBLE);
    }

    public void againClick(View v){
        proder();
        repeatsCount++;
        roundHandler.post(round);
    }

    public void goodClick(View v){
        if(showBool){
            roundHandler.post(round);
        }else{
            showAnswer();
        }
    }

    private void proder(){
        int tmp=5;
        if(countInt==29){
            countInt=27;
        }else{
            if(countInt>24)tmp=29-countInt;
            for(int i=countInt;i<countInt+tmp;i++)itemOrderArr[i]=itemOrderArr[i+1];
            itemOrderArr[countInt+tmp]=roundInt;
            countInt--;
        }
    }


    public void audClick(View v){
        if(grpBase<4) {
            String grpString = "group_" + grpBase;
            String tarString = "tar_set_" + tarBase;
            String roundStr = "audio/" + grpString + "/" + tarString + "/s_" + roundInt + ".mp3";
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
        Intent myInt=new Intent(getApplicationContext(),SettingsActivityReview.class);
        myInt.putExtra("playAudBool",playAudBool);
        myInt.putExtra("showPinyinBool",showPinyinBool);
        myInt.putExtra("showEnglishBool",showEnglishBool);
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
                if(playAudBool&&!oldPlayAudBool)audClick(null);
                if(showPinyinBool){
                    revealedLine0.setVisibility(View.VISIBLE);
                }else{
                    revealedLine0.setVisibility(View.GONE);
                }
                if(showEnglishBool){
                    revealedLine1.setVisibility(View.VISIBLE);
                }else{
                    revealedLine1.setVisibility(View.GONE);
                }
            }
        }
    }

    private int[] ranSeq(){
        int[] outArr=new int[30];
        ArrayList<Integer> tArr=new ArrayList<Integer>();
        for(int i=0;i<30;i++){
            tArr.add(i);
        }
        Collections.shuffle(tArr);
        for(int i=0;i<30;i++){
            outArr[i]=tArr.get(i);
        }
        return outArr;
    }

    public void onStop(){
        super.onStop();
        long endTime=System.currentTimeMillis();
        long passedTime=(endTime-startTime)/1000;
        myWrap.updateStats(viewsCount,passedTime,-1,-1);
    }

    public void onDestroy(){
        super.onDestroy();
        myWrap.closeDatabase();
        roundHandler.removeCallbacks(round);
    }
}

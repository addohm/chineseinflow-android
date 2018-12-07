package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import database.DatabaseWrapper;

public class LearnActivity extends AppCompatActivity {

    //this myBox determines which characters appear
    private MetricsLearn myBox;
    private TextView mainText;
    private Button[] buttons;
    private int tarBase;
    private int grpBase;
    private DatabaseWrapper myWrap;
    private ArrayList<Item> myItems;
    private boolean buttonsActive;
    private Handler hintDelayHandler;
    private Handler delayExitHandler;
    private Handler promptHandler;
    private Handler myHandler;
    private ArrayList<Integer> curShuffle;
    private int roundVar;
    private int countVar;
    private ArrayList<Integer> positionShuffle;
    private LinearLayout buttonLayout0;
    private LinearLayout buttonLayout1;
    private LinearLayout buttonLayout2;
    private float defaultTextSize;
    private float largerTextSize;
    private float defaultTopSize;
    private float smallerTopSize;
    private int curDisplayVal;
    private int lastDisplayPos;
    private long startTime;
    private int missesCount;
    private boolean hintShowing=false;
    private AsyncSave myAsyncSave;
    private MediaPlayer myMp=new MediaPlayer();
    private long promptDelay;

    private boolean playAudBool=false;
    private boolean showPinyinBool=true;
    private boolean showEnglishBool=true;
    private boolean showTextBool=true;
    private boolean delayTextBool=false;
    private boolean showPinyinButtonsBool=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initValues();
        restoreProgress();
        ImageView bBut=(ImageView)findViewById(R.id.audButBig);
        ImageView sBut=(ImageView)findViewById(R.id.audBut);
        if(grpBase<=3){
            if(showTextBool){
                bBut.setVisibility(View.GONE);
                sBut.setVisibility(View.VISIBLE);
            }else{
                bBut.setVisibility(View.VISIBLE);
                sBut.setVisibility(View.INVISIBLE);
            }
        }else{
            bBut.setVisibility(View.GONE);
            sBut.setVisibility(View.INVISIBLE);
        }
        myHandler.post(round);
    }

    private void restoreProgress(){
        int[] progInts=myWrap.getProgress();
        if(progInts[0]==0){
            myBox.init();
            hintShowing=true;
            hintDelayHandler.postDelayed(delayHint,4000);
        }else{
            int[] myInts=myWrap.getLevels();
            myBox.reinit(progInts[0],myInts);
            if(progInts[0]<9)myBox.setRestartStageCount(progInts[1]);
        }
    }

    private Runnable delayHint=new Runnable(){
        public void run(){
            ImageView hint0=(ImageView)findViewById(R.id.activityLearnHint);
            ImageView hint1=(ImageView)findViewById(R.id.activityLearnHintDummy);
            hint0.setVisibility(View.VISIBLE);
            hint1.setVisibility(View.VISIBLE);
        }
    };

    private void removeHint(){
        hintShowing=false;
        ImageView hint0=(ImageView)findViewById(R.id.activityLearnHint);
        ImageView hint1=(ImageView)findViewById(R.id.activityLearnHintDummy);
        hint0.setVisibility(View.GONE);
        hint1.setVisibility(View.GONE);
        hintDelayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume(){
        super.onResume();
        startTime=System.currentTimeMillis();
    }

    @Override
    public void onStop(){
        super.onStop();
        long endTime=System.currentTimeMillis();
        long passedTime=(endTime-startTime)/1000;
        int stageCount=myBox.getStageCount();
        int stageVar=myBox.getStageVar();
        myWrap.updateStats(countVar,passedTime,stageVar,stageCount);
        hintDelayHandler.removeCallbacksAndMessages(null);
        myHandler.removeCallbacksAndMessages(null);
        delayExitHandler.removeCallbacksAndMessages(null);
        promptHandler.removeCallbacksAndMessages(null);
    }

    public void onDestroy(){
        super.onDestroy();
        if(myAsyncSave!=null){
            myAsyncSave.cancel(true);
        }
        myMp.release();
        myWrap.closeDatabase();
    }

    private void initValues(){
        myBox=new MetricsLearn();
        Intent myInt=this.getIntent();
        tarBase=myInt.getIntExtra("contentSelected",0);
        grpBase=myInt.getIntExtra("groupSelected",0);
        myWrap=new DatabaseWrapper(this,grpBase,tarBase);
        myItems=myWrap.getItems();
        myHandler=new Handler();
        hintDelayHandler=new Handler();
        delayExitHandler=new Handler();
        promptHandler=new Handler();
        mainText=(TextView)findViewById(R.id.activityLearnMainText);
        buttons=new Button[8];
        positionShuffle=new ArrayList<Integer>();
        buttonLayout0=(LinearLayout)findViewById(R.id.learnLay0);
        buttonLayout1=(LinearLayout)findViewById(R.id.learnLay1);
        buttonLayout2=(LinearLayout)findViewById(R.id.learnLay2);
        for(int i=0;i<8;i++){
            int myIn=getResources().getIdentifier("learnButton"+i,"id",getPackageName());
            buttons[i]=(Button)findViewById(myIn);
        }
        defaultTextSize=buttons[0].getTextSize();
        largerTextSize=(float) (defaultTextSize*1.5);
        defaultTopSize=mainText.getTextSize();
        smallerTopSize=(float) (defaultTopSize/2);
        countVar=0;
        curDisplayVal=0;
        lastDisplayPos=0;
        missesCount=0;
        promptDelay=0;
    }

    private Runnable round=new Runnable(){
        public void run() {
            countVar++;
            curShuffle=myBox.newRound();
            roundVar=curShuffle.get(0);
            setPositionShuffle();
            setButtons();
            showPrompt();
            setAppearance();
            saveNowCheck();
            buttonsActive=true;
            if(playAudBool)audClick(null);
        }
    };

    private void saveNowCheck(){
        if(myBox.checkSaveNow()){
            ArrayList<MetricsLearn.MyPair> saveNowPairs=myBox.getSaveNowPairs();
            myWrap.saveLevels(saveNowPairs);
        }
    }

    public void buttonClick(View v){
        if(buttonsActive){
            buttonsActive=false;
            for(int i=0;i<8;i++){
                if(v==buttons[i]){
                    if(i==positionShuffle.get(0)){
                        hitCorrect(i);
                    }else{
                        hitWrong(i);
                    }
                }
            }
        }
        if(hintShowing)removeHint();
    }

    private void hitCorrect(int i){
        promptHandler.removeCallbacksAndMessages(null);
        for(Button myBut:buttons) {
            if(myBut.getVisibility()==View.VISIBLE){
                myBut.setVisibility(View.INVISIBLE);
            }
        }
        buttons[i].setVisibility(View.VISIBLE);
        ArrayList<MetricsLearn.MyPair> gotPairs=myBox.updateStats();
        if(gotPairs.size()>0){
            if(myAsyncSave==null||myAsyncSave.getStatus()!= AsyncTask.Status.RUNNING){
                myAsyncSave=new AsyncSave();
                myAsyncSave.execute(gotPairs);
            }
        }
        if(myBox.FINISHED){
            endGame();
        }else{
            myHandler.postDelayed(round,myBox.delayValue);
        }
    }

    private void endGame(){
        for(Button myBut:buttons) {
            myBut.setVisibility(View.INVISIBLE);
        }
        mainText.setText(Html.fromHtml("<b>The End</b>"));
        delayExitHandler.postDelayed(endGameExit, 2000);
    }

    private Runnable endGameExit=new Runnable(){
        public void run(){
            long endTime=System.currentTimeMillis();
            long passedTime=(endTime-startTime)/1000;
            Intent myInt=new Intent(getApplicationContext(),EndActivity.class);
            myInt.putExtra("duration", passedTime);
            myInt.putExtra("misses",missesCount);
            myInt.putExtra("activity",0);
            myInt.putExtra("contentSelected", tarBase);
            myInt.putExtra("groupSelected", grpBase);
            myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(myInt);
            overridePendingTransition(0,0);
            finish();
        }
    };

    private void hitWrong(int i){
        missesCount++;
        buttons[i].setVisibility(View.INVISIBLE);
        buttonsActive=true;
        myBox.missedFlag=true;
    }

    private void setPositionShuffle(){
        for(int i=positionShuffle.size()-1;i>=0;i--){
            positionShuffle.remove(i);
        }
        for(int i=0;i<myBox.itemDisplayNumber;i++){
            positionShuffle.add(i);
        }
        do{
            Collections.shuffle(positionShuffle);
        }while(lastDisplayPos==positionShuffle.get(0)&&positionShuffle.size()>3);
    }

    private void setButtons(){
        for(int i=0;i<positionShuffle.size();i++){
            Integer myIn=curShuffle.get(i);
            Integer myIn2=positionShuffle.get(i);
            if(myBox.itemDisplayType==0){
                String myPinzi=myItems.get(myIn).getPinzi();
                String myEngzi=myItems.get(myIn).getEngzi();
                if(showEnglishBool){
                    if(showPinyinButtonsBool){
                        buttons[myIn2].setText(Html.fromHtml(myPinzi+"<br><b>"+myEngzi+"</b>"));
                    }else{
                        buttons[myIn2].setText(Html.fromHtml("<b>"+myEngzi+"</b>"));
                    }
                }else{
                    if(showPinyinBool){
                        buttons[myIn2].setText(Html.fromHtml(myPinzi));
                    }else{
                        buttons[myIn2].setText("");
                    }
                }
                buttons[myIn2].setLineSpacing(0,(float) 1.1);
                buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,defaultTextSize);
            }else{
                String myStr=myItems.get(myIn).getHanzi();
                buttons[myIn2].setText(myStr);
                buttons[myIn2].setLineSpacing(0,(float)1);
                buttons[i].setTextSize(TypedValue.COMPLEX_UNIT_PX,largerTextSize);
            }
        }
    }

    private void showPrompt(){
        if(promptDelay>0)mainText.setText("");
        promptHandler.removeCallbacksAndMessages(null);
        promptHandler.postDelayed(setTextFields,promptDelay);
    }

    private Runnable setTextFields=new Runnable(){
        public void run(){
        if(myBox.itemDisplayType==0){
            String myStr=myItems.get(roundVar).getHanzi();
            mainText.setText(myStr);
            mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX,defaultTopSize);
        }else{
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
            mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX,smallerTopSize);
        }
        ImageView bBut=(ImageView)findViewById(R.id.audButBig);
        ImageView sBut=(ImageView)findViewById(R.id.audBut);
        if (showTextBool) {
            mainText.setVisibility(View.VISIBLE);
        } else {
            mainText.setVisibility(View.GONE);
        }
        if(grpBase<=3){
            if (showTextBool) {
                bBut.setVisibility(View.GONE);
                sBut.setVisibility(View.VISIBLE);
            } else {
                bBut.setVisibility(View.VISIBLE);
                sBut.setVisibility(View.INVISIBLE);
            }
        }else{
            bBut.setVisibility(View.GONE);
            sBut.setVisibility(View.INVISIBLE);
        }
        }
    };

    private void setAppearance(){
        int displayVal=positionShuffle.size();
        if(displayVal!=curDisplayVal){
            buttonLayout1.setVisibility(View.GONE);
            buttonLayout2.setVisibility(View.GONE);
            if(positionShuffle.size()>3) buttonLayout1.setVisibility(View.VISIBLE);
            if(positionShuffle.size()>5) buttonLayout2.setVisibility(View.VISIBLE);
            if(curDisplayVal==1&&displayVal==3)showTransOneThree();
            if(curDisplayVal==3&&displayVal==1)showTransThreeOne();
            if(curDisplayVal==3&&displayVal==5)showTransThreeFive();
            if(curDisplayVal==5&&displayVal==1)showTransFiveOne();
            if(curDisplayVal==5&&displayVal==8)showTransFiveEight();
            if(curDisplayVal==8&&displayVal==1)showTransEightOne();
        }
        curDisplayVal=displayVal;
        for(int i=0;i<8;i++) buttons[i].setVisibility(View.GONE);
        for(int i=0;i<positionShuffle.size();i++){
            buttons[i].setVisibility(View.VISIBLE);
        }
        lastDisplayPos=positionShuffle.get(0);
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
        Intent myInt=new Intent(getApplicationContext(),SettingsActivityLearn.class);
        myInt.putExtra("playAudBool",playAudBool);
        myInt.putExtra("showPinyinBool",showPinyinBool);
        myInt.putExtra("showEnglishBool",showEnglishBool);
        myInt.putExtra("showTextBool",showTextBool);
        myInt.putExtra("delayTextBool",delayTextBool);
        myInt.putExtra("showPinyinButtonsBool",showPinyinButtonsBool);
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
                showTextBool=data.getBooleanExtra("showTextBool",false);
                delayTextBool=data.getBooleanExtra("delayTextBool",false);
                showPinyinButtonsBool=data.getBooleanExtra("showPinyinButtonsBool",false);
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

    private void showTransOneThree(){
        Animation myAni=AnimationUtils.loadAnimation(this,R.anim.one_three_anim);
        Animation myFade=AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
        buttons[0].startAnimation(myAni);
        buttons[1].startAnimation(myFade);
        buttons[2].startAnimation(myFade);
        myWrap.incLearnProgress();
    }

    private void showTransThreeOne(){
        if(lastDisplayPos==0){
            Animation myAni=AnimationUtils.loadAnimation(this,R.anim.three_one_ling_anim);
            buttons[0].startAnimation(myAni);
        }
        if(lastDisplayPos==1){
            Animation myAni=AnimationUtils.loadAnimation(this,R.anim.three_one_anim);
            buttons[0].startAnimation(myAni);
        }
        if(lastDisplayPos==2){
            Animation myAni=AnimationUtils.loadAnimation(this,R.anim.three_one_er_anim);
            buttons[0].startAnimation(myAni);
        }
    }

    private void showTransThreeFive(){
        Animation myFade=AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
        Animation myAni=AnimationUtils.loadAnimation(this,R.anim.three_five_anim);
        if(showTextBool){
            Animation myAniTop=AnimationUtils.loadAnimation(this,R.anim.three_five_trans_top);
            mainText.startAnimation(myAniTop);
        }
        buttonLayout0.startAnimation(myAni);
        for(int i=0;i<5;i++){
            if(i!=lastDisplayPos)buttons[i].startAnimation(myFade);
        }
    }

    private void showTransFiveOne(){
        Animation myAni=AnimationUtils.loadAnimation(this,R.anim.five_one_anim);
        if(showTextBool){
            mainText.startAnimation(myAni);
        }
        if(lastDisplayPos<3){
            buttonLayout0.startAnimation(myAni);
            if(lastDisplayPos==0){
                Animation myAni2=AnimationUtils.loadAnimation(this,R.anim.three_one_ling_anim);
                buttons[0].startAnimation(myAni2);
            }
            if(lastDisplayPos==1){
                Animation myAni2=AnimationUtils.loadAnimation(this,R.anim.three_one_anim);
                buttons[0].startAnimation(myAni2);
            }
            if(lastDisplayPos==2){
                Animation myAni2=AnimationUtils.loadAnimation(this,R.anim.three_one_er_anim);
                buttons[0].startAnimation(myAni2);
            }
        }else{
            Animation myFade=AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
            buttons[0].startAnimation(myFade);
        }
    }

    private void showTransFiveEight(){
        Animation myFade=AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
        Animation myAni=AnimationUtils.loadAnimation(this,R.anim.five_eight_trans);
        if(showTextBool){
            Animation myAniTop= AnimationUtils.loadAnimation(this,R.anim.five_eight_trans_top);
            mainText.startAnimation(myAniTop);
        }
        buttonLayout0.startAnimation(myAni);
        buttonLayout1.startAnimation(myAni);
        for(int i=0;i<8;i++){
            if(i!=lastDisplayPos)buttons[i].startAnimation(myFade);
        }
    }

    private void showTransEightOne(){
        Animation myFade=AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
        buttons[0].startAnimation(myFade);
        if(showTextBool){
            Animation myAni=AnimationUtils.loadAnimation(this,R.anim.eight_one_trans);
            mainText.startAnimation(myAni);
        }
    }

    private class AsyncSave extends AsyncTask<ArrayList<MetricsLearn.MyPair>, Void, Void> {
        @Override
        protected Void doInBackground(ArrayList<MetricsLearn.MyPair>... $gotPairs) {
            myWrap.saveLevels($gotPairs[0]);
            return null;
        }
        @Override
        protected void onPostExecute(final Void unused){
            myAsyncSave=null;
        }
    }
}

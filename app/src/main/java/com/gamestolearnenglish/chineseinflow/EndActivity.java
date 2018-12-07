package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    private TextView endTitleText;
    private TextView endSummaryText0;
    private TextView endSummaryText1;
    private TextView endInfoText;
    private Intent myInt;
    private int tarBase;
    private int grpBase;
    private int actType;
    private final static String APP_PNAME = "com.gamestolearnenglish.chineseinflow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setFields();
        myInt=this.getIntent();
        tarBase=myInt.getIntExtra("contentSelected",0);
        grpBase=myInt.getIntExtra("groupSelected",0);
        actType=myInt.getIntExtra("activity",0);
        if(actType==2)showReview();
        if(actType==1)showPractice();
        if(actType==0)showLearn();
        setupRate();
    }

    private void setFields(){
        endTitleText=(TextView)findViewById(R.id.activityEndTitleText);
        endSummaryText0=(TextView)findViewById(R.id.activityEndSummaryText0);
        endSummaryText1=(TextView)findViewById(R.id.activityEndSummaryText1);
        endInfoText=(TextView)findViewById(R.id.activityEndInfoText);
        endInfoText.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
        endTitleText.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
        endSummaryText0.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
        endSummaryText1.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));

        Button aBut=(Button)findViewById(R.id.activityEndAgainButton);
        Button bBut=(Button)findViewById(R.id.activityEndBackButton);
        Button rBut=(Button)findViewById(R.id.activityEndFeedbackButton);
        rBut.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
        aBut.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
        bBut.setTypeface(Typeface.createFromAsset(getAssets(),"AmaBold.ttf"));
    }

    private void showLearn(){
        endTitleText.setText("Completed");
        endInfoText.setText(R.string.end_activity_info_learn);
        int misses=myInt.getIntExtra("misses",0);
        long myTime=myInt.getLongExtra("duration",0);
        int minutesTime=(int)Math.floor(myTime/60);
        int secondsTime=(int)myTime%60;

        endSummaryText0.setText("Misses "+misses);
        if(minutesTime>0){
            endSummaryText1.setText("Time spent "+minutesTime+"m "+secondsTime+"s");
        }else{
            endSummaryText1.setText("Time spent "+secondsTime+"s");
        }
    }

    private void showPractice(){
        int outcome=myInt.getIntExtra("outcome",0);
        int myProg=myInt.getIntExtra("myProg",0);
        long myTime=myInt.getLongExtra("duration",0);
        int minutesTime=(int)Math.floor(myTime/60);
        int secondsTime=(int)myTime%60;

        endTitleText.setText("Practice Completed");
        switch(outcome){
            case 0:
                endInfoText.setText(R.string.end_activity_info_practice_complete);
                break;
            case 1:
                endInfoText.setText(R.string.end_activity_info_practice_timeout);
                endTitleText.setText("Time Over");
                break;
            case 2:
                endInfoText.setText(R.string.end_activity_info_practice_sdtimeout);
                break;
            case 3:
                endInfoText.setText(R.string.end_activity_info_practice_sdmiss);
                break;
        }
        endSummaryText0.setText("Completed "+myProg+"/60");
        if(minutesTime>0){
            endSummaryText1.setText("Time spent "+minutesTime+"m "+secondsTime+"s");
        }else{
            endSummaryText1.setText("Time spent "+secondsTime+"s");
        }
    }

    private void showReview(){
        endTitleText.setText("Review Completed");
        endInfoText.setText(R.string.end_activity_info_review);
        int repeats=myInt.getIntExtra("repeats",0);
        long myTime=myInt.getLongExtra("duration",0);
        int minutesTime=(int)Math.floor(myTime/60);
        int secondsTime=(int)myTime%60;

        endSummaryText0.setText("Repeats "+repeats);
        if(minutesTime>0){
            endSummaryText1.setText("Time spent "+minutesTime+"m "+secondsTime+"s");
        }else{
            endSummaryText1.setText("Time spent "+secondsTime+"s");
        }
    }

    public void backClick(View v){
        overridePendingTransition(0,0);
        finish();
    }

    public void againClick(View v){
        if(actType==0){
            Intent newInt=new Intent(getApplicationContext(),LearnActivity.class);
            newInt.putExtra("contentSelected", tarBase);
            newInt.putExtra("groupSelected", grpBase);
            newInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(newInt);
        }
        if(actType==1){
            Intent newInt=new Intent(getApplicationContext(),PracticeActivity.class);
            newInt.putExtra("contentSelected", tarBase);
            newInt.putExtra("groupSelected", grpBase);
            newInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(newInt);
        }
        if(actType==2){
            Intent newInt=new Intent(getApplicationContext(),ReviewActivity.class);
            newInt.putExtra("contentSelected", tarBase);
            newInt.putExtra("groupSelected", grpBase);
            newInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(newInt);
        }
        overridePendingTransition(0,0);
        finish();
    }

    private void setupRate(){
        SharedPreferences prefs = getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("showRated",true)){
            LinearLayout rBut=(LinearLayout)findViewById(R.id.activityEndRateLayout);
            rBut.setVisibility(View.VISIBLE);
        }
    }

    public void feedbackClick(View v){
        SharedPreferences prefs =getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("showRated",false);
        editor.commit();
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
        }catch(Exception e){
        }
    }
}

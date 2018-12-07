package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivityLearn extends AppCompatActivity {

    private boolean playAudBool;
    private boolean showPinyinBool;
    private boolean showEnglishBool;
    private boolean showTextBool;
    private boolean delayTextBool;
    private boolean showPinyinButtonsBool;
    private CheckBox playAudBox;
    private CheckBox showPinyinBox;
    private CheckBox showEnglishBox;
    private CheckBox showTextBox;
    private CheckBox delayTextBox;
    private CheckBox showPinyinButtonsBox;
    private Intent myData=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_settings_learn);

        Intent gotInt=this.getIntent();

        playAudBool=gotInt.getBooleanExtra("playAudBool",false);
        showPinyinBool=gotInt.getBooleanExtra("showPinyinBool",false);
        showEnglishBool=gotInt.getBooleanExtra("showEnglishBool",false);
        showTextBool=gotInt.getBooleanExtra("showTextBool",false);
        delayTextBool=gotInt.getBooleanExtra("delayTextBool",false);
        showPinyinButtonsBool=gotInt.getBooleanExtra("showPinyinButtonsBool",false);

        playAudBox=(CheckBox)findViewById(R.id.playAudBoxCheck);
        showPinyinBox=(CheckBox)findViewById(R.id.showPinyinBoxCheck);
        showEnglishBox=(CheckBox)findViewById(R.id.showEnglishBoxCheck);
        showTextBox=(CheckBox)findViewById(R.id.showTextBoxCheck);
        delayTextBox=(CheckBox)findViewById(R.id.delayTextBoxCheck);
        showPinyinButtonsBox=(CheckBox)findViewById(R.id.showPinyinButtonBoxCheck);
        setBoxesDefault();

        //LinearLayout myLay=(LinearLayout)findViewById(R.id.backPart);
        //myLay.setOnClickListener(backListener);

        LinearLayout frontLay=(LinearLayout)findViewById(R.id.frontPart);
        frontLay.setOnClickListener(null);

        Button myBut=(Button)findViewById(R.id.settingsOkBut);
        myBut.setOnClickListener(okListener);

        TextView tViews[]=new TextView[7];
        for(int i=0;i<7;i++){
            int myId=getResources().getIdentifier("learnSettingsText"+i,"id",getPackageName());
            tViews[i]=(TextView)findViewById(myId);
            tViews[i].setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        }

        myData.putExtra("altBool",false);
        setResult(0,myData);
    }

    public void checkClick(View v){
    }

    private View.OnClickListener okListener=new View.OnClickListener(){
        public void onClick(View arg0) {
            updateBooleans();
            myData.putExtra("altBool",true);
            myData.putExtra("playAudBool",playAudBool);
            myData.putExtra("showPinyinBool",showPinyinBool);
            myData.putExtra("showEnglishBool",showEnglishBool);
            myData.putExtra("showTextBool",showTextBool);
            myData.putExtra("delayTextBool",delayTextBool);
            myData.putExtra("showPinyinButtonsBool",showPinyinButtonsBool);
            setResult(0,myData);
            finish();
        }
    };

    private View.OnClickListener backListener=new View.OnClickListener(){
        public void onClick(View arg0) {
            finish();
        }
    };

    private void setBoxesDefault(){
        if(playAudBool)playAudBox.setChecked(true);
        if(showPinyinBool)showPinyinBox.setChecked(true);
        if(showEnglishBool)showEnglishBox.setChecked(true);
        if(showTextBool)showTextBox.setChecked(true);
        if(delayTextBool)delayTextBox.setChecked(true);
        if(showPinyinButtonsBool)showPinyinButtonsBox.setChecked(true);
    }

    private void updateBooleans(){
        playAudBool=playAudBox.isChecked() ? true:false;
        showPinyinBool=showPinyinBox.isChecked() ? true:false;
        showEnglishBool=showEnglishBox.isChecked() ? true:false;
        showTextBool=showTextBox.isChecked() ? true:false;
        delayTextBool=delayTextBox.isChecked() ? true:false;
        showPinyinButtonsBool=showPinyinButtonsBox.isChecked() ? true:false;
    }
}

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

public class SettingsActivityPractice extends AppCompatActivity {

    private boolean playAudBool;
    private boolean showPinyinBool;
    private boolean showEnglishBool;
    private boolean delayTextBool;
    private CheckBox playAudBox;
    private CheckBox showPinyinBox;
    private CheckBox showEnglishBox;
    private CheckBox delayTextBox;
    private Intent myData=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_practice);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Intent gotInt=this.getIntent();

        playAudBool=gotInt.getBooleanExtra("playAudBool",false);
        showPinyinBool=gotInt.getBooleanExtra("showPinyinBool",false);
        showEnglishBool=gotInt.getBooleanExtra("showEnglishBool",false);
        delayTextBool=gotInt.getBooleanExtra("delayTextBool",false);

        playAudBox=(CheckBox)findViewById(R.id.playAudBoxCheck);
        showPinyinBox=(CheckBox)findViewById(R.id.showPinyinBoxCheck);
        showEnglishBox=(CheckBox)findViewById(R.id.showEnglishBoxCheck);
        delayTextBox=(CheckBox)findViewById(R.id.delayTextBoxCheck);
        setBoxesDefault();

        //LinearLayout myLay=(LinearLayout)findViewById(R.id.backPart);
        //myLay.setOnClickListener(backListener);

        LinearLayout frontLay=(LinearLayout)findViewById(R.id.frontPart);
        frontLay.setOnClickListener(null);

        Button myBut=(Button)findViewById(R.id.settingsOkBut);
        myBut.setOnClickListener(okListener);

        TextView tViews[]=new TextView[7];
        for(int i=0;i<5;i++){
            int myId=getResources().getIdentifier("practiceSettingsText"+i,"id",getPackageName());
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
            myData.putExtra("delayTextBool",delayTextBool);
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
        if(delayTextBool)delayTextBox.setChecked(true);
    }

    private void updateBooleans(){
        playAudBool=playAudBox.isChecked() ? true:false;
        showPinyinBool=showPinyinBox.isChecked() ? true:false;
        showEnglishBool=showEnglishBox.isChecked() ? true:false;
        delayTextBool=delayTextBox.isChecked() ? true:false;
    }
}

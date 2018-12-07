package com.gamestolearnenglish.chineseinflow;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import database.DatabaseWrapper;

public class MenuActivity extends AppCompatActivity {

    private TextView menuTitleText;
    private TextView menuStat00;
    private TextView menuStat01;
    private TextView menuStat10;
    private TextView menuStat11;
    private TextView menuStat20;
    private TextView menuStat21;
    private Button MenuReviewButton;
    private Button MenuLearnButton;
    private Button MenuPracticeButton;

    private long totalTime;
    private long viewsCount;
    private long learnProgress;
    private long practiceProgress;
    private long reviewProgress;
    private boolean textsBool;

    private Intent gotInt;
    private int tarBase;
    private int grpBase;
    private Timer myTimer;

    public MenuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initItems();
    }

    public void onResume(){
        super.onResume();
        myTimer=new Timer();
        myTimer.schedule(new TimerTask(){
            public void run(){
                goSetTexts();
            }
        }, 0,3000);
    }

    public void onPause(){
        super.onPause();
        if(myTimer!=null){
            myTimer.cancel();
            myTimer.purge();
            myTimer=null;
        }
    }

    private void goSetTexts(){
        this.runOnUiThread(setTexts);
    }

    private Runnable setTexts=new Runnable(){
        public void run(){
            getStats();
            if(textsBool==false){
                textsBool=true;
                menuStat00.setText("Total character views");
                menuStat10.setText("Total time");
                menuStat20.setText("Total progress");

                int minutesTime=(int)Math.floor(totalTime/60);
                int secondsTime=(int)totalTime%60;

                String viewsCountString=Long.toString(viewsCount);
                menuStat01.setText(viewsCountString);
                if(minutesTime>0){
                    menuStat11.setText(minutesTime+"m "+secondsTime+"s");
                }else{
                    menuStat11.setText(secondsTime+"s");
                }
                long prog=learnProgress*4+practiceProgress*6+reviewProgress*6;
                menuStat21.setText(prog+"%");
            }else{
                textsBool=false;
                menuStat00.setText("Learn progress");
                menuStat10.setText("Practice progress");
                menuStat20.setText("Review progress");
                menuStat01.setText((learnProgress*10)+"%");
                menuStat11.setText(practiceProgress+"/5");
                menuStat21.setText(reviewProgress+"/5");
            }
        }
    };

    private void getStats(){
        DatabaseWrapper myWrap=new DatabaseWrapper(this,grpBase,tarBase);
        long[] myStats=myWrap.getStats();
        myWrap.closeDatabase();
        totalTime=myStats[0];
        viewsCount=myStats[1];
        learnProgress=myStats[2];
        practiceProgress=myStats[3];
        reviewProgress=myStats[4];
    }

    private void initItems(){
        gotInt=this.getIntent();
        tarBase=gotInt.getIntExtra("contentSelected",0);
        grpBase=gotInt.getIntExtra("groupSelected",0);
        getStats();
        textsBool=false;

        menuTitleText=(TextView)findViewById(R.id.activityMenuTitleText);
        menuStat00=(TextView)findViewById(R.id.activityMenuStat00);
        menuStat01=(TextView)findViewById(R.id.activityMenuStat01);
        menuStat10=(TextView)findViewById(R.id.activityMenuStat10);
        menuStat11=(TextView)findViewById(R.id.activityMenuStat11);
        menuStat20=(TextView)findViewById(R.id.activityMenuStat20);
        menuStat21=(TextView)findViewById(R.id.activityMenuStat21);
        MenuReviewButton=(Button)findViewById(R.id.activityMenuReviewButton);
        MenuLearnButton=(Button)findViewById(R.id.activityMenuLearnButton);
        MenuPracticeButton=(Button)findViewById(R.id.activityMenuPracticeButton);

        menuTitleText.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat00.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat01.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat10.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat11.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat20.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        menuStat21.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        MenuReviewButton.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        MenuLearnButton.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
        MenuPracticeButton.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));

        String menuTitle="Characters   "+(tarBase+1+150*grpBase)+" - "+(tarBase+30+150*grpBase);
        menuTitleText.setText(menuTitle);
        registerForContextMenu(MenuLearnButton);
        registerForContextMenu(MenuPracticeButton);
        registerForContextMenu(MenuReviewButton);
    }

    public void reviewClick(View v){
        Intent myInt=new Intent(getApplicationContext(),ReviewActivity.class);
        myInt.putExtra("contentSelected", tarBase);
        myInt.putExtra("groupSelected", grpBase);
        myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myInt);
        overridePendingTransition(0,0);
        finish();
    }

    public void learnClick(View v){
        Intent myInt=new Intent(getApplicationContext(),LearnActivity.class);
        myInt.putExtra("contentSelected", tarBase);
        myInt.putExtra("groupSelected", grpBase);
        myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myInt);
        overridePendingTransition(0,0);
        finish();
    }

    public void practiceClick(View v){
        Intent myInt=new Intent(getApplicationContext(),PracticeActivity.class);
        myInt.putExtra("contentSelected", tarBase);
        myInt.putExtra("groupSelected", grpBase);
        myInt.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myInt);
        overridePendingTransition(0,0);
        finish();
    }

    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(m, v, menuInfo);
        String titleText="What to do?";
        m.setHeaderTitle(titleText);
        switch(v.getId()){
            case R.id.activityMenuLearnButton:
                m.add(0, 0, 0, "Start");
                m.add(0, 3, 0, "Reset learn data for this set");
                break;
            case R.id.activityMenuPracticeButton:
                m.add(0, 1, 0, "Start practice");
                m.add(0, 4, 0, "Reset practice data for this set");
                break;
            case R.id.activityMenuReviewButton:
                m.add(0, 2, 0, "Start review");
                m.add(0, 5, 0, "Reset review data for this set");
                break;
        }
        m.add(0, 6, 0, "Reset all data for this set");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                learnClick(null);
                return true;
            case 1:
                practiceClick(null);
                return true;
            case 2:
                reviewClick(null);
                return true;
            case 3:
                dataReseter(0);
                return true;
            case 4:
                dataReseter(1);
                return true;
            case 5:
                dataReseter(2);
                return true;
            case 6:
                dataReseter(3);
                return true;
            case 7:
                return true;
            default:
                return true;
        }
    }

    private void dataReseter(int $switchInt){
        DatabaseWrapper myWrap=new DatabaseWrapper(this,grpBase,tarBase);
        switch($switchInt){
            case 0:
                myWrap.resetData(false,true,false,false);
                break;
            case 1:
                myWrap.resetData(false,false,true,false);
                break;
            case 2:
                myWrap.resetData(false,false,false,true);
                break;
            case 3:
                myWrap.resetData(true,true,true,true);
                break;
        }
        myWrap.closeDatabase();
        if(textsBool==false){
            textsBool=true;
        }else{
            textsBool=false;
        }
        this.runOnUiThread(setTexts);
    }

    protected void onStop() {
        super.onStop();
    }

}

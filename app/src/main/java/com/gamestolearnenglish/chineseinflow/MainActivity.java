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

import database.DatabaseWrapper;

public class MainActivity extends AppCompatActivity {

    private Button[] buttons;
    private int curGrp;
    private TextView subTitleText;
    private View tmpView;
    private int menuContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initButs();
        curGrp=0;
        setGroup();
    }

    public void onResume(){
        super.onResume();
        showStats();
    }

    private void initButs(){
        buttons=new Button[6];
        for(int i=0;i<6;i++){
            int tmpInt=getResources().getIdentifier("activityMainButton"+i,"id",getPackageName());
            buttons[i]=(Button)findViewById(tmpInt);
            buttons[i].setTypeface(Typeface.createFromAsset(getAssets(),"AmaBoldItalic.ttf"));
            if(i!=5)registerForContextMenu(buttons[i]);
        }
        subTitleText=(TextView)findViewById(R.id.activtiyMainSubTitleText);
        subTitleText.setTypeface(Typeface.createFromAsset(getAssets(), "AmaBold.ttf"));
    }

    private void setGroup(){
        if(curGrp==0){subTitleText.setText("HSK 1");}
        if(curGrp==1){subTitleText.setText("HSK 2");}
        if(curGrp==2){subTitleText.setText("HSK 3a");}
        if(curGrp==3){subTitleText.setText("HSK 3b");}
        if(curGrp==4){subTitleText.setText("HSK 4a");}
        if(curGrp==5){subTitleText.setText("HSK 4b");}
        if(curGrp==6){subTitleText.setText("HSK 4c");}
        if(curGrp==7){subTitleText.setText("HSK 4d");}
        if(curGrp==8){subTitleText.setText("HSK 5a");}
        if(curGrp==9){subTitleText.setText("HSK 5b");}
        if(curGrp==10){subTitleText.setText("HSK 5c");}
        if(curGrp==11){subTitleText.setText("HSK 5d");}
        if(curGrp==12){subTitleText.setText("HSK 5e");}
        if(curGrp==13){subTitleText.setText("HSK 5f");}
        if(curGrp==14){subTitleText.setText("HSK 5g");}
        if(curGrp==15){subTitleText.setText("HSK 5h");}
        if(curGrp==16){subTitleText.setText("HSK 5i");}
        for(int i=0;i<5;i++){
            String txtStr=" "+(i*30+(1+curGrp*150))+" - "+((i+1)*30+(curGrp*150))+" ";
            buttons[i].setText(txtStr);
        }
    }

    private void showStats(){
        DatabaseWrapper myWrap=new DatabaseWrapper(this,curGrp,0);
        int[] myStats=myWrap.getAllStats();
        myWrap.closeDatabase();
        float[] cumStats=new float[5];
        for(int i=0;i<5;i++){
            buttons[i].setBackgroundResource(R.drawable.blank_button_selector);
            cumStats[i]=myStats[i*3]*4;
            cumStats[i]+=myStats[i*3+1]*6;
            cumStats[i]+=myStats[i*3+2]*6;
            if(cumStats[i]>=20) buttons[i].setBackgroundResource(R.drawable.blank_button_bronze_selector);
            if(cumStats[i]>=50) buttons[i].setBackgroundResource(R.drawable.blank_button_silver_selector);
            if(cumStats[i]==100) buttons[i].setBackgroundResource(R.drawable.blank_button_gold_selector);
        }
    }

    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(m, v, menuInfo);
        tmpView=v;
        String titleText= String.valueOf(subTitleText.getText())+" - Set ";
        switch(v.getId()){
            case R.id.activityMainButton0:
                titleText+="1";
                menuContent=0;
                break;
            case R.id.activityMainButton1:
                titleText+="2";
                menuContent=30;
                break;
            case R.id.activityMainButton2:
                titleText+="3";
                menuContent=60;
                break;
            case R.id.activityMainButton3:
                titleText+="4";
                menuContent=90;
                break;
            case R.id.activityMainButton4:
                titleText+="5";
                menuContent=120;
                break;
        }
        m.setHeaderTitle(titleText);
        m.add(0, 0, 0, "Use these characters");
        m.add(0, 1, 0, "Reset data for this set");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                buttonClick(tmpView);
                return true;
            case 1:
                sendReset();
                return true;
            default:
                return true;
        }
    }

    private void sendReset(){
        DatabaseWrapper myWrap=new DatabaseWrapper(this,curGrp,menuContent);
        myWrap.resetData(true,true,true,true);
        myWrap.closeDatabase();
        showStats();
    }

    public void buttonClick(View v){
        Intent myIntent=new Intent(getApplicationContext(),MenuActivity.class);
        switch(v.getId()){
            case R.id.activityMainButton0:
                myIntent.putExtra("contentSelected",0);
                break;
            case R.id.activityMainButton1:
                myIntent.putExtra("contentSelected",30);
                break;
            case R.id.activityMainButton2:
                myIntent.putExtra("contentSelected",60);
                break;
            case R.id.activityMainButton3:
                myIntent.putExtra("contentSelected",90);
                break;
            case R.id.activityMainButton4:
                myIntent.putExtra("contentSelected",120);
                break;
        }
        myIntent.putExtra("groupSelected",curGrp);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myIntent);
        overridePendingTransition(0,0);
    }

    public void leftTap(View v){
        curGrp--;
        if(curGrp==-1)curGrp=16;
        setGroup();
        showStats();
    }

    public void rightTap(View v){
        curGrp++;
        if(curGrp==17)curGrp=0;
        setGroup();
        showStats();
    }

    public void moreClick(View v){
        curGrp++;
        if(curGrp==17)curGrp=0;
        setGroup();
        showStats();
    }

    public void onStop(){
        super.onStop();
    }
}

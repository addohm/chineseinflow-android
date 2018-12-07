package com.gamestolearnenglish.chineseinflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;


public class CountDownFragment extends Fragment {
    private ProgressBar myBar;
    private Timer myTimer;
    private int myInt;
    private Boolean timerRunning;
    private double timerInc;

    public CountDownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count_down, container, false);
        myInt=300;
        timerInc=0.8;
        myBar=(ProgressBar)view.findViewById(R.id.activityCountDownProgressBar1);
        myBar.setProgress(myInt);
        timerRunning=false;
        return view;
    }

    public void onResume(){
        super.onResume();
        myTimer=new Timer();
        myTimer.schedule(new TimerTask(){
            public void run(){
                updateTime();
            }
        }, 0,100);
    }

    public void onPause(){
        super.onPause();
        if(myTimer!=null){
            myTimer.cancel();
            myTimer.purge();
            myTimer=null;
        }
    }

    public void altTimerInc(boolean $sdMode){
        if($sdMode){
            timerInc+=0.2;
            if(timerInc>3.4)timerInc=3.4;
        }else{
            timerInc+=0.05;
            if(timerInc>2)timerInc=2;
        }
    }

    public void startTimer(){
        timerRunning=true;
    }

    public void stopTimer(){
        timerRunning=false;
    }

    public void addTime(int $add){
        myInt+=$add;
        if(myInt>300) myInt=300;
    }

    public void resetTimer(){
        myInt=300;
    }

    private void updateTime(){
        if(timerRunning){
            myInt-=timerInc;
            if(myInt<=0){
                myInt=0;
                timerRunning=false;
                FragmentActivity myActivity=getActivity();
                ((PracticeActivity) myActivity).timeOut();
            }
            myBar.setProgress(myInt);
        }
    }
}

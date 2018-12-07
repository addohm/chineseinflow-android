package com.gamestolearnenglish.chineseinflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricsPractice {
	
	public Boolean FINISHED=false;
	
	private int[] cArr=new int[30];
	private int[] pLast=new int[30];
	private int[] sArr=new int[30];
	private int[] dArr=new int[30];
	private int[] curRun=new int[8];
	private int rVar;
	private int nLoop;
	private int LIM_1=6;
	private int LIM_2=502;
	
	private int countVar=0;
	private int unknownCount=0;
	
	private Boolean missFlag=false;
	public int timeDelay=1000;
	
	public Boolean SDMODE=false;
	public Boolean SDFLAG=false;
	private int sdCount;
	private int stCount=0;
	
	public MetricsPractice(){
		init();
	}
	
	public int[] getRoundVar(){
		countVar++;
		if(SDMODE){
			return(sdRound());
		}else{
			return(stRound());
		}
	}
	
	private int[] sdRound(){
		int ini=sArr[sdCount];
		dArr=randomizeIntArrayInit(dArr,ini);
		int[] myOut=new int[8];
		for(int i=0;i<8;i++) myOut[i]=dArr[i];
		sdCount++;
		rVar=ini;
		return(myOut);
	}
	
	private int[] stRound(){
		rVar=curRun[0];
		int[] myOut=new int[8];
		for(int i=0;i<8;i++) myOut[i]=curRun[i];
		return myOut;	
	}
	
	private void setIncVar(){
		timeDelay-=50;
		if(timeDelay<50)timeDelay=0;
	}
	
	private void init(){
		for(int i=0;i<30;i++){
			cArr[i]=0;
			dArr[i]=i;
			pLast[i]=0;
		}
		ranSeq();
		for(int i=0;i<8;i++) curRun[i]=sArr[i];
		nLoop=8;
	}
	
	public void hit(){
		hitUpdateStats();
		hitNextRound();
		sendMetrics();
		if(sdCount>=30) FINISHED=true;
	}
	
	private void hitUpdateStats(){
		if(!missFlag){
			setIncVar();
			if(cArr[rVar]==0){
				stCount++;
				cArr[rVar]=6;
			}else{
				cArr[rVar]=cArr[rVar]+1;
			}
		}
		missFlag=false;
		pLast[rVar]=countVar+1;
	}
	
	private void hitNextRound(){
		if(!SDMODE){
			if(checkGood()){
				initSdMode();
			}else{
				updateCurRun();
			}
		}
	}
	
	private void updateCurRun(){
		if(cArr[curRun[0]]==LIM_1){
			cArr[curRun[0]]=500;
			prodNew();
		}else{
			if(cArr[curRun[0]]==LIM_2){
				prodNew();
			}
		}
		int curShuf=1;
		for(int i=2;i<8;i++){
			if(getUrg(curRun[i])>getUrg(curRun[curShuf]))curShuf=i;
		}
		int tmp=curRun[0];
		curRun[0]=curRun[curShuf];
		curRun[curShuf]=tmp;
		int tmp2=curRun[curShuf];
		for(int i=curShuf;i<7;i++) curRun[i]=curRun[i+1];
		curRun[7]=tmp2;
	}
	
	private void prodNew(){
		if(nLoop<30){
			curRun[0]=sArr[nLoop];
			nLoop++;
		}else{
			if(nLoop==30){
				setLim();
				sArr=randomizeIntArray(sArr);
				nLoop++;
			}
			for(int i=0;i<30;i++){
				if(cArr[sArr[i]]<LIM_2){
					if(checkAlready(sArr[i])){
						curRun[0]=sArr[i];
						break;
					}
				}
			}
		}
	}
	
	private Boolean checkAlready(int $val){
		for(int i:curRun){
			if(i==$val) return false;
		}
		return true;
	}
	
	private void setLim(){
		LIM_2=501;
		if(unknownCount<=15) LIM_2=501;
		if(unknownCount<=5) LIM_2=500;
	}
	
	private Boolean checkGood(){
		if(countVar==10){
			if(unknownCount==0) return true;
		}
		if(countVar==15){
			if(unknownCount<2) return true;
		}
		for(int i:cArr){
			if(i<LIM_2) return false;
		}
		return true;
	}
	
	public void miss(){
		missFlag=true;
		if(cArr[rVar]>=498){
			cArr[rVar]=498;
		}else{
			if(cArr[rVar]>4){
				cArr[rVar]=4;
			}if(cArr[rVar]==0){
				cArr[rVar]=1;
				unknownCount++;
			}
		}
		sendMetrics();
	}
	
	private void initSdMode(){
		sdCount=0;
		stCount=30;
		sArr=randomizeIntArray(sArr);
		SDFLAG=true;
		SDMODE=true;
	}
	
	private void sendMetrics(){
		int cTmp=cArr[rVar];
		if(cTmp>10) cTmp-=494;
		int mOut=0;
		int cnt=0;
		for(int i=0;i<30;i++){
			if(cArr[i]>0){
				cTmp=cArr[i];
				if(cTmp>10){
					cTmp-=494;
				}
				mOut+=cTmp;
				cnt++;
			}
		}
		if(cnt==0){
			mOut=0;
		}else{
			mOut=mOut/cnt;
		}
		mOut=Math.round(mOut*10)/10;
	}
	
	private int getLastPlayVar(int $var){
		if(pLast[$var]==0){
			return(0);
		}else{
			return(1+countVar-pLast[$var]);
		}
	}
	
	private int getUrg(int $var){
		int urg=getLastPlayVar($var)-cArr[$var];
		return(urg);
	}
	
	public int getProg(){
		int stProg=stCount;
		if(stProg>30)stProg=30;
		int myProg=sdCount+stProg;
		return myProg;
	}
	
	private void ranSeq(){
		ArrayList<Integer> tArr1=new ArrayList<Integer>();
		for(int i=0;i<30;i++){
			tArr1.add(i);
		}
		Collections.shuffle(tArr1);
		for(int i=0;i<30;i++){
			sArr[i]=tArr1.get(i);
		}
	}
	
	private int[] randomizeIntArray(int[] $inArr){
		List<Integer> tList=new ArrayList<Integer>();
		for(int i:$inArr) tList.add(i);
		Collections.shuffle(tList);
		int[] outArr=new int[tList.size()];
		for(int i=0;i<tList.size();i++) outArr[i]=tList.get(i);
		return outArr;
	}
	
	private int[] randomizeIntArrayInit(int[] $inArr,int $init){
		List<Integer> tList=new ArrayList<Integer>();
		for(int i:$inArr) tList.add(i);
		Collections.shuffle(tList);
		List<Integer> tmpList=new ArrayList<Integer>();
		tmpList.add($init);
		int tmp=Collections.indexOfSubList(tList,tmpList);
		if(tmp!=0) Collections.swap(tList,0,tmp);
		int[] outArr=new int[tList.size()];
		for(int i=0;i<tList.size();i++) outArr[i]=tList.get(i);
		return outArr;
	}
}

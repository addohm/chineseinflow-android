package com.gamestolearnenglish.chineseinflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//this class determines which characters to show in LearnActivity
//learnActivity calls the newRound function here which returns an array which tells which characters to show

//each set has 30 characters
//each character is represented here by a LearnItem which holds values for how well it is known by the user and when it was last shown
//LearnItem.curLevel represents how well the user knows the character - range is 1 to 9

//stageVar determines how many options to show at the same time in the learnActivity
//stageVar 1 means that just one option is shown by itself
//stageVar 2 means show 3 options
//stageVar 3 means show 3 options, but switch to display English and pick character
//stageVar 4,5,6 mean show 5 options
//stageVar 7 - show 8 options
//stageVar 8,9 - just show one option

//curItemsList holds the currently active items
//the newRound function takes items from this array and tells the LearnActivity to display them

//example
//initially all 30 items are at curLevel of 1 and stageVar is 1
//3 items are added to curItemsList
//learnActivity calls newRound and gets 1 character from curItemsList to display
//user clicks on the character
//updateStats is called here and the curLevel of that item increments to 2
//repeat x2
//all 3 items are now curLevel 2 so stage is complete - stageVar increments to 2
//learnActivity calls newRound and gets 3 characters from curItemsList to display
//...
//when stageVar increments to 4, it is reset to 1 and three new items are added to curItemsList

public class MetricsLearn{
	private ArrayList<Integer> returnItemsList=new ArrayList<Integer>();
	private ArrayList<Integer> curItemsList=new ArrayList<Integer>();
	private ArrayList<LearnItem> learnItemsList=new ArrayList<LearnItem>();
	private ArrayList<Integer> repeatList=new ArrayList<Integer>();

	private int[] itemOrderArr=new int[30];
	
	private int learnItemsInt=0;
	private int newItemsLength=3;
	
	private int curItemInd;
	
	public long delayValue=100;
	public boolean missedFlag=false;
	private boolean saveNowFlag=false;
	private int countVar=0;
	
	private int stageLength;
	private int stageCount;
	private int stageVar;

	private boolean stageFourBool=true;
	private boolean stageSevenBool1=true;
	private boolean stageSevenBool2=true;
	
	public boolean FINISHED;
	
	public int itemDisplayNumber;
	public int itemDisplayType;
	
	public MetricsLearn(){
		itemOrderArr=ranSeq();
	}
	
	public void init(){
		for(int i=0;i<30;i++){
			//here a curLevel of 15 means that the item hasn't been added to curItemsList
			LearnItem myItem=new LearnItem(i,0,15);
			learnItemsList.add(myItem);
		}
		stageVar=1;
		addNewItems();
		initNewStage();
	}
	
	public void reinit(int $stageVar,int[] $levels){
		stageVar=$stageVar;
		for(int i=0;i<30;i++){
			LearnItem myItem=new LearnItem(i,0,$levels[i]);
			learnItemsList.add(myItem);
		}
		for(int i:$levels){
			if(i!=15)learnItemsInt++;
		}
		if(learnItemsInt>3)stageFourBool=false;
		if(learnItemsInt>17)stageSevenBool1=false;
		if(stageVar==7)stageSevenBool1=false;
		if(learnItemsInt>25)stageSevenBool2=false;
		if(learnItemsInt>22&&stageVar==7)stageSevenBool2=false;
		initNewStage();
	}
	
	public void setRestartStageCount(int $stageCount){
		stageCount=$stageCount;
		for(int i=0;i<curItemsList.size();i++){
			if(i<stageCount){
				learnItemsList.get(curItemsList.get(i)).curLevel++;
			}
		}
	}
	
	public ArrayList<Integer> newRound(){
		countVar++;
		if(stageCount<=curItemsList.size()){
			curItemInd=curItemsList.get(stageCount);
		}else{
			stageCount=0;
			curItemInd=curItemsList.get(stageCount);
		}
		returnItemsList=randomizeListArrayInit(curItemsList,curItemInd);
		return returnItemsList;
	}

	//this is called from learnActivity when a correct response is given
	public ArrayList<MyPair> updateStats(){
		learnItemsList.get(curItemInd).lastShown=countVar;
		if(!missedFlag){
			if(learnItemsList.get(curItemInd).curLevel<stageVar+1){
				learnItemsList.get(curItemInd).curLevel=stageVar+1;
			}
			//stageCount is the number of correct guesses w/o a miss
			//when all characters have been correctly guessed, this value will equal stageLength and the stage will end
			stageCount++;
		}else{
			repeatList.add(curItemInd);
			stageLength=curItemsList.size();
			randomizeFromPoint(curItemsList,stageCount);
		}
		missedFlag=false;
		
		ArrayList<MyPair> retCurItemsList=new ArrayList<MyPair>();
		//check stage is complete
		if(stageCompleteCheck()){
			stageVar++;//stage is complete so proceed to next stage
			if(stageVar>9)stageVar=9;
			if(stageVar==7||stageVar==8)resetLevels();
			for(int i:curItemsList){
				MyPair aPair=new MyPair(i,learnItemsList.get(i).curLevel);
				retCurItemsList.add(aPair);
			}
			if(stageVar==4&&stageFourBool)resetToStageOne();//don't proceed to stage 4 until stage 3 has been finished twice
			if(stageVar==7&&!stageSevenCheck())resetToStageOne();//don't proceed to stage 7 until stage 6 has been finished three times
			if(stageVar==8&&learnItemsInt<30)resetToStageOne();//don't proceed to stage 8 unless all characters have been shown
			if(stageVar>=9)FINISHED=true; else initNewStage();
		}
		return retCurItemsList;
	}
	
	private void learnAgainReset(){
		for(int i=0;i<30;i++){
			learnItemsList.get(i).curLevel=8;
		}
	}
	
	private void resetLevels(){
		for(int i:repeatList){
			if(learnItemsList.get(i).curLevel==2)learnItemsList.get(i).curLevel=1;
			if(learnItemsList.get(i).curLevel==4)learnItemsList.get(i).curLevel=2;
			if(learnItemsList.get(i).curLevel==5)learnItemsList.get(i).curLevel=4;
			if(learnItemsList.get(i).curLevel==7)learnItemsList.get(i).curLevel=5;
		}
		repeatList.clear();
	}
	
	private void resetToStageOne(){
		stageFourBool=false;
		stageVar=1;
		addNewItems();
	}
	
	private boolean stageSevenCheck(){
		if(learnItemsInt>14&&stageSevenBool1){
			stageSevenBool1=false;
			return true;
		}
		if(learnItemsInt>22&&stageSevenBool2){
			stageSevenBool2=false;
			return true;
		}
		if(learnItemsInt>27)return true;
		return false;
	}
	
	private boolean stageCompleteCheck(){
		if(stageCount>=stageLength) return true;
		return false;
	}
	
	private void addNewItems(){
		int loopVar=0;
		int addedInt=0;
		saveNowFlag=true;
		while(addedInt<newItemsLength){
			if(learnItemsList.get(itemOrderArr[loopVar]).curLevel==15){
				learnItemsList.get(itemOrderArr[loopVar]).curLevel=1;
				addedInt++;
				learnItemsInt++;
			}
			loopVar++;
			if(loopVar==30)addedInt=newItemsLength;
		}
	}
	
	private void initNewStage(){
		itemDisplayType=0;
		//itemDisplayNumber is how many options to show
		//itemDisplayType 0 means display character and pick English
		//itemDisplayType 1 means display English and pick character
		switch(stageVar){
		case 1:
			fillCurItemsList(3);
			itemDisplayNumber=1;
			break;
		case 2:
			fillCurItemsList(3);
			itemDisplayNumber=3;
			break;
		case 3:
			fillCurItemsList(3);
			itemDisplayNumber=3;
			itemDisplayType=1;
			break;
		case 4:
			fillCurItemsList(5);
			itemDisplayNumber=5;
			break;
		case 5:
			fillCurItemsList(5);
			itemDisplayNumber=5;
			break;
		case 6:
			fillCurItemsList(5);
			itemDisplayNumber=5;
			itemDisplayType=1;
			break;
		case 7:
			fillCurItemsList(15);
			itemDisplayNumber=8;
			break;
		case 8:
			fillCurItemsList(30);
			itemDisplayNumber=1;
			itemDisplayType=2;
			break;
		case 9:
			learnAgainReset();
			fillCurItemsList(30);
			itemDisplayNumber=1;
			itemDisplayType=2;
			break;
		}
		stageLength=curItemsList.size();
		stageCount=0;
		do{
            Collections.shuffle(curItemsList);
        } while (curItemsList.get(0)==curItemInd);
	}
	
	private void fillCurItemsList(int $i){
		Collections.sort(learnItemsList,myComp);
		curItemsList.clear();
		for(int i=0;i<30;i++){
			if(learnItemsList.get(i).curLevel==stageVar){
				curItemsList.add(learnItemsList.get(i).ind);
			}
		}
		int lVar=0;
		while(curItemsList.size()<$i&&lVar<30){
			if(learnItemsList.get(lVar).curLevel!=stageVar){
				curItemsList.add(learnItemsList.get(lVar).ind);
			}
			lVar++;
		}
		Collections.sort(learnItemsList,myCompInd);
	}
	
	public int getStageVar(){
		return stageVar;
	}
	
	public int getStageCount(){
		return stageCount;
	}
	
	public boolean checkSaveNow(){
		if(saveNowFlag){
			saveNowFlag=false;
			return true;
		}
		return false;
	}
	
	public ArrayList<MyPair> getSaveNowPairs(){
		ArrayList<MyPair> savePairs=new ArrayList<MyPair>();
		for(int i:curItemsList){
			MyPair aPair=new MyPair(i,1);
			savePairs.add(aPair);
		}
		return savePairs;
	}
	
	private ArrayList<Integer> randomizeListArrayInit(ArrayList<Integer> $inArr,int $init){
		ArrayList<Integer> tList=new ArrayList<Integer>();
		for(Integer i:$inArr) tList.add(i);
		Collections.shuffle(tList);
		List<Integer> tmpList=new ArrayList<Integer>();
		tmpList.add($init);
		int tmp=Collections.indexOfSubList(tList,tmpList);
		if(tmp!=0) Collections.swap(tList,0,tmp);
		ArrayList<Integer> outArr=new ArrayList<Integer>();
		for(int i=0;i<tList.size();i++) outArr.add(tList.get(i));
		return outArr;
	}
	
	private void randomizeFromPoint(ArrayList<Integer> $inArr,int $int){
		ArrayList<Integer> tList=new ArrayList<Integer>();
		for(int i=$int;i<$inArr.size();i++) {
			tList.add($inArr.get(i));
		}
		int tmp=$inArr.get($int);
		if(tList.size()>1){
			while(tmp==tList.get(0))Collections.shuffle(tList);
		}
		for(int i=$int;i<$inArr.size();i++) {
			$inArr.set(i,tList.get(i-$int));
		}
	}
	
	private Comparator<com.gamestolearnenglish.chineseinflow.MetricsLearn.LearnItem> myComp=new Comparator<LearnItem>(){
		public int compare(LearnItem i0,LearnItem i1){
			Integer in0=i0.curLevel;
			Integer in1=i1.curLevel;
			int com=in0.compareTo(in1);
			if(com==0){
				Integer in3=i0.lastShown;
				Integer in4=i1.lastShown;
				return in3.compareTo(in4);
			}
			return com;
		}
	};
	
	private Comparator<com.gamestolearnenglish.chineseinflow.MetricsLearn.LearnItem> myCompInd=new Comparator<LearnItem>(){
		public int compare(LearnItem i0,LearnItem i1){
			Integer in0=i0.ind;
			Integer in1=i1.ind;
			return in0.compareTo(in1);
		}
	};
	
	private int[] ranSeq(){
		int[] outArr=new int[30];
		ArrayList<Integer> tArr1=new ArrayList<Integer>();
		for(int i=0;i<30;i++){
			tArr1.add(i);
		}
		Collections.shuffle(tArr1);
		for(int i=0;i<30;i++){
			outArr[i]=tArr1.get(i);
		}
		return outArr;
	}
	
	public class LearnItem{
		public LearnItem(int $ind,int $lastShown,int $curLevel){
			ind=$ind;
			lastShown=$lastShown;
			curLevel=$curLevel;
		}
		int ind;
		int lastShown;
		int curLevel;
	}
	
	public class MyPair{
		private final int key;
		private final int value;
		public MyPair(int $key,int $value){
			key=$key;
			value=$value;
		}
		public int returnKey(){return key;}
		public int returnValue(){return value;}
	}
}

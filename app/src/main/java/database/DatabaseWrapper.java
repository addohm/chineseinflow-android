package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gamestolearnenglish.chineseinflow.Item;
import com.gamestolearnenglish.chineseinflow.MetricsLearn.MyPair;

import java.util.ArrayList;

public class DatabaseWrapper {
	
	private Context myContext;
	private int inden;
	private int tarBaseInd;
	private DatabaseHelper myHlpr;
	private SQLiteDatabase myDb;
	
	public DatabaseWrapper(Context context,int $db,int $dbInd){
		myContext=context;
		inden=$dbInd;
		tarBaseInd=inden/30;
		String myDatabaseName="charsBase_"+$db;
		myHlpr=new DatabaseHelper(myContext,myDatabaseName);
		myHlpr.initializeDataBase();
        myDb=myHlpr.getWritableDatabase();
	}
	
	public void saveLevels(ArrayList<MyPair> $gotPairs){
		for(MyPair aPair:$gotPairs){
			ContentValues dataToInsert=new ContentValues();
			dataToInsert.put("level",aPair.returnValue());
			int goodInt=aPair.returnKey()+1+inden;
			myDb.update("levels",dataToInsert,"_id="+goodInt,null);
		}
	}
	
	public int[] getLevels(){
		int[] myLevels=new int[30];
		Cursor c=myDb.rawQuery("SELECT * FROM 'levels'",null);
		c.moveToFirst();
    	c.moveToPosition(inden);
		for(int i=0;i<30;i++){
			myLevels[i]=c.getInt(1);
			c.moveToNext();
		}
		c.close();
		return myLevels;
	}
	
	public ArrayList<Item> getItems(){
		ArrayList<Item> myQuests=new ArrayList<Item>();
    	Cursor c=myDb.rawQuery("SELECT * FROM 'chars'",null);
    	c.moveToFirst();
    	c.moveToPosition(inden);
    	for(int i=0;i<30;i++){
    		Item nQuest=new Item(c.getString(1),c.getString(2),c.getString(3));
        	myQuests.add(nQuest);
        	c.moveToNext();
    	}
    	c.close();
		return myQuests;
	}
	
	public void updateStats(long $countVar,long $passedTime,int $stageVar,int $stageCount){
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	long gotTotalTime=c.getLong(1);
    	long gotViewsCount=c.getLong(2);
    	
    	long newTotalTime=gotTotalTime+$passedTime;
    	long newViewsCount=gotViewsCount+$countVar;
		
		ContentValues dataToInsert=new ContentValues();
		dataToInsert.put("total_time",newTotalTime);
		dataToInsert.put("views_count",newViewsCount);
		if($stageVar!=-1)dataToInsert.put("stage_var",$stageVar);
		if($stageCount!=-1)dataToInsert.put("stage_count",$stageCount);
		int goodInt=tarBaseInd+1;
		myDb.update("stats",dataToInsert,"_id="+goodInt,null);
	}
	
	public int[] getProgress(){
		int[] returnInts=new int[2];
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	returnInts[0]=c.getInt(3);
    	returnInts[1]=c.getInt(4);
		return returnInts;
	}
	
	public long[] getStats(){
		long[] returnlongs=new long[5];
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	returnlongs[0]=c.getLong(1);
    	returnlongs[1]=c.getLong(2);
    	returnlongs[2]=c.getLong(5);
    	returnlongs[3]=c.getLong(6);
    	returnlongs[4]=c.getLong(7);
		return returnlongs;
	}
	
	public int[] getAllStats(){
		int[] returnInts=new int[15];
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	returnInts[0]=c.getInt(5);
    	returnInts[1]=c.getInt(6);
    	returnInts[2]=c.getInt(7);
    	for(int i=0;i<4;i++){
        	c.moveToNext();
        	returnInts[3+i*3]=c.getInt(5);
        	returnInts[4+i*3]=c.getInt(6);
        	returnInts[5+i*3]=c.getInt(7);
    	}
    	return returnInts;
	}
	
	public void incReviewProgress(){
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	int myProgress=c.getInt(7);
    	myProgress++;
    	if(myProgress>5)myProgress=5;

		ContentValues dataToInsert=new ContentValues();
		dataToInsert.put("review_progress",myProgress);
		int goodInt=tarBaseInd+1;
		myDb.update("stats",dataToInsert,"_id="+goodInt,null);
	}
	
	public void incPracticeProgress(){
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	int myProgress=c.getInt(6);
    	myProgress++;
    	if(myProgress>5)myProgress=5;

		ContentValues dataToInsert=new ContentValues();
		dataToInsert.put("practice_progress",myProgress);
		int goodInt=tarBaseInd+1;
		myDb.update("stats",dataToInsert,"_id="+goodInt,null);
	}
	
	public void incLearnProgress(){
		Cursor c=myDb.rawQuery("SELECT * FROM 'stats'",null);
    	c.moveToFirst();
    	c.moveToPosition(tarBaseInd);
    	int myProgress=c.getInt(5);
    	myProgress++;
    	if(myProgress>10)myProgress=10;

		ContentValues dataToInsert=new ContentValues();
		dataToInsert.put("learn_progress",myProgress);
		int goodInt=tarBaseInd+1;
		myDb.update("stats",dataToInsert,"_id="+goodInt,null);
	}
	
	public void resetData(boolean $all,boolean $learn,boolean $practice,boolean $review){
		ContentValues dataToInsert2=new ContentValues();
		if($learn){
			for(int i=0;i<30;i++){
				ContentValues dataToInsert=new ContentValues();
				dataToInsert.put("level",15);
				int goodInt=i+inden+1;
				myDb.update("levels",dataToInsert,"_id="+goodInt,null);
			}
			dataToInsert2.put("learn_progress",0);
			dataToInsert2.put("stage_var",0);
			dataToInsert2.put("stage_count",0);
		}
		if($all){
			dataToInsert2.put("total_time",0);
			dataToInsert2.put("views_count",0);
		}
		if($practice)dataToInsert2.put("practice_progress",0);
		if($review)dataToInsert2.put("review_progress",0);
		int goodInt=inden/30+1;
		myDb.update("stats",dataToInsert2,"_id="+goodInt,null);
	}
	
	public void closeDatabase(){
    	myHlpr.close();
        myDb.close();
	}
}

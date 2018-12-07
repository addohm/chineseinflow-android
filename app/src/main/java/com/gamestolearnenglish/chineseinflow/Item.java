package com.gamestolearnenglish.chineseinflow;

public class Item {
	
	private String hanzi;
	private String pinzi;
	private String engzi;
	
	public Item(String $hanzi,String $pinzi,String $engzi){
		hanzi=$hanzi;
		pinzi=$pinzi;
		engzi=$engzi;
	}
	
	public String getHanzi(){
		return hanzi;
	}
	
	public String getPinzi(){
		return pinzi;
	}
	
	public String getEngzi(){
		return engzi;
	}

}

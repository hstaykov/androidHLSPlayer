package com.example.demovideoview;

import android.content.res.Resources;

public class TVChannel {
	private int channelNumber;
	private String channelName;
	private String channelURL;
	
	public TVChannel(int num, String name, String url){
		this.channelName = name;
		this.channelNumber = num;
		this.channelURL = url;
	}
	
	public TVChannel() {
		// TODO Auto-generated constructor stub
	}

	public int getNumber(){
		return this.channelNumber;
	}
	
//	public void setNumber(int num){
//		this.channelNumber = num;
//	}
//	
	
	public String getName(){
		return this.channelName;
	}
	
	public void setName(String name){
		this.channelName = name;
	}
	
	public String getURL(){
		return this.channelURL;
	}
	
	public void setURL(String url){
		this.channelURL = url;
	}
	
	
}

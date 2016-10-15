package com.example.demovideoview;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class IPTVApplication extends Application {
	private SQLiteDatabase  db = null;
	private ChannelsDB channelsDb = null;
	
	public SQLiteDatabase getDB(){
		if (db == null){
			db = new DBAdapter(getApplicationContext()).opne();
		}
		return db;
	}
	
	public ChannelsDB getChannelsDB(){
		if (channelsDb == null){
			channelsDb = new ChannelsDB(getDB());
		}
		return channelsDb;
	}
}

package com.example.demovideoview;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChannelsDB {

	private SQLiteDatabase db;
	private static final String TABLE_CHANNELS = "channels";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_URL = "url";
	
	
	public ChannelsDB(SQLiteDatabase db){
		this.db = db;
	}
	
	public void addChannel(TVChannel ch) throws Exception{
		if (ch == null){
			throw new Exception("Inserting channel error. Channel is null.");
		}
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ch.getName());
		values.put(KEY_URL, ch.getURL());
		
		db.insert(TABLE_CHANNELS, null, values);
	
		
	}
	
	
	public List<TVChannel> getChannels(){
		List<TVChannel> channels = new ArrayList<TVChannel>();
		String select = "SELECT * FROM " + TABLE_CHANNELS;
		
		
		Cursor cursor = db.rawQuery(select, null);
		
		if(cursor.moveToFirst()){
			do{
				int num = Integer.parseInt(cursor.getString(0));
				String name = cursor.getString(1);
				String url = cursor.getString(2);
				TVChannel ch = new TVChannel(num, name, url);
				channels.add(ch);
			}
			while(cursor.moveToNext());
		}
		return channels;
	}
	
	public TVChannel getChannelById(int id){
	
		String select = "SELECT * FROM " + TABLE_CHANNELS + " WHERE " + KEY_ID + " = " + id;		
	
		Cursor cursor = db.rawQuery(select, null);
		TVChannel ch = null ;
		if(cursor.moveToFirst()){
			do{
				int num = Integer.parseInt(cursor.getString(0));
				String name = cursor.getString(1);
				String url = cursor.getString(2);
				ch = new TVChannel(num, name, url);
			
			}
			while(cursor.moveToNext());
		}
		return ch;
	}
	
	
	

	public TVChannel getChannelByName(String name){
		String select = "SELECT * FROM " + TABLE_CHANNELS + " WHERE " + KEY_NAME + " = ?; ";	
	
		Cursor cursor = db.rawQuery(select, new String[] {name});
		TVChannel ch = null ;
		if(cursor.moveToFirst()){
			do{
				int num = Integer.parseInt(cursor.getString(0));
				String returnedNamename = cursor.getString(1);
				String url = cursor.getString(2);
				ch = new TVChannel(num, returnedNamename, url);
			}
			while(cursor.moveToNext());
		}
		return ch;
		
	}
	
	
	public List<String> getChannelsNames(){
		List<String> channelsNames = new ArrayList<String>();
		String select = "SELECT " + KEY_NAME + " FROM " + TABLE_CHANNELS;
		
	
		Cursor cursor = db.rawQuery(select, null);
		
		if(cursor.moveToFirst()){
			do{
				String name = (cursor.getString(0));
				channelsNames.add(name);
			}
			while(cursor.moveToNext());
		}
		return channelsNames ;
	}
	
	public void deleteChannel(TVChannel ch){
		
		db.delete(TABLE_CHANNELS, KEY_ID + " = ?", new String[] {String.valueOf(ch.getNumber())});
	
	}
	
	public void deleteAll(){
		
		String delete = "DELETE FROM " + TABLE_CHANNELS;
		db.execSQL(delete);

	}
	
	public int editChannel(TVChannel ch){
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ch.getName());
		values.put(KEY_URL, ch.getURL());
		return db.update(TABLE_CHANNELS, values, KEY_ID + " = ?", new String[] {String.valueOf(ch.getNumber())});

	}
	

}

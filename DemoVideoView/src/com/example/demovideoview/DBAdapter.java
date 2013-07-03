package com.example.demovideoview;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "TVDB";
	
	private static final String TABLE_CHANNELS = "channels";
	
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_URL = "url";
	
	
	
	
	public DBAdapter(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public DBAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try{
		String CREATE_TABLE = "CREATE TABLE " + TABLE_CHANNELS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ KEY_NAME + " TEXT, " + KEY_URL + " TEXT" + ")";
		db.execSQL(CREATE_TABLE);
		}
		catch(Exception e){
			Log.wtf("DB Create error", e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
		
		onCreate(db);
	}
	
	public void addChannel(TVChannel ch){
		try{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ch.getName());
		values.put(KEY_URL, ch.getURL());
		
		db.insert(TABLE_CHANNELS, null, values);
		db.close();
		}
		catch(Exception e){
			Log.wtf("DB Add error", e.toString());
		}	
	}
	
	public List<TVChannel> getChannels(){
		List<TVChannel> channels = new ArrayList<TVChannel>();
		String select = "SELECT * FROM " + TABLE_CHANNELS;
		
		SQLiteDatabase db = this.getWritableDatabase();
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
		SQLiteDatabase db = this.getWritableDatabase();
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
	
	
	public List<String> getChannelsNames(){
		List<String> channelsNames = new ArrayList<String>();
		String select = "SELECT " + KEY_NAME + " FROM " + TABLE_CHANNELS;
		
		SQLiteDatabase db = this.getWritableDatabase();
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
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CHANNELS, KEY_ID + " = ?", new String[] {String.valueOf(ch.getNumber())});
		db.close();
	}
	
	public void deleteAll(){
		SQLiteDatabase db = this.getWritableDatabase();
//		db.delete(TABLE_CHANNELS, null, null);
		String delete = "DELETE FROM " + TABLE_CHANNELS;
		db.execSQL(delete);
		db.close();
	}
	
	public int editChannel(TVChannel ch){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, ch.getName());
		values.put(KEY_URL, ch.getURL());
		return db.update(TABLE_CHANNELS, values, KEY_ID + " = ?", new String[] {String.valueOf(ch.getNumber())});
		
//		String update = "UPDATE " + TABLE_CHANNELS + " SET " + KEY_NAME + " = " + ch.getName() + " , " + KEY_URL + " = "
//		+ ch.getURL() + " WHERE id= " + ch.getNumber();
//		db.execSQL(update);
//		db.close();
	}
	

	
}

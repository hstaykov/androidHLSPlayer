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
import com.example.IptvPlayer.R;

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
	
	}
	public DBAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNELS);
		
		onCreate(db);
	}
	
	public SQLiteDatabase opne(){
		return getWritableDatabase();
	}

	
	
	
	
}

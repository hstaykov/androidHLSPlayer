package com.example.demovideoview;

import java.util.List;
import java.util.prefs.Preferences;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PreferencesActivity extends Activity {
public static final String USER_CHANNEL = "USER_CHANNEL";
public static final String USER_PREFS = "USER_PREFS";
SharedPreferences prefs;
	Spinner channelSpinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.ac_preferences);
        
      
        DBAdapter db = new DBAdapter(this);
//	    db.addChannel(currentChannel);
	    List<String> chanls = db.getChannelsNames();
        
	    for(String s : chanls){
	    	Log.d("Prefs Check", s);
	    }
        
        channelSpinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> fAdapter;
        fAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, chanls);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        channelSpinner.setAdapter(fAdapter);
        
        prefs = getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        int channel = prefs.getInt(USER_CHANNEL, 1);
        channelSpinner.setSelection(channel);
        
        Log.d("A tag", "We are before button");
        Button okButton = (Button)findViewById(R.id.buttonOK);
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				savePreferences();
				PreferencesActivity.this.setResult(RESULT_OK);
				finish();
				
			}
		});
        
        
        Button addButton = (Button)findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addChannel();
			}
		});
        
        }
        catch(Exception e){
        	Log.d("A tag", "Something went wrong...");
        }
    }
    
    private void addChToDB(TVChannel ch){
    	DBAdapter db = new DBAdapter(this);
    	db.addChannel(ch);
    	
    }
    
    private void addChannel(){
    	// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_channel);
		dialog.setTitle("Add channel");
		
		final EditText mName = (EditText)dialog.findViewById(R.id.tbName);
		final EditText mUrl = (EditText)dialog.findViewById(R.id.tbUrl);
		
		Button mButtonSubmit = (Button)dialog.findViewById(R.id.buttonSubmit);
		mButtonSubmit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String name = mName.getText().toString();
					String url = mUrl.getText().toString();
					TVChannel tvc = new TVChannel(0, name, url);	
					addChToDB(tvc);
					dialog.dismiss();
				}
			});
		dialog.show();
    }
    
    private void savePreferences(){
    	int user_channel_number = channelSpinner.getSelectedItemPosition();
    	Editor edit = prefs.edit();
    	edit.putInt(USER_CHANNEL, user_channel_number);
    	edit.commit();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ac_preferences, menu);
        return true;
    }
}

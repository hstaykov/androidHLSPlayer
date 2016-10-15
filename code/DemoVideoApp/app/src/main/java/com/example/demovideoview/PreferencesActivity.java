package com.example.demovideoview;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.IptvPlayer.R;

public class PreferencesActivity extends Activity {
public static final String USER_CHANNEL = "USER_CHANNEL";
public static final String USER_PREFS = "USER_PREFS";
private static final String tag = "PrefsError";
SharedPreferences prefs;
Spinner channelSpinner;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.ac_preferences);
        
      
      
        channelSpinner = (Spinner)findViewById(R.id.spinner1);
        
        ArrayAdapter<String> fAdapter;
       
        
        prefs = getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        int channel = prefs.getInt(USER_CHANNEL, 1);
        //channelSpinner.setSelection(channel - 1);
        
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
        
        Button deleteButton = (Button)findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				TVChannel selectedChannel = getChannelByName(channelSpinner.getSelectedItem().toString());
				((IPTVApplication)getApplication()).getChannelsDB().deleteChannel(selectedChannel);
				
				updateSpinner();
			}
		});
        
        Button editButton = (Button)findViewById(R.id.buttonEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editChannel();
			}
		});
        
        }
        catch(Exception e){
        	Log.d("A tag", "Something went wrong..." + e.getMessage());
        }
    }



	private void updateSpinner() {
		ArrayAdapter<String> fAdapter;
	    List<String> chanls = ((IPTVApplication)getApplication()).getChannelsDB().getChannelsNames();	    		
        fAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, chanls);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        channelSpinner.setAdapter(fAdapter);
	}
   
    
    
    private void addChToDB(TVChannel ch){
    	try {
			((IPTVApplication)getApplication()).getChannelsDB().addChannel(ch);
		} catch (Exception e) {
			Log.wtf(tag, "Problem with adding channel to db" + e.getMessage());
			
		}
    }
    
    private void editChToDB(TVChannel ch){

    	((IPTVApplication)getApplication()).getChannelsDB().editChannel(ch);
    	
    }
    
    private TVChannel getChannelById(int id){

    	TVChannel tv = ((IPTVApplication)getApplication()).getChannelsDB().getChannelById(id);
    	return tv;
    }
    
    private TVChannel getChannelByName(String name){
    	TVChannel tv = ((IPTVApplication)getApplication()).getChannelsDB().getChannelByName(name);
    	return tv;
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
					updateSpinner();
					dialog.dismiss();
				}
			});
		dialog.show();
    }
    
    private void editChannel(){
    	// custom dialog
   
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_channel);
		dialog.setTitle("Add channel");
		
		final EditText mName = (EditText)dialog.findViewById(R.id.tbName);
		final EditText mUrl = (EditText)dialog.findViewById(R.id.tbUrl);
		final TVChannel selectedChannel = getChannelByName(channelSpinner.getSelectedItem().toString());
		mName.setText(selectedChannel.getName());
		mUrl.setText(selectedChannel.getURL());
		
		Button mButtonSubmit = (Button)dialog.findViewById(R.id.buttonSubmit);
		mButtonSubmit.setText("Edit Channel");
		mButtonSubmit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String name = mName.getText().toString();
					String url = mUrl.getText().toString();
					TVChannel tvc = new TVChannel(selectedChannel.getNumber(), name, url);	
					editChToDB(tvc);
					updateSpinner();
					dialog.dismiss();
				}
			});
		dialog.show();
    }
    
    private void savePreferences(){
    	Object userChannelName = channelSpinner.getSelectedItem();
    	int user_channel_number  = getChannelByName(userChannelName.toString()).getNumber();
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

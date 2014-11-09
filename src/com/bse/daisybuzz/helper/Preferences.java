package com.bse.daisybuzz.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	String appId;	
	private Activity gameActivity;	
	String PREFERENCES_ID = "BRAND_SURVEY";
	
	public SharedPreferences sharedPreferences;
	
	
	public Preferences(Activity gameActivity){
		this.gameActivity = gameActivity;		
	    Context mContext = gameActivity.getApplicationContext();
	    sharedPreferences = mContext.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);	    
	}
		
	public void saveValue(String key, String value){		
	    Context mContext = gameActivity.getApplicationContext();
	    sharedPreferences = mContext.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putString(key, value);
	    editor.commit();
	}
	
	public void saveValue(String key, int value){		
	    Context mContext = gameActivity.getApplicationContext();
	    sharedPreferences = mContext.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putInt(key, value);
	    editor.commit();
	}
	
	public String getStringValue( String key) {		
	    Context mContext = gameActivity.getApplicationContext();
	    sharedPreferences = mContext.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
	    return sharedPreferences.getString(key, "NONE");
	}
	
	public int getIntValue(String key) {		
	    Context mContext = gameActivity.getApplicationContext();
	    sharedPreferences = mContext.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
	    return sharedPreferences.getInt(key, 0);
	}
}

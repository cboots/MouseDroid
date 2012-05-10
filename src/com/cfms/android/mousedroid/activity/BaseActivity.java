package com.cfms.android.mousedroid.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cfms.android.mousedroid.utils.Configuration;
import com.cfms.android.mousedroid.utils.DebugLog;
import com.flurry.android.FlurryAgent;

public abstract class BaseActivity extends Activity {
	
	public abstract String getTag();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.D(getTag(), "+++OnCreate+++");
		//TODO implement themes
//		String theme = Preferences.GetPreferences(this)
//		.getString(Preferences.PREF_THEME, 
//		           Preferences.DEF_THEME);
//		if(theme.equals(Preferences.THEME_LIGHT))
//		{
//			//this.setTheme(android.R.style.Theme_Light);
//		}else
//		{
//			//this.setTheme(android.R.style.Theme);
//		}
//		
//		
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		DebugLog.D(getTag(), "+  OnResume  +");
		super.onResume();
	}

	@Override
	public void onPause() {
		DebugLog.D(getTag(), "-  OnPause  -");
		super.onPause();
	}

	@Override
	public void onStart() {
		DebugLog.D(getTag(), "++ OnStart ++");
		super.onStart();
		
		if(PreferencesActivity
				.getBoolean(this, PreferencesActivity.PREF_FLURRY_ENABLED))
		{
			
			if(!PreferencesActivity
					.getBoolean(this, PreferencesActivity.PREF_FLURRY_LOCATION_ENABLED))
			{
				FlurryAgent.setReportLocation(false);
			}
			FlurryAgent.onStartSession(this, Configuration.getFlurryApiKey(this));
			FlurryAgent.onEvent(getTag());
		}
		

	}

	@Override
	public void onStop() {
		DebugLog.D(getTag(), "-- OnStop --");
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onDestroy() {
		DebugLog.D(getTag(), "---OnDestroy---");
		super.onDestroy();
	}

}

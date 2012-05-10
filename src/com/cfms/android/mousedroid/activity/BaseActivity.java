package com.cfms.android.mousedroid.activity;

import android.app.Activity;
import android.os.Bundle;

import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.utils.Configuration;
import com.cfms.android.mousedroid.utils.DebugLog;
import com.flurry.android.FlurryAgent;

public abstract class BaseActivity extends Activity {
	
	public abstract String getTag();
	
	private int setTheme = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.D(getTag(), "+++OnCreate+++");
		setTheme = PreferencesActivity.getInteger(this, PreferencesActivity.PREF_THEME);
		switch(setTheme)
		{
		case PreferencesActivity.THEME_DEFAULT:
			this.setTheme(R.style.Theme_Dark);
			break;
		case PreferencesActivity.THEME_LIGHT:
			this.setTheme(R.style.Theme_Light);
			break;
		case PreferencesActivity.THEME_COLORFUL:
			this.setTheme(R.style.Theme_Colorful);
			break;
		}
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		DebugLog.D(getTag(), "+  OnResume  +");
		super.onResume();
		if(setTheme != PreferencesActivity.getInteger(this, PreferencesActivity.PREF_THEME))
		{
			//Theme has changed, restart activity.
			startActivity(getIntent());
			finish();
		}
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

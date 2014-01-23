package com.cfms.mousedroid.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.utils.Configuration;
import com.cfms.mousedroid.utils.DebugLog;
import com.flurry.android.FlurryAgent;

public abstract class BaseActivity extends FragmentActivity {
	
	public abstract String getTag();
	
	private int setTheme = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.D(getTag(), "+++OnCreate+++");

		setTheme = PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 0);
		switch(setTheme)
		{
		case 0:
			this.setTheme(R.style.Theme_Dark);
			break;
		case 1:
			this.setTheme(R.style.Theme_Light);
			break;
		case 2:
			this.setTheme(R.style.Theme_Colorful);
			break;
		}
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		DebugLog.D(getTag(), "+  OnResume  +");
		super.onResume();
		if(setTheme != PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 0))
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
		
		boolean flurry_en = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("flurry_enabled", false);
		
		if(flurry_en)
		{
			
			if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("flurry_loc_enabled", false))
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

package com.cfms.android.mousedroid.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity {

	public static final String PREF_FLURRY_ENABLED = "flurry_enabled";
	public static final String PREF_FLURRY_LOCATION_ENABLED = "flurry_location_enabled";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}


	public static SharedPreferences GetPreferences(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c);
	}

	public static Editor GetEditor(Context c) {

		return PreferencesActivity.GetPreferences(c).edit();
	}

	public static boolean getBoolean(Context c, String key)
	{
		Resources res = c.getResources();
		int id = res.getIdentifier(key, "bool", c.getPackageName());
		return PreferencesActivity.GetPreferences(c).getBoolean(key, res.getBoolean(id));
	}
	
	public static String getString(Context c, String key)
	{
		Resources res = c.getResources();
		int id = res.getIdentifier(key, "string", c.getPackageName());
		return PreferencesActivity.GetPreferences(c).getString(key, res.getString(id));
	}
	
	public static int getInteger(Context c, String key)
	{
		Resources res = c.getResources();
		int id = res.getIdentifier(key, "integer", c.getPackageName());
		return PreferencesActivity.GetPreferences(c).getInt(key, res.getInteger(id));
	}
	
    private final String TAG = "PreferencesActivity";
	public String getTag() {
		return TAG;
	}
	
}

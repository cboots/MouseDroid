package com.cfms.mousedroid;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import com.cfms.mousedroid.bluetooth.BluetoothService;
import com.cfms.mousedroid.utils.DebugLog;

public class MouseDroidApplication extends Application {
	public static boolean customApplicationInitialized = false;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DebugLog.D("MouseDroidApplication", "onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		DebugLog.D("MouseDroidApplication", "onCreate()");
		super.onCreate();
        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
		customApplicationInitialized = true;
	}

	@Override
	public void onLowMemory() {
		DebugLog.D("MouseDroidApplication", "onLowMemory()");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		DebugLog.D("MouseDroidApplication", "onTerminate()");
        Intent intent = new Intent(this, BluetoothService.class);
        stopService(intent);
		super.onTerminate();
	}
	
	
}

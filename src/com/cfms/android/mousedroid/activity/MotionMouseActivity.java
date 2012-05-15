package com.cfms.android.mousedroid.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;

public class MotionMouseActivity extends BluetoothActivity implements
		SensorEventListener {

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private WakeLock mWakeLock;
	private Sensor mAccelerometer;

	private long mLastT;
	private float mLastDeltaT;
	
	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;
	private long mSensorTimeStamp;
	private long mCpuTimeStamp;
	private float mHorizontalBound;
	private float mVerticalBound;

	public MotionMouseActivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass()
                .getName());

	}
	
	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		/*
		 * record the accelerometer data, the event's timestamp as well as the
		 * current time. The latter is needed so we can calculate the "present"
		 * time during rendering. In this application, we need to take into
		 * account how the screen is rotated with respect to the sensors (which
		 * always return data in a coordinate space aligned to with the screen
		 * in its native orientation).
		 */

		switch (mDisplay.getRotation()) {
		case Surface.ROTATION_0:
			mSensorX = event.values[0];
			mSensorY = event.values[1];
			break;
		case Surface.ROTATION_90:
			mSensorX = -event.values[1];
			mSensorY = event.values[0];
			break;
		case Surface.ROTATION_180:
			mSensorX = -event.values[0];
			mSensorY = -event.values[1];
			break;
		case Surface.ROTATION_270:
			mSensorX = event.values[1];
			mSensorY = -event.values[0];
			break;
		}

		mSensorZ = event.values[2];
		
		mSensorTimeStamp = event.timestamp;
		mCpuTimeStamp = System.nanoTime();

	}

	private String TAG = "MotionMouseActivity";
	@Override
	public String getTag(){
		return TAG;
	}
}

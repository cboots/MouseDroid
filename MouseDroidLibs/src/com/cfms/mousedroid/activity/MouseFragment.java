package com.cfms.mousedroid.activity;

import Jama.Matrix;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.bluetooth.BluetoothActivity;
import com.cfms.mousedroid.sensor.MouseKalmanFilter;

public class MouseFragment extends Fragment implements SensorEventListener {
	private static final double GRAVITY = 9.81;

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private WakeLock mWakeLock;
	private Sensor mAccelerometer;

	BluetoothActivity mBTActivity;
	private TextView mTextViewXVel;
	private TextView mTextViewYVel;
	private TextView mTextViewZVel;
	
	private double[] LinearAccel = {0,0,GRAVITY};
	private long mSensorTimeStamp = 0;
	private long mCpuTimeStamp = 0;
	
	private double[] LinearAccelBias = {0,0,0};
	MouseKalmanFilter mKalmanFilter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mBTActivity = (BluetoothActivity) getActivity();
		
		mKalmanFilter = new MouseKalmanFilter();
		
		mSensorManager = (SensorManager) mBTActivity.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) mBTActivity.getSystemService(Context.POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) mBTActivity.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass()
                .getName());

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.mouse_fragment, container, false);
		Button calibrateButton = (Button)v.findViewById(R.id.calibrate_button);
		calibrateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				calibrate();
			}
		});
		
		mTextViewXVel = (TextView) v.findViewById(R.id.xveltext);
		mTextViewYVel = (TextView) v.findViewById(R.id.yveltext);
		mTextViewZVel = (TextView) v.findViewById(R.id.zveltext);
		return v;
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
	

	@Override
	public void onSensorChanged(SensorEvent event) {
		//TODO
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
			LinearAccel[0] = event.values[0];
			LinearAccel[1] = event.values[1];
			break;
		case Surface.ROTATION_90:
			LinearAccel[0] = -event.values[1];
			LinearAccel[1] = event.values[0];
			break;
		case Surface.ROTATION_180:
			LinearAccel[0] = -event.values[0];
			LinearAccel[1] = -event.values[1];
			break;
		case Surface.ROTATION_270:
			LinearAccel[0] = event.values[1];
			LinearAccel[1] = -event.values[0];
			break;
		}

		LinearAccel[2]= event.values[2]-GRAVITY;
		
		//Subtract biases
		LinearAccel[0] -= LinearAccelBias[0];
		LinearAccel[1] -= LinearAccelBias[1];
		LinearAccel[2] -= LinearAccelBias[2];
		
		long timeNow = System.nanoTime();
		
		double dt = ((double) (timeNow - mCpuTimeStamp))/1000000000;
		mSensorTimeStamp = event.timestamp;
		mCpuTimeStamp = timeNow;
		
		mKalmanFilter.update(dt, LinearAccel);
		
		Matrix estimate = mKalmanFilter.getState();
		double xVelEst = estimate.get(3, 0);
		double yVelEst = estimate.get(4, 0);
		double zVelEst = estimate.get(5, 0);
		
		//Update TextViews
		mTextViewXVel.setText("X Velocity: "+xVelEst);
		mTextViewYVel.setText("Y Velocity: "+yVelEst);
		mTextViewZVel.setText("Z Velocity: "+zVelEst);
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

	protected void calibrate() {
		// TODO Auto-generated method stub
		
	}
	
}

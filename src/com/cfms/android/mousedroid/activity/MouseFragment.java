package com.cfms.android.mousedroid.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.bluetooth.BluetoothActivity;

public class MouseFragment extends Fragment implements SensorEventListener {

//	private SensorManager mSensorManager;
//	private PowerManager mPowerManager;
//	private WindowManager mWindowManager;
//	private Display mDisplay;
//	private WakeLock mWakeLock;
//	private Sensor mAccelerometer;

	BluetoothActivity mBTActivity;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mBTActivity = (BluetoothActivity) getActivity();
		
//		mSensorManager = (SensorManager) mBTActivity.getSystemService(Context.SENSOR_SERVICE);
//		mAccelerometer = mSensorManager
//				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		
//        // Get an instance of the PowerManager
//        mPowerManager = (PowerManager) mBTActivity.getSystemService(Context.POWER_SERVICE);
//
//        // Get an instance of the WindowManager
//        mWindowManager = (WindowManager) mBTActivity.getSystemService(Context.WINDOW_SERVICE);
//        mDisplay = mWindowManager.getDefaultDisplay();
//
//        // Create a bright wake lock
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass()
//                .getName());

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.mouse_fragment, container, false);
		Button button1 = (Button)v.findViewById(R.id.button1);
		Button button2 = (Button)v.findViewById(R.id.button2);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				testButton1(v);
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				testButton2(v);
			}
		});
		
		return v;
	}

	
	@Override
	public void onResume() {
		super.onResume();
//		mSensorManager.registerListener(this, mAccelerometer,
//				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause() {
		super.onPause();
//		mSensorManager.unregisterListener(this);
	}
	

	@Override
	public void onSensorChanged(SensorEvent event) {
		//TODO
//		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
//			return;
//		/*
//		 * record the accelerometer data, the event's timestamp as well as the
//		 * current time. The latter is needed so we can calculate the "present"
//		 * time during rendering. In this application, we need to take into
//		 * account how the screen is rotated with respect to the sensors (which
//		 * always return data in a coordinate space aligned to with the screen
//		 * in its native orientation).
//		 */
//
//		switch (mDisplay.getRotation()) {
//		case Surface.ROTATION_0:
//			mSensorX = event.values[0];
//			mSensorY = event.values[1];
//			break;
//		case Surface.ROTATION_90:
//			mSensorX = -event.values[1];
//			mSensorY = event.values[0];
//			break;
//		case Surface.ROTATION_180:
//			mSensorX = -event.values[0];
//			mSensorY = -event.values[1];
//			break;
//		case Surface.ROTATION_270:
//			mSensorX = event.values[1];
//			mSensorY = -event.values[0];
//			break;
//		}
//
//		mSensorZ = event.values[2];
//		
//		mSensorTimeStamp = event.timestamp;
//		mCpuTimeStamp = System.nanoTime();
//
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

	public void testButton1(View target){
		if(mBTActivity.isBoundToBTService())
		{
			byte[] packet = BTProtocol.getMouseMovePacket((short)10, (short)-10);
			mBTActivity.getBTService().write(packet, packet.length);
		}
	}

	public void testButton2(View target){
		if(mBTActivity.isBoundToBTService())
		{
			byte[] packet = BTProtocol.getMouseButtonEventPacket(MouseButton.BUTTON1, MouseButtonEvent.PRESS);
			mBTActivity.getBTService().write(packet, packet.length);
			packet = BTProtocol.getMouseButtonEventPacket(MouseButton.BUTTON1, MouseButtonEvent.RELEASE);
			mBTActivity.getBTService().write(packet, packet.length);
		}
	}
}

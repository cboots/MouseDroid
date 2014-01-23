package com.cfms.mousedroid.bluetooth;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.activity.MultiTouchActivity;
import com.cfms.mousedroid.bluetooth.BluetoothService.BluetoothBinder;
import com.cfms.mousedroid.bluetooth.BluetoothService.BluetoothEventListener;
import com.cfms.mousedroid.utils.DebugLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothActivity.
 */
public class BluetoothActivity extends MultiTouchActivity implements BluetoothEventListener{

	
	
	/** The Constant TAG. */
	private static final String TAG = "BluetoothActivity";

	/** The Bt service. */
	protected BluetoothService mBtService;
	
	/** The Bound. */
	protected boolean mBound = false;
	
	/** The Bluetooth adapter. */
	protected BluetoothAdapter mBluetoothAdapter;

	/** The Constant REQUEST_ENABLE_BT. */
	protected static final int REQUEST_ENABLE_BT = 101;

	/** Defines callbacks for service binding, passed to bindService(). */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			BluetoothBinder binder = (BluetoothBinder) service;
			mBtService = binder.getService();
			mBtService.setEventListener(BluetoothActivity.this);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBtService.setEventListener(null);
			mBound = false;
			mBtService = null;
		}
	};

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.D(TAG, "+++onCreate+++");
		super.onCreate(savedInstanceState);
		
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			// TODO handle error more gracefully
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onResume()
	 */
	@Override
	public void onResume() {
		DebugLog.D(TAG, "+  onResume  +");
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onPause()
	 */
	@Override
	public void onPause() {
		DebugLog.D(TAG, "-  onPause  -");
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onStart()
	 */
	@Override
	public void onStart() {
		DebugLog.D(TAG, "++ onStart ++");
		super.onStart();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mBtService == null)
				setupBT();
		}

	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onStop()
	 */
	@Override
	public void onStop() {
		DebugLog.D(TAG, "-- onStop --");

		super.onStop();
	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		DebugLog.D(TAG, "---onDestroy---");
		// Unbind from the service
		closeBT();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.D(getTag(), "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a BT
				setupBT();
			} else {
				// User did not enable Bluetooth or an error occurred
				DebugLog.D(getTag(), "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_quit,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Checks if is bound to bt service.
	 *
	 * @return true, if is bound to bt service
	 */
	public boolean isBoundToBTService() {
		return mBound;
	}

	/**
	 * Gets the bT service.
	 *
	 * @return the bT service
	 */
	public BluetoothService getBTService() {
		if (isBoundToBTService()) {
			return mBtService;
		}
		return null;
	}

	/**
	 * Ensure discoverable.
	 */
	public void ensureDiscoverable() {
		DebugLog.D(getTag(), "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Setup bt.
	 */
	protected void setupBT() {
		DebugLog.D(getTag(), "setupBT");

		if (!isBTServiceStarted()) {
			// Need to start service
			Intent intent = new Intent(this, BluetoothService.class);
			startService(intent);
			// Bind to BluetoothService
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		} else {
			// Bind to BluetoothService
			Intent intent = new Intent(this, BluetoothService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * Checks if is bT service started.
	 *
	 * @return true, if is bT service started
	 */
	public boolean isBTServiceStarted() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(1000)) {
			if ("com.cfms.mousedroid.bluetooth.BluetoothService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Close bt.
	 */
	protected void closeBT() {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
			mBtService = null;
		}
	}

	
	
	/**
	 * Send bt message.
	 *
	 * @param message the message
	 * @param length the length
	 * @return true, if successful
	 */
	public boolean sendBTMessage(byte[] message, int length) {
		if (isBoundToBTService()) {
			mBtService.write(message, length);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cfms.mousedroid.activity.BaseActivity#getTag()
	 */
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public void onBTMessageWritten(byte[] message, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBTStateChanged(int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBTError(int errorCode) {
		// TODO Auto-generated method stub
		
	}
}

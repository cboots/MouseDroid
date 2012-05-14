package com.cfms.android.mousedroid.bluetooth;

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

import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.BaseActivity;
import com.cfms.android.mousedroid.bluetooth.BluetoothService.BluetoothBinder;
import com.cfms.android.mousedroid.utils.DebugLog;


public class BluetoothActivity extends BaseActivity {

	private static final String TAG = "BluetoothActivity";
	
	protected BluetoothService mBtService;
    protected boolean mBound = false;
	protected BluetoothAdapter mBluetoothAdapter;
	

    protected static final int REQUEST_ENABLE_BT = 101;
    
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		DebugLog.D(TAG, "+++onCreate+++");
		super.onCreate(savedInstanceState);
		
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
        	//TODO handle error more gracefully
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		
	}

	@Override
	public void onResume() {
		DebugLog.D(TAG, "+  onResume  +");
		super.onResume();
	}

	@Override
	public void onPause() {
		DebugLog.D(TAG, "-  onPause  -");
		super.onPause();
	}

	@Override
	public void onStart() {
		DebugLog.D(TAG, "++ onStart ++");
		super.onStart();
		 if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mBtService == null) setupBT();
        }

	}

	@Override
	public void onStop() {
		DebugLog.D(TAG, "-- onStop --");
		// Unbind from the service
        closeBT();
        
		super.onStop();   
	}

	@Override
	public void onDestroy() {
		DebugLog.D(TAG, "---onDestroy---");
		super.onDestroy();
	}
	
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
	                Toast.makeText(this, R.string.bt_not_enabled_quit, Toast.LENGTH_SHORT).show();
	                finish();
	            }
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	
	
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	BluetoothBinder binder = (BluetoothBinder) service;
        	mBtService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mBtService = null;
        }
    };
	
    public boolean isBoundToBTService()
    {
    	return mBound;
    }
    
    public BluetoothService getBTService()
    {
    	if(isBoundToBTService())
    	{
    		return mBtService;
    	}
    	return null;
    }
    
    public void ensureDiscoverable() {
        DebugLog.D(getTag(), "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    protected void setupBT(){
    	DebugLog.D(getTag(), "setupBT");
    	
    	if(!isBTServiceStarted()){
    		//Need to start service
            Intent intent = new Intent(this, BluetoothService.class);
            startService(intent);
	        // Bind to BluetoothService
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    	}else{    	
	        // Bind to BluetoothService
	        Intent intent = new Intent(this, BluetoothService.class);
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    	}
    }
    

	public boolean isBTServiceStarted() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(1000)) {
	        if ("com.cfms.android.mousedroid.bluetooth.BluetoothService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
		return false;
	}

	protected void closeBT() {
		if (mBound) {
            unbindService(mConnection);
            mBound = false;
            mBtService = null;
        }
	}
    
    public void onBTMessage(byte[] message, int length){
    	
    }

	@Override
	public String getTag() {
		return TAG;
	}
}

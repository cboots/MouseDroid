package com.cfms.android.mousedroid.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.cfms.android.mousedroid.activity.BaseActivity;
import com.cfms.android.mousedroid.bluetooth.BluetoothService.BluetoothBinder;


public abstract class BluetoothActivity extends BaseActivity {

	protected BluetoothService mBtService;
    protected boolean mBound = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		// Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            mBtService = null;
        }
        
		super.onStop();   
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
}

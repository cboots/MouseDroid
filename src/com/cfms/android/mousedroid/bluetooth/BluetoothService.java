package com.cfms.android.mousedroid.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;

import com.cfms.android.mousedroid.utils.DebugLog;

public class BluetoothService extends Service {

	 // Binder given to clients
    private final IBinder mBinder = new BluetoothBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class BluetoothBinder extends Binder {
    	BluetoothService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onCreate() {
		  DebugLog.I("BluetoothService", "Service onCreate()");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.I("BluetoothService", "Service onStartCommand()");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        DebugLog.I("BluetoothService", "Service onDestroy()");
    }

}

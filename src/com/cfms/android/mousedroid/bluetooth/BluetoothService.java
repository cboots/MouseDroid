package com.cfms.android.mousedroid.bluetooth;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.MouseDroidActivity;
import com.cfms.android.mousedroid.utils.DebugLog;

public class BluetoothService extends Service {

	 // Binder given to clients
    private final IBinder mBinder = new BluetoothBinder();
    
    private static final int ONGOING_NOTIFICATION = 101;

    
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
	    super.onCreate();

	    //TODO compatibility fix
	    Notification notification = new Notification(R.drawable.ic_launcher, "Mouse Droid Connection",
		        System.currentTimeMillis());
		//TODO make this a valid target activity
		Intent notificationIntent = new Intent(this, MouseDroidActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "Bluetooth",
		        "Bluetooth service running", pendingIntent);
		startForeground(ONGOING_NOTIFICATION, notification);
	    
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.I("BluetoothService", "Service onStartCommand()");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        DebugLog.I("BluetoothService", "Service onDestroy()");
        super.onDestroy();
    }

}

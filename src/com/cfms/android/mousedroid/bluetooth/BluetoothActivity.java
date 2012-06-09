package com.cfms.android.mousedroid.bluetooth;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.PacketID;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.MultiTouchActivity;
import com.cfms.android.mousedroid.bluetooth.BluetoothService.BluetoothBinder;
import com.cfms.android.mousedroid.utils.DebugLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothActivity.
 */
public class BluetoothActivity extends MultiTouchActivity {

	
	/** The Version code. */
	protected int VersionCode = -1;
	
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
			mBtService.setHandler(mHandler);
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBtService.setHandler(null);
			mBound = false;
			mBtService = null;
		}
	};

	/* (non-Javadoc)
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DebugLog.D(TAG, "+++onCreate+++");
		super.onCreate(savedInstanceState);
		try
		{
		    VersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		}
		catch (NameNotFoundException e)
		{
		    DebugLog.V(getTag(), e.getMessage());
		}
		
		
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
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onResume()
	 */
	@Override
	public void onResume() {
		DebugLog.D(TAG, "+  onResume  +");
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onPause()
	 */
	@Override
	public void onPause() {
		DebugLog.D(TAG, "-  onPause  -");
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onStart()
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
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onStop()
	 */
	@Override
	public void onStop() {
		DebugLog.D(TAG, "-- onStop --");

		super.onStop();
	}

	/* (non-Javadoc)
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#onDestroy()
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
			if ("com.cfms.android.mousedroid.bluetooth.BluetoothService"
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

	
	// The Handler that gets information back from the BluetoothChatService
    /** The m handler. */
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                onBTStateChanged(msg.arg1, msg.arg2);
                break;
            case BluetoothService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                int length = msg.arg1;
                onBTMessageWritten(writeBuf, length);
                break;
            case BluetoothService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                length = msg.arg1;
                onBTMessageRead(readBuf, length);
                break;
            case BluetoothService.MESSAGE_ERROR:
            	int errorCode = msg.arg1;
            	onBTError(errorCode);
            	break;
            }
        }
    };

    /** The command buffer. */
    byte[] commandBuffer = new byte[128];
	
	/** The command length. */
	int commandLength = 0;
	
	/**
	 * Called when a new bluetooth message is received by the BluetoothService
	 * Override this method to handle this event
	 * By default the implementation releases the buffer back to the factory to prevent memory overloads.
	 * Any override must call super.onBTMessageRead(message, length)
	 * 
	 * @param message the message
	 * @param length the length
	 */
	public void onBTMessageRead(byte[] message, int length) {
		//Parse out additional listeners
		for(int i = 0; i< length; i++)
		{
			commandBuffer[commandLength] = message[i];
			if(commandLength == 0)
			{
				if(message[i] == BTProtocol.PACKET_PREAMBLE){
					commandLength++;
				}
			}else{
				commandLength++;
			}
			
			if(isFullCommand(commandBuffer, commandLength)){
				executeCommand(commandBuffer, commandLength);
				commandLength = 0;
			}
		}
		
		//Release the buffer
		ByteBufferFactory.releaseBuffer(message);
	}


	/**
	 * Checks if is full command.
	 *
	 * @param commandBuffer the command buffer
	 * @param len the len
	 * @return true, if is full command
	 */
	public boolean isFullCommand(byte[] commandBuffer, int len) {
		if(len >= 4){
			if(commandBuffer[len - 1] == BTProtocol.LF
					&& commandBuffer[len - 2] == BTProtocol.CR){
				return true;
			}
		}
		return false;
	}

	/**
	 * Execute command.
	 *
	 * @param commandBuffer the command buffer
	 * @param len the len
	 */
	public void executeCommand(byte[] commandBuffer, int len) {
		PacketID ID = PacketID.get(commandBuffer[1]);
		if(ID == null)
			return;
		
		switch(ID)
		{
		case DISCONNECT:
			DebugLog.D(getTag(), "Disconnect Packet");
			mBtService.disconnect();
			break;
		case GET_VERSION:
			sendVersion();
			break;

		}
	}

	/**
	 * Send version.
	 */
	private void sendVersion() {
		byte msb = (byte) (VersionCode >> 8);
		byte lsb = (byte) (VersionCode);
		
		byte[] packet = {BTProtocol.PACKET_PREAMBLE, PacketID.RET_VERSION.getCode(), msb, lsb, BTProtocol.CR, BTProtocol.LF};
		mBtService.write(packet, packet.length);
	}


	/**
	 * Called when a message is written out to the bluetooth socket.
	 * Override this method to handle this event
	 *
	 * @param message the message
	 * @param length the length of the message
	 */
	public void onBTMessageWritten(byte[] message, int length) {
		
	}
	
	/**
	 * Called when the BluetoothService state changes
	 * Override this method to handle this event.
	 *
	 * @param oldState the old state
	 * @param newState the new state
	 */
	public void onBTStateChanged(int oldState, int newState){
		//Do nothing, event handler
	}

	
	/**
	 * On BluetoothService Error
	 * Override this method to handle this event.
	 *
	 * @param errorCode the error code
	 */
	public void onBTError(int errorCode) {
		//Do nothing, event handler
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
	 * @see com.cfms.android.mousedroid.activity.BaseActivity#getTag()
	 */
	@Override
	public String getTag() {
		return TAG;
	}
}

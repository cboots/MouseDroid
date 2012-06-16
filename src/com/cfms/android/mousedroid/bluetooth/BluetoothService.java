/*
 * 
 */
package com.cfms.android.mousedroid.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.PacketID;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.MouseDroidActivity;
import com.cfms.android.mousedroid.activity.PreferencesActivity;
import com.cfms.android.mousedroid.utils.DebugLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothService.
 */
public class BluetoothService extends Service {

	/** The Version code. */
	protected int VersionCode = -1;
	
	/** The Constant TAG. */
	private final static String TAG = "BluetoothService";

	/** The D. */
	private final boolean D = true;

	// Binder given to clients
	/** The Binder. */
	private final IBinder mBinder = new BluetoothBinder();

	/** The Adapter. */
	private BluetoothAdapter mAdapter;

	/** The Connect thread. */
	private ConnectThread mConnectThread;

	/** The Connected thread. */
	private ConnectedThread mConnectedThread;

	private ConnectionManager mConnectionManager;
	
	/** The State. */
	private int mState;

	/** The m in foreground. */
	private boolean mInForeground = false;

	private BluetoothEventListener mListener;

	/** The Constant ONGOING_NOTIFICATION. */
	private static final int ONGOING_NOTIFICATION = 101;

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_IDLE. */
	public static final int STATE_IDLE = 1;

	/** The Constant STATE_CONNECTING. */
	public static final int STATE_CONNECTING = 2;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 3;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant MESSAGE_WRITE. */
	public static final int MESSAGE_WRITE = 1;

	/** The Constant MESSAGE_READ. */
	public static final int MESSAGE_READ = 2;

	/** The Constant MESSAGE_ERROR. */
	public static final int MESSAGE_ERROR = 3;

	/** The Constant ERRORCODE_CONNECTION_FAILED. */
	public static final int ERRORCODE_CONNECTION_FAILED = 1;

	/** The Constant ERRORCODE_CONNECTION_LOST. */
	public static final int ERRORCODE_CONNECTION_LOST = 2;


	// /** The Constant MY_UUID_INSECURE. */
	// private static final UUID MY_UUID_INSECURE = UUID
	// .fromString("fa46ddbb-0694-49f6-993c-1a1621f2e34d");
	/** The Constant SPP_UUID. */
	private static final UUID SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class BluetoothBinder extends Binder {

		/**
		 * Gets the service.
		 * 
		 * @return the service
		 */
		BluetoothService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return BluetoothService.this;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		DebugLog.I("BluetoothService", "Service onCreate()");
		super.onCreate();
		try
		{
		    VersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		}
		catch (NameNotFoundException e)
		{
		    DebugLog.V(TAG, e.getMessage());
		}

		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;

	}

	/**
	 * Begin foreground.
	 */
	private void beginForeground() {
		Resources res = this.getResources();

		// TODO make this a valid target activity
		Intent notificationIntent = new Intent(this, MouseDroidActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(
						BitmapFactory.decodeResource(res,
								R.drawable.ic_launcher))
				.setTicker("Mouse Droid Connection")
				.setWhen(System.currentTimeMillis())
				.setContentTitle("Bluetooth")
				.setContentText("Bluetooth service running")
				.setContentIntent(pendingIntent);

		Notification notification = builder.getNotification();

		startForeground(ONGOING_NOTIFICATION, notification);
		mInForeground = true;
	}

	/**
	 * End foreground.
	 */
	private void endForeground() {
		this.stopForeground(true);
		mInForeground = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DebugLog.I("BluetoothService", "Service onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		DebugLog.I("BluetoothService", "Service onDestroy()");
		super.onDestroy();
	}

	/**
	 * Sets the message handler.
	 * 
	 * @param handler
	 *            the new handler
	 */
	public void setEventListener(BluetoothEventListener listener) {
		mListener = listener;
	}

	/**
	 * Reset the bluetooth service to idle mode with no connection.
	 * */
	public synchronized void reset() {
		DebugLog.D(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_IDLE);

	}

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return (mState == STATE_CONNECTED);
	}

	/**
	 * Error.
	 * 
	 * @param errorCode
	 *            the error code
	 */
	private synchronized void error(int errorCode) {
		DebugLog.E(TAG, "error: " + errorCode);
		if (mHandler != null) {
			mHandler.obtainMessage(MESSAGE_ERROR, errorCode, -1);
		}
	}

	/**
	 * Set the current state of the chat connection.
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		int oldState = mState;

		DebugLog.D(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		if (mHandler != null) {
			mHandler.obtainMessage(MESSAGE_STATE_CHANGE, oldState, state)
					.sendToTarget();
		}

		if (state == STATE_CONNECTED) {
			if (!mInForeground)
				beginForeground();
		} else {
			if (mInForeground)
				endForeground();
		}

	}

	/**
	 * Return the current connection state.
	 * 
	 * @return the state
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Disconnect.
	 */
	public synchronized void disconnect() {

		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the disconnect unsynchronized
		r.disconnect();
		r.cancel();
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device) {
		DebugLog.D(TAG, "connect to: " + device);

		// Cancel any current processes and kill any current connections
		if (mState == STATE_CONNECTED) {
			// Need to safely disconnect
			disconnect();
		} else if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, 	PreferencesActivity.getBoolean(getApplicationContext(),
						PreferencesActivity.PREF_HTC_BLUETOOTH_COMPATIBILITY));
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection.
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		DebugLog.D(TAG, "socket connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		setState(STATE_CONNECTED);
	}

	/**
	 * Disconnected.
	 */
	public synchronized void disconnected() {
		DebugLog.D(TAG, "disconnected");
		reset();
	}

	/**
	 * Write.
	 * 
	 * @param message
	 *            the message
	 */
	public void write(byte[] message) {
		write(message, message.length);
	}

	/**
	 * Write.
	 * 
	 * @param message
	 *            the message
	 * @param length
	 *            the length
	 */
	public void write(byte[] message, int length) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(message, length);
	}

	/**
	 * On bt message read.
	 *
	 * @param buffer the buffer
	 * @param length the length
	 */
	protected void onBTMessageRead(byte[] buffer, int length) {
		if (mHandler != null) {
			mHandler.obtainMessage(BluetoothService.MESSAGE_READ, length, -1,
					buffer).sendToTarget();
		}
	}

	/**
	 * On bt message written.
	 *
	 * @param buffer the buffer
	 * @param length the length
	 */
	protected void onBTMessageWritten(byte[] buffer, int length) {
		if (mHandler != null) {
			mHandler.obtainMessage(BluetoothService.MESSAGE_WRITE, length, -1,
					buffer).sendToTarget();
		}
	}

	
	/**
	 * Called when a ping packet is received
	 * @param pingID
	 */
	protected void onPing(int pingID) {
		if(mConnectionManager != null)
		{
			mConnectionManager.onPing(pingID);
		}
	}
	
	
	/**
	 * Called when a ping return packet is received
	 * @param pingID
	 */
	protected void onPingReturn(int pingID) {
		if(mConnectionManager != null)
		{
			mConnectionManager.onPingReturn(pingID);
		}
	}
	
	
	
	
	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		error(ERRORCODE_CONNECTION_FAILED);

		// Start the service over to set idle mode
		reset();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		error(ERRORCODE_CONNECTION_LOST);

		// Start the service over to set idle mode
		reset();
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {

		/** The socket. */
		private final BluetoothSocket mmSocket;

		/** The device. */
		private final BluetoothDevice mmDevice;

		/** The Socket type. */
		private String mmSocketType;

		/**
		 * Instantiates a new connect thread.
		 *
		 * @param device the device
		 */
		@SuppressWarnings("unused")
		public ConnectThread(BluetoothDevice device) {
			this(device, false);
		}

		/**
		 * Instantiates a new connect thread.
		 *
		 * @param device the device
		 * @param useHTCCompatibility the use htc compatibility
		 */
		public ConnectThread(BluetoothDevice device, boolean useHTCCompatibility) {
			mmDevice = device;
			if (useHTCCompatibility)
				mmSocket = createCompatibilitySocket(device);
			else
				mmSocket = createSocket(device);
		}

		/**
		 * Creates the socket.
		 *
		 * @param device the device
		 * @return the bluetooth socket
		 */
		private BluetoothSocket createSocket(BluetoothDevice device) {
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device
						.createInsecureRfcommSocketToServiceRecord(SPP_UUID);

			} catch (IOException e) {
				Log.e(TAG, "Socket create() failed", e);
			}
			return tmp;
		}

		/**
		 * Creates the compatibility socket.
		 *
		 * @param device the device
		 * @return the bluetooth socket
		 */
		private BluetoothSocket createCompatibilitySocket(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				BluetoothDevice hxm = BluetoothAdapter.getDefaultAdapter()
						.getRemoteDevice(device.getAddress());
				Method m = hxm.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				tmp = (BluetoothSocket) m.invoke(device, 1);
			} catch (NoSuchMethodException e) {
				Log.e(TAG, "Socket Type: " + mmSocketType + "create() failed",
						e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Socket Type: " + mmSocketType + "create() failed",
						e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Socket Type: " + mmSocketType + "create() failed",
						e);
			} catch (InvocationTargetException e) {
				Log.e(TAG, "Socket Type: " + mmSocketType + "create() failed",
						e);
			}
			return tmp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			DebugLog.I(TAG, "BEGIN mConnectThread SocketType:" + mmSocketType);
			setName("ConnectThread" + mmSocketType);

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				DebugLog.W(TAG, "connecting thread crashed", e);
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					DebugLog.E(TAG, "unable to close() " + mmSocketType
							+ " socket during connection failure", e2);
				}
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				DebugLog.E(TAG, "close() of connect " + mmSocketType
						+ " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {

		/** The socket. */
		private final BluetoothSocket mmSocket;

		/** The in stream. */
		private final InputStream mmInStream;

		/** The out stream. */
		private final OutputStream mmOutStream;

		/** The mm disconnected. */
		private boolean mmDisconnected = false;

		/**
		 * Instantiates a new connected thread.
		 * 
		 * @param socket
		 *            the socket
		 */
		public ConnectedThread(BluetoothSocket socket) {
			DebugLog.D(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				DebugLog.E(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			DebugLog.I(TAG, "BEGIN mConnectedThread");
			byte[] buffer;
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					buffer = ByteBufferFactory.getBuffer();
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					// Send the obtained bytes to the UI Activity
					if (bytes > 0 && mHandler != null) {
						onBTMessageRead(buffer, bytes);
					} else {
						DebugLog.D(TAG, "Empty Read");
						ByteBufferFactory.releaseBuffer(buffer);
					}
				} catch (IOException e) {
					if (mmDisconnected) {
						DebugLog.I(TAG, "safely disconnected", e);
						disconnected();
					} else {
						DebugLog.E(TAG, "connection Lost", e);
						connectionLost();
					}
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 * @param length
		 *            the length
		 */
		public void write(byte[] buffer, int length) {
			try {
				if (D)
					DebugLog.D(TAG, "write length=" + length);
				mmOutStream.write(buffer, 0, length);

				// Share the sent message back to the UI Activity
				onBTMessageWritten(buffer, length);
			} catch (IOException e) {
				DebugLog.E(TAG, "Exception during write", e);
			}
		}

		/**
		 * Disconnect.
		 */
		public void disconnect() {
			mmDisconnected = true;
			// Disconnect packet
			byte[] buffer = { BTProtocol.PACKET_PREAMBLE,
					PacketID.DISCONNECT.getCode(), BTProtocol.CR, BTProtocol.LF };
			try {
				mmOutStream.write(buffer, 0, 4);
				// Share the sent message back to the UI Activity
				onBTMessageWritten(buffer, 4);
			} catch (IOException e) {
				DebugLog.E(TAG, "Exception during disconnect", e);
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				DebugLog.E(TAG, "close() of connect socket failed", e);
			}
		}
	}

	// The Handler that gets information back from the BluetoothChatService
    /** The m handler. */
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
            	if(mListener != null)
            	{
            		mListener.onBTStateChanged(msg.arg1, msg.arg2);
            	}
                
                break;
            case BluetoothService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                int length = msg.arg1;
                if(mListener != null)
                {
                	mListener.onBTMessageWritten(writeBuf, length);
                }
                break;
            case BluetoothService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                length = msg.arg1;
                //parse message and possibly distribute command
                ParseBTMessage(readBuf, length);
                break;
            case BluetoothService.MESSAGE_ERROR:
            	int errorCode = msg.arg1;
            	if(mListener != null)
            	{
            		mListener.onBTError(errorCode);
            	}
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
	public void ParseBTMessage(byte[] message, int length) {
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
			DebugLog.D(TAG, "Disconnect Packet");
			disconnect();
			break;
		case GET_VERSION:
			sendVersion();
			break;
		case PING:
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer, 2, 4);
			int pingID = bb.getInt();
			onPing(pingID);
			break;
		case PING_RETURN:
			bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer, 2, 4);
			pingID = bb.getInt();
			onPingReturn(pingID);
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
		write(packet, packet.length);
	}

	
	
	public interface BluetoothEventListener{
		public void onBTMessageWritten(byte[] message, int length);

		public void onBTStateChanged(int arg1, int arg2);

		public void onBTError(int errorCode);
		
		
		
	}
	
	private class ConnectionManager{

		public void onPing(int pingID) {
			// TODO Auto-generated method stub
			
		}

		public void onPingReturn(int pingID) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

/*
 * 
 */
package com.cfms.android.mousedroid.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.PacketID;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.MouseDroidActivity;
import com.cfms.android.mousedroid.utils.DebugLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothService.
 */
public class BluetoothService extends Service {

	/** The Constant TAG. */
	private final static String TAG = "BluetoothService";

	private final boolean D = true;

	// Binder given to clients
	/** The Binder. */
	private final IBinder mBinder = new BluetoothBinder();

	/** The Handler. */
	private Handler mHandler = null;

	/** The Adapter. */
	private BluetoothAdapter mAdapter;

	/** The Connect thread. */
	private ConnectThread mConnectThread;

	/** The Connected thread. */
	private ConnectedThread mConnectedThread;

	/** The State. */
	private int mState;

	/** The m in foreground. */
	private boolean mInForeground = false;

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

	// Unique UUID for this application
	/** The Constant MY_UUID_SECURE. */
	private static final UUID MY_UUID_SECURE = UUID
			.fromString("6201c3fc-22cc-4a55-8fc2-4f4342ad97e0");

	/** The Constant MY_UUID_INSECURE. */
	private static final UUID MY_UUID_INSECURE = UUID
			.fromString("fa46ddbb-0694-49f6-993c-1a1621f2e34d");

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
	public void setHandler(Handler handler) {
		mHandler = handler;
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
	 * Connect.
	 * 
	 * @param device
	 *            the device
	 */
	public synchronized void connect(BluetoothDevice device) {
		connect(device, false);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
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
		mConnectThread = new ConnectThread(device, secure);
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
	 * @param socketType
	 *            the socket type
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device, final String socketType) {
		DebugLog.D(TAG, "connected, Socket Type:" + socketType);

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
		mConnectedThread = new ConnectedThread(socket, socketType);
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

	protected void onBTMessageRead(byte[] buffer, int length) {
		if (mHandler != null) {
			mHandler.obtainMessage(BluetoothService.MESSAGE_READ, length, -1,
					buffer).sendToTarget();
		}
	}

	protected void onBTMessageWritten(byte[] buffer, int length) {
		if (mHandler != null) {
			mHandler.obtainMessage(BluetoothService.MESSAGE_WRITE, length, -1,
					buffer).sendToTarget();
		}
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
		 * @param device
		 *            the device
		 * @param secure
		 *            the secure
		 */
		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mmSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device
							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mmSocketType + "create() failed",
						e);
			}
			mmSocket = tmp;
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
			connected(mmSocket, mmDevice, mmSocketType);
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
		 * @param socketType
		 *            the socket type
		 */
		public ConnectedThread(BluetoothSocket socket, String socketType) {
			DebugLog.D(TAG, "create ConnectedThread: " + socketType);
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

}

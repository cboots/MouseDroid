package com.cfms.mousedroid.pc.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.cfms.mousedroid.BTProtocol;
import com.cfms.handler.Handler;
import com.cfms.handler.Looper;
import com.cfms.handler.Message;
import com.cfms.mousedroid.pc.MyLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothServer.
 */
public class BluetoothServer {

	/** Debug log flag. */
	protected boolean D = false;

	// Server string must not have any dashes in the ID.
	/** The Constant uuidStr. */
	// private static final String uuidStrInsecure =
	// "fa46ddbb069449f6993c1a1621f2e34d";//
	// fa46ddbb-0694-49f6-993c-1a1621f2e34d
	private static final String uuidStrInsecure = "0000110100001000800000805F9B34FB";// 00001101-0000-1000-8000-00805F9B34FB

	/** The Constant uuidStr. */
	private static final String uuidStrSecure = "6201c3fc22cc4a558fc24f4342ad97e0";// 6201c3fc-22cc-4a55-8fc2-4f4342ad97e0

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_LISTEN. */
	public static final int STATE_LISTEN = 1;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 2;

	/** The Constant MESSAGE_STATE_CHANGE. */
	public static final int MESSAGE_STATE_CHANGE = 0;

	/** The Constant MESSAGE_WRITE. */
	public static final int MESSAGE_WRITE = 1;

	/** The Constant MESSAGE_READ. */
	public static final int MESSAGE_READ = 2;

	/** The Constant MESSAGE_ERROR. */
	public static final int MESSAGE_ERROR = 3;

	/** The Constant ERRORCODE_CONNECTION_LOST. */
	public static final int ERRORCODE_CONNECTION_LOST = 2;
	
	public static final int ERRORCODE_CONNECTION_TIMEOUT = 3;

	/** The m listening thread. */
	private ListeningThread mListeningThread;

	/** The m connected thread. */
	private ConnectedThread mConnectedThread;

	private LooperThread mLooperThread;

	private ConnectionManager mConnectionManager;
	
	/** The m state. */
	int mState = STATE_NONE;

	BTEventListener mEventListener = null;

	private Handler mHandler = null;

	/**
	 * Instantiates a new bluetooth server.
	 */
	public BluetoothServer() {
	}

	class LooperThread extends Thread {

		public void run() {
			MyLog.log("LooperThread Running");
			Looper.prepare();

			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MESSAGE_STATE_CHANGE:
						if (mEventListener != null) {
							mEventListener.onStateChanged(msg.arg1, msg.arg2);
						}
						break;
					case MESSAGE_WRITE:
						byte[] writeBuf = (byte[]) msg.obj;
						int length = msg.arg1;
						if (mEventListener != null) {
							mEventListener.onBTMessageWritten(writeBuf, length);
						}
						// Release the buffer
						ByteBufferFactory.releaseBuffer(writeBuf);
						break;
					case MESSAGE_READ:
						byte[] readBuf = (byte[]) msg.obj;
						length = msg.arg1;
						if (mEventListener != null) {
							mEventListener.onBTMessageRead(readBuf, length);
						}
						// Release the buffer
						ByteBufferFactory.releaseBuffer(readBuf);
						break;
					case MESSAGE_ERROR:
						if (mEventListener != null) {
							int errorCode = msg.arg1;
							mEventListener.onError(errorCode);
						}
						break;
					}
				}
			};

			mPingHandler = new Handler(){
				 @Override
			        public void handleMessage(Message msg) {
					 	//DebugLog.D(TAG, msg.toString());
					 	if(mConnectionManager == null)
					 		return;
					 	
					 	if(BluetoothServer.this.getState() != STATE_CONNECTED)
					 	{
					 		mConnectionManager.stopPinging();
					 		return;
					 	}
					 	
					 	int pingID = msg.arg1;
			            switch (msg.what) {
			            case ConnectionManager.PING_SEND:
			            	mConnectionManager.sendPing(pingID);
			            	break;
			            case ConnectionManager.PING_SEND_RESPONSE:
			            	mConnectionManager.sendPingResponse(pingID);
			            	break;
			            case ConnectionManager.PING_GOT_RESPONSE:
			            	mConnectionManager.gotResponse(pingID);
			            	break;
			            case ConnectionManager.PING_TIMEOUT:
			            	mConnectionManager.pingTimeout(pingID);
			            	break;
			            }
			        }

			};
			
			Looper.loop();
		}
	}

	public void setEventListener(BTEventListener listener) {
		mEventListener = listener;
	}

	/**
	 * Start.
	 */
	public synchronized void start() {
		// Ensure hander is set up
		if (mHandler == null) {
			if (mLooperThread != null) {
				MyLog.log("Error, looper thread invalidated");
			} else {
				mLooperThread = new LooperThread();
				mLooperThread.start();
				// Loop until LooperThread inited
				while (mHandler == null) {
					try {

						this.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MyLog.log("LooperThread waiting");

				}
			}
		}

		if (mConnectedThread != null) {
			if (mState == STATE_CONNECTED)
			{
				byte[] cmd = BTProtocol.getDisconnectPacket();
				write(cmd, cmd.length);
			}

			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_LISTEN);

		if (mListeningThread == null) {
			mListeningThread = new ListeningThread();
			mListeningThread.start();
		}

	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	private synchronized void setState(int state) {
		int oldState = mState;
		mState = state;
		onBTStateChanged(oldState, state);
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public int getState() {
		return mState;
	}

	/**
	 * Called when a new bluetooth message is received by the BluetoothService
	 * 
	 * @param message
	 *            the message
	 * @param length
	 *            the length
	 */
	protected void onBTMessageRead(byte[] message, int length) {
		if (D)
			MyLog.log("onBTMessageRead: len=" + length);
		if (mHandler != null) {
			mHandler.obtainMessage(MESSAGE_READ, length, -1, message)
					.sendToTarget();
		}
	}

	/**
	 * Called when a message is written out to the bluetooth socket.
	 * 
	 * @param message
	 *            the message
	 * @param length
	 *            the length of the message
	 */
	protected void onBTMessageWritten(byte[] message, int length) {
		if (D)
			MyLog.log("onBTMessageWritten: len=" + length);
		if (mHandler != null) {
			mHandler.obtainMessage(MESSAGE_WRITE, length, -1, message)
					.sendToTarget();
		}
	}

	/**
	 * Called when the BluetoothService state changes Override this method to
	 * handle this event
	 */
	private void onBTStateChanged(int oldState, int newState) {

		if (D)
			MyLog.log("onBTStateChanged: " + oldState + "->" + newState);
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(MESSAGE_STATE_CHANGE,
					oldState, newState);
			msg.sendToTarget();
		}

	}

	/**
	 * On BluetoothService Error Override this method to handle this event
	 * 
	 * @param errorCode
	 *            the error code
	 */
	private void onBTError(int errorCode) {
		if (D)
			MyLog.log("onBTError: " + errorCode);
		if (mHandler != null) {
			mHandler.obtainMessage(MESSAGE_ERROR, errorCode, -1);
		}
	}
	
	public void sendPingResponse(int pingID)
	{
		if(mConnectionManager != null)
			mConnectionManager.sendPingResponse(pingID);
		
	}

	public void onPingReturnPacket(int pingID)
	{
		if(mConnectionManager != null)
			mConnectionManager.onPingReturnPacket(pingID);
	}
	
	public void gotPingPacket(int pingID)
	{
		if(mConnectionManager != null)
			mConnectionManager.onPingPacket(pingID);
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
	 * Connected.
	 * 
	 * @param connection
	 *            the connection
	 */
	private synchronized void connected(StreamConnection connection) {
		// Establish connection
		if (mListeningThread != null) {
			mListeningThread.cancel();
			mListeningThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectedThread = new ConnectedThread(connection);
		mConnectedThread.start();

		setState(STATE_CONNECTED);

	}

	/**
	 * Indicate that the connection was lost.
	 */
	private void connectionLost() {
		onBTError(ERRORCODE_CONNECTION_LOST);
		// Restart the server
		start();
	}


	/**
	 * Disconnect.
	 */
	public synchronized void timeoutConnection() {

		onBTError(ERRORCODE_CONNECTION_TIMEOUT);
		disconnect();
	}
	
	public void disconnect() {
		if (getState() == STATE_CONNECTED) {
			byte[] cmd = BTProtocol.getDisconnectPacket();
			write(cmd, cmd.length);
		}
		// Restart the server
		start();
	}

	/**
	 * The Class ListeningThread.
	 */
	public class ListeningThread extends Thread {

		/** The notifier. */
		StreamConnectionNotifier mmNotifier;

		/** The connection. */
		StreamConnection mmConnection = null;

		boolean mmSecure = false;

		private boolean mmCanceled = false;

		/**
		 * Instantiates a new listening thread.
		 */
		public ListeningThread() {
			this(false);
		}

		/**
		 * Instantiates a new listening thread.
		 */
		public ListeningThread(boolean secure) {
			mmSecure = secure;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			waitForConnection();
		}

		/**
		 * Wait for connection.
		 */
		private void waitForConnection() {
			MyLog.log("Listening Thread Running");
			LocalDevice local = null;
			try {
				local = LocalDevice.getLocalDevice();
				if (local.getDiscoverable() != DiscoveryAgent.GIAC)
					local.setDiscoverable(DiscoveryAgent.GIAC);

				String url;
				if (mmSecure) {
					url = "btspp://localhost:" + uuidStrSecure
							+ ";name=RemoteBluetooth";
				} else {
					url = "btspp://localhost:" + uuidStrInsecure
							+ ";name=RemoteBluetooth";
				}
				mmNotifier = (StreamConnectionNotifier) Connector.open(url);

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			while (mState == STATE_LISTEN) {
				try {
					MyLog.log("waiting for connection...");
					mmConnection = mmNotifier.acceptAndOpen();

					if (mmConnection != null) {
						switch (mState) {
						case STATE_LISTEN:
							connected(mmConnection);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							try {
								MyLog.log("Connection attempted during invalid state.");
								mmConnection.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
							break;
						}
					}
				} catch (Exception e) {
					if (!mmCanceled)
						e.printStackTrace();
					else if (D)
						MyLog.log("ListenThread canceled");

					break;
				}
			}
			MyLog.log("Listening Thread Stopped");

		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			if (D)
				MyLog.log("ListenThread cancel");
			try {
				mmCanceled = true;
				mmNotifier.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * The Class ProcessConnectionThread.
	 */
	public class ConnectedThread extends Thread {

		/** The m connection. */
		private StreamConnection mmConnection;

		InputStream mmInputStream;
		OutputStream mmOutputStream;

		private boolean mmCanceled = false;

		/**
		 * Instantiates a new process connection thread.
		 * 
		 * @param connection
		 *            the connection
		 */
		public ConnectedThread(StreamConnection connection) {
			mmConnection = connection;
			
			mConnectionManager = new ConnectionManager();
			// prepare to receive data
			try {
				mmInputStream = mmConnection.openInputStream();
				mmOutputStream = mmConnection.openOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			byte[] buffer;
			int bytes;

			if(mConnectionManager != null)
			{
				mConnectionManager.startPinging();
			}
			// Keep listening to the InputStream while connected
			while (!mmCanceled) {
				try {
					buffer = ByteBufferFactory.getBuffer();
					// Read from the InputStream
					bytes = mmInputStream.read(buffer);
					if (bytes > 0)
						onBTMessageRead(buffer, bytes);
					else
						ByteBufferFactory.releaseBuffer(buffer);

				} catch (IOException e) {
					MyLog.log("Connection Terminated");
					connectionLost();// Restarts service

					break;
				}
			}

			if (!mmCanceled) {
				onBTError(ERRORCODE_CONNECTION_LOST);
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
				mmOutputStream.write(buffer, 0, length);

				// Share the sent message back to the UI Activity
				onBTMessageWritten(buffer, length);
			} catch (IOException e) {
				MyLog.log("Exception during write:");
				e.printStackTrace();
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				if(mConnectionManager != null)
				{
					mConnectionManager.stopPinging();
				}
				
				mmCanceled = true;
				mmInputStream.close();
				mmConnection.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static interface BTEventListener {
		public void onStateChanged(int oldState, int newState);

		public void onError(int errorCode);

		public void onBTMessageWritten(byte[] message, int length);

		public void onBTMessageRead(byte[] message, int length);
	}
	
	protected Handler mPingHandler = null;
	
	private class ConnectionManager{

		private static final int PING_SEND = 1;
		
		private static final int PING_TIMEOUT = 2;
		
		private static final int PING_SEND_RESPONSE = 3;
		
		private static final int PING_GOT_RESPONSE = 4;
		
		private static final int PING_TIMEOUT_MS = 1000;
		
		private static final int PING_PERIOD_MS = 200;
		
		private static final int DROPPED_PINGS_ALLOWED = 10;
		
		private int lastID = -1;
		
		private int mPingCounter = 0;

		private boolean mPinging = false;
		
		private ArrayList<Integer> mPendingPings = new ArrayList<Integer>();
			
		public ConnectionManager()
		{
			

		}
		
		protected int getNextPingID() {
			lastID += 1;
			return lastID;
		}

		public void startPinging(){
			mPinging = true;
			mPingCounter = DROPPED_PINGS_ALLOWED;
			//Send first ping
			Message ping = mPingHandler.obtainMessage(PING_SEND, getNextPingID(), -1);
			ping.sendToTarget();
		}
		
		public void stopPinging(){
			mPinging = false;
		}
		
		private void replyToPing(int pingID)
		{
			Message ping = mPingHandler.obtainMessage(PING_SEND_RESPONSE, pingID, -1);
			ping.sendToTarget();
		}
		
		public void onPingPacket(int pingID) {
			replyToPing(pingID);
		}

		public void onPingReturnPacket(int pingID) {
			Message ping = mPingHandler.obtainMessage(PING_GOT_RESPONSE, pingID, -1);
			ping.sendToTarget();
		}

		public void sendPing(int pingID) {
			//actually write ping packet
			byte[] ping = BTProtocol.getPingPacket(pingID);
			write(ping, ping.length);
			
			//Queue timeout packet
			Message timeout = mPingHandler.obtainMessage(PING_TIMEOUT, pingID, -1);
			mPingHandler.sendMessageDelayed(timeout, PING_TIMEOUT_MS);
			
			//Add to list of pending pings
			mPendingPings.add(new Integer(pingID));
			
			//Queue next packet
			if(mPinging)
			{
				Message nextPing = mPingHandler.obtainMessage(PING_SEND, getNextPingID(), -1);
				mPingHandler.sendMessageDelayed(nextPing, PING_PERIOD_MS);
			}
		}

		public void pingTimeout(int pingID) {
			if(mPendingPings.contains(new Integer(pingID)))
			{
				//ping timed out
				if(D) MyLog.log("Ping Timed Out id="+ pingID);
				gotResponse(pingID);
				
				//Decrement counter
				mPingCounter--;
				if(D) MyLog.log("ping counter=" + mPingCounter);
				if(mPingCounter <= 0)
				{
					//Connection Timed Out
					stopPinging();
					timeoutConnection();
				}
			}else{
				//ping succeeded
				if(mPingCounter < DROPPED_PINGS_ALLOWED)
				{
					mPingCounter++;
					if(D) MyLog.log("ping counter=" + mPingCounter);
				}
			}
		}

		public void gotResponse(int pingID) {
			mPendingPings.remove(new Integer(pingID));
		}

		public void sendPingResponse(int pingID) {
			//actually write response packet
			byte[] pingResponse = BTProtocol.getPingReturnPacket(pingID);
			write(pingResponse, pingResponse.length);
		}		
	}


}

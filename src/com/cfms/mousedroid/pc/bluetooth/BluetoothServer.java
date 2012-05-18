package com.cfms.mousedroid.pc.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.cfms.mousedroid.pc.MyLog;

// TODO: Auto-generated Javadoc
/**
 * The Class BluetoothServer.
 */
public class BluetoothServer {

	/** Debug log flag. */
	protected boolean D = true;

	// Server string must not have any dashes in the ID.
	/** The Constant uuidStr. */
	private static final String uuidStrInsecure = "fa46ddbb069449f6993c1a1621f2e34d";// fa46ddbb-0694-49f6-993c-1a1621f2e34d

	/** The Constant uuidStr. */
	private static final String uuidStrSecure = "6201c3fc22cc4a558fc24f4342ad97e0";// 6201c3fc-22cc-4a55-8fc2-4f4342ad97e0

	/** The Constant STATE_NONE. */
	public static final int STATE_NONE = 0;

	/** The Constant STATE_LISTEN. */
	public static final int STATE_LISTEN = 1;

	/** The Constant STATE_CONNECTED. */
	public static final int STATE_CONNECTED = 2;
	
	/** The Constant ERRORCODE_CONNECTION_LOST. */
	public static final int ERRORCODE_CONNECTION_LOST = 2;

	/** The m listening thread. */
	ListeningThread mListeningThread;

	/** The m connected thread. */
	ProcessConnectionThread mConnectedThread;

	/** The m state. */
	int mState = STATE_NONE;

	BTEventListener mEventListener = null;

	/**
	 * Instantiates a new bluetooth server.
	 */
	public BluetoothServer() {

	}

	public void setEventListener(BTEventListener listener) {
		mEventListener = listener;
	}

	/**
	 * Start.
	 */
	public synchronized void start() {
		if (mConnectedThread != null) {
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
	private void onBTMessageRead(byte[] message, int length) {
		if(D) MyLog.log("onBTMessageRead: len="+ length );
		if (mEventListener != null) {
			mEventListener.onBTMessageRead(message, length);
		}
		// Release the buffer
		ByteBufferFactory.releaseBuffer(message);
	}

	/**
	 * Called when a message is written out to the bluetooth socket.
	 * 
	 * @param message
	 *            the message
	 * @param length
	 *            the length of the message
	 */
	private void onBTMessageWritten(byte[] message, int length) {
		if(D) MyLog.log("onBTMessageWritten: len="+ length );
		if (mEventListener != null) {
			mEventListener.onBTMessageWritten(message, length);
		}
		// Release the buffer
		ByteBufferFactory.releaseBuffer(message);
	}

	/**
	 * Called when the BluetoothService state changes Override this method to
	 * handle this event
	 */
	private void onBTStateChanged(int oldState, int newState) {
		if(D) MyLog.log("onBTStateChanged: "+ oldState + "->" + newState);
		if (mEventListener != null) {
			mEventListener.onStateChanged(oldState, newState);
		}
	}

	/**
	 * On BluetoothService Error Override this method to handle this event
	 * 
	 * @param errorCode
	 *            the error code
	 */
	private void onBTError(int errorCode) {
		if(D) MyLog.log("onBTError: "+ errorCode);
		if (mEventListener != null) {
			mEventListener.onError(errorCode);
		}
	}

	/**
	 * Connected.
	 * 
	 * @param connection
	 *            the connection
	 */
	private synchronized void connected(StreamConnection connection) {
		// TODO allow only one connection
		// Establish connection
		if (mListeningThread != null) {
			mListeningThread.cancel();
			mListeningThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectedThread = new ProcessConnectionThread(connection);
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

	public void disconnect() {
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
			LocalDevice local = null;
			try {
				local = LocalDevice.getLocalDevice();
				if(local.getDiscoverable() != DiscoveryAgent.GIAC)
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
					if(D) MyLog.log("waiting for connection...");
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
					if(!mmCanceled)
						e.printStackTrace();
					else
						if(D) MyLog.log("ListenThread canceled");
					return;
				}
			}

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
	public class ProcessConnectionThread extends Thread {

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
		public ProcessConnectionThread(StreamConnection connection) {
			mmConnection = connection;
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

			// Keep listening to the InputStream while connected
			while (!mmCanceled) {
				try {
					buffer = ByteBufferFactory.getBuffer();
					// Read from the InputStream
					bytes = mmInputStream.read(buffer);
					if(bytes > 0)
						onBTMessageRead(buffer, bytes);
					else
						ByteBufferFactory.releaseBuffer(buffer);
					
				} catch (IOException e) {
					MyLog.log("Connection Terminated");
					connectionLost();//Restarts service
					
					break;
				}
			}
			
			if(!mmCanceled){
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
				mmCanceled  = true;
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

}

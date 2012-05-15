package com.cfms.mousedroid.pc;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import java.util.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ListeningThread implements Runnable {

	public ListeningThread() {

	}

	@Override
	public void run() {
		waitForConnection();
	}

	private void waitForConnection() {
		LocalDevice local = null;

		StreamConnectionNotifier notifier;
		StreamConnection connection = null;

		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);

			UUID uuid = UUID
					.fromString("fa46ddbb-0694-49f6-993c-1a1621f2e34d");
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier) Connector.open(url);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			try {
				System.out.println("waiting for connection...");
				connection = notifier.acceptAndOpen();
//TODO allow only one connection
				// Establish connection
				Thread processThread = new Thread(new ProcessConnectionThread(
						connection));
				processThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

	}

}

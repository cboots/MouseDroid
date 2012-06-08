package com.cfms.mousedroid.pc;

import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.plaf.metal.MetalIconFactory;

import com.cfms.mousedroid.pc.bluetooth.BluetoothServer;

public class SysTray {
	private static CommandProcessor sCmdProcessor = null;

	protected static TrayIcon sTrayIcon;

	private static Image getImage() throws HeadlessException {

		Icon defaultIcon = MetalIconFactory.getTreeComputerIcon();
		Image img = new BufferedImage(defaultIcon.getIconWidth(),

		defaultIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		defaultIcon.paintIcon(new Panel(), img.getGraphics(), 0, 0);
		return img;
	}

	private static PopupMenu createPopupMenu() throws HeadlessException {

		PopupMenu menu = new PopupMenu();
		MenuItem exit = new MenuItem("Exit");

		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		MenuItem startListening = new MenuItem("Start Listening");

		startListening.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sCmdProcessor.startListening();
			}
		});

		menu.add(exit);
		menu.add(startListening);

		return menu;

	}

	public static void main(String[] args) throws Exception {
		sCmdProcessor = new CommandProcessor();

		sCmdProcessor
				.setStatusListener(new CommandProcessor.StatusEventListener() {

					@Override
					public void onStateChanged(int oldState, int newState) {
						if (newState == BluetoothServer.STATE_LISTEN) {
							sTrayIcon.displayMessage("Connecting...",
									"Waiting for connection", MessageType.INFO);
							sTrayIcon.setToolTip("Waiting for connection");
							// TODO set icon to waiting for connection
						} else if (newState == BluetoothServer.STATE_CONNECTED) {
							sTrayIcon.displayMessage("Connected",
									"Connection Established", MessageType.INFO);
							sTrayIcon.setToolTip("Connection Established");
							// TODO set icon to connected
						} else if (newState == BluetoothServer.STATE_NONE) {
							sTrayIcon.displayMessage("Disconnected",
									"Server is Down", MessageType.WARNING);
							sTrayIcon.setToolTip("Server is Down");
							// TODO set icon to disconnected
						}
					}

					@Override
					public void onError(int errorCode) {
						// TODO switch on error code
						sTrayIcon.displayMessage("Error", "Errorcode: "
								+ errorCode, MessageType.ERROR);
					}

					@Override
					public void onDisconnect() {
						sCmdProcessor.startListening();
					}
				});

		sTrayIcon = new TrayIcon(getImage(), "This is a Java Tray Icon",
				createPopupMenu());

		sTrayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchMainWindow();
			}
		});

		SystemTray.getSystemTray().add(sTrayIcon);

		//sCmdProcessor.startListening();
	}

	private static void launchMainWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow(sCmdProcessor);
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

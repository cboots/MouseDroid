package com.cfms.mousedroid.pc;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.cfms.mousedroid.pc.bluetooth.BTProtocol;
import com.cfms.mousedroid.pc.bluetooth.BTProtocol.MouseButton;
import com.cfms.mousedroid.pc.bluetooth.BTProtocol.MouseButtonEvent;
import com.cfms.mousedroid.pc.bluetooth.BTProtocol.PacketID;
import com.cfms.mousedroid.pc.bluetooth.BluetoothServer;
import com.cfms.mousedroid.pc.bluetooth.BluetoothServer.BTEventListener;

public class MainWindow extends JFrame implements BTEventListener {

	public static final byte MajorVersion = 1;
	public static final byte MinorVersion = 0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 717984543671268522L;
	private JPanel contentPane;
	private JButton btnNewButton;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private BluetoothServer mBTServer;
	private Robot mRobot;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		initComponents();
		try {
			mRobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		btnNewButton = new JButton("Start Listening");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				do_btnNewButton_actionPerformed(arg0);
			}
		});
		contentPane.add(btnNewButton, BorderLayout.NORTH);

		progressBar = new JProgressBar();
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		lblStatus = new JLabel("No Connection");
		contentPane.add(lblStatus, BorderLayout.CENTER);
	}

	protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
		mBTServer = new BluetoothServer();
		mBTServer.setEventListener(this);
		mBTServer.start();
		
	}

	@Override
	public void onStateChanged(int oldState, int newState) {
		if(newState == BluetoothServer.STATE_LISTEN){
			lblStatus.setText("Waiting for Connection");
			progressBar.setIndeterminate(true);
		}else if(newState == BluetoothServer.STATE_CONNECTED){
			lblStatus.setText("Connected");
			progressBar.setIndeterminate(false);
		}else if(newState == BluetoothServer.STATE_NONE){
			lblStatus.setText("Server Down");
			progressBar.setIndeterminate(false);
		}
	}

	@Override
	public void onError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBTMessageWritten(byte[] message, int length) {
		// TODO Auto-generated method stub
		
	}

	byte[] commandBuffer = new byte[128];
	int commandLength = 0;
	
	@Override
	public void onBTMessageRead(byte[] message, int length) {
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
	}

	public boolean isFullCommand(byte[] commandBuffer, int len) {
		if(len >= 4){
			if(commandBuffer[len - 1] == BTProtocol.LF
					&& commandBuffer[len - 2] == BTProtocol.CR){
				return true;
			}
		}
		return false;
	}

	public void executeCommand(byte[] commandBuffer, int len) {
		PacketID ID = PacketID.get(commandBuffer[1]);
		if(ID == null)
			return;
		
		switch(ID)
		{
		case DISCONNECT:
			MyLog.log("Disconnect");
			mBTServer.disconnect();
			break;
		case GET_VERSION:
			MyLog.log("GetVersion");
			sendVersion();
			break;
		case MOVE_MOUSE:
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer[2]);
			bb.put(commandBuffer[3]);
			bb.put(commandBuffer[4]);
			bb.put(commandBuffer[5]);
			int dx = bb.getShort(0);
			int dy = bb.getShort(2);
			
			MyLog.log("Move Mouse: " + dx + "," + dy);
			mouseMoveEvent(dx, dy);
			break;
		case MOUSE_BUTTON_EVENT:
			mouseButtonEvent(commandBuffer[2], commandBuffer[3]);
			break;
		}
	}

	private void mouseMoveEvent(int dx, int dy) {
		Point currentMouse = MouseInfo.getPointerInfo().getLocation();
		mRobot.mouseMove(dx + currentMouse.x, dy + currentMouse.y);
		
	}

	private void mouseButtonEvent(byte buttonCode, byte eventCode){
		MouseButton MB = MouseButton.get(buttonCode);
		MouseButtonEvent MBE = MouseButtonEvent.get(eventCode);
		
		int robotButtonCode = 0;
		switch(MB){
		case BUTTON1:
			robotButtonCode = InputEvent.BUTTON1_MASK;
			break;
		case BUTTON2:
			robotButtonCode = InputEvent.BUTTON2_MASK;
			break;
		case BUTTON3:
			robotButtonCode = InputEvent.BUTTON3_MASK;
			break;
		}
		
		switch(MBE){
		case PRESS:
			MyLog.log("Mouse Press: " + MB.toString());
			mRobot.mousePress(robotButtonCode);
			break;
		case RELEASE:
			MyLog.log("Mouse Release: " + MB.toString());
			mRobot.mouseRelease(robotButtonCode);	
			break;
		}
	}
	
	private void sendVersion() {
		byte[] packet = {BTProtocol.PACKET_PREAMBLE, PacketID.RET_VERSION.getCode(), MajorVersion, MinorVersion, BTProtocol.CR, BTProtocol.LF};
		mBTServer.write(packet, packet.length);
	}

	
}

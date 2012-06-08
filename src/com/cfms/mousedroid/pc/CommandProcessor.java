package com.cfms.mousedroid.pc;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.BTProtocol.KeyEventType;
import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.BTProtocol.PacketID;
import com.cfms.android.mousedroid.KeyCode;
import com.cfms.mousedroid.pc.bluetooth.BluetoothServer;
import com.cfms.mousedroid.pc.bluetooth.BluetoothServer.BTEventListener;

public class CommandProcessor implements BTEventListener {

	protected boolean D = false;
	public static final byte MajorVersion = 1;
	public static final byte MinorVersion = 0;
	
	private BluetoothServer mBTServer;
	private Robot mRobot;
	private CommandEventListener mCmdEventListener = null;
	private StatusEventListener mStatusEventListener = null;
	
	public CommandProcessor(){
		this(new BluetoothServer());
	}
	
	public CommandProcessor(BluetoothServer btServer)
	{
		mBTServer = btServer;
		try {
			mRobot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void startListening()
	{
		mBTServer.setEventListener(this);
		mBTServer.start();
	}
	

	@Override
	public void onStateChanged(int oldState, int newState) {
		if(mCmdEventListener != null)
		{
			mCmdEventListener.onStateChanged(oldState, newState);
		}
		
		if(mStatusEventListener != null)
		{
			mStatusEventListener.onStateChanged(oldState, newState);
		}
		
		if(newState == BluetoothServer.STATE_LISTEN){
//			lblStatus.setText("Waiting for Connection");
//			progressBar.setIndeterminate(true);
		}else if(newState == BluetoothServer.STATE_CONNECTED){
//			lblStatus.setText("Connected");
//			progressBar.setIndeterminate(false);
		}else if(newState == BluetoothServer.STATE_NONE){
//			lblStatus.setText("Server Down");
//			progressBar.setIndeterminate(false);
		}
	}
	
	
	@Override
	public void onError(int errorCode) {
		if(mCmdEventListener != null)
		{
			mCmdEventListener.onError(errorCode);
		}
		
		if(mStatusEventListener != null)
		{
			mStatusEventListener.onError(errorCode);
		}
		
	}

	@Override
	public void onBTMessageWritten(byte[] message, int length) {
		
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
			if(D) MyLog.log("Disconnect");
			mBTServer.disconnect();
			
			if(mCmdEventListener != null)
			{
				mCmdEventListener.onDisconnect();
			}
			
			if(mStatusEventListener != null)
			{
				mStatusEventListener.onDisconnect();
			}
			
			break;
		case GET_VERSION:
			if(D) MyLog.log("GetVersion");
			sendVersion();
			break;
		case MOVE_MOUSE:
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer, 2, 8);
			int dx = bb.getInt(0);
			int dy = bb.getInt(4);
			
			if(D) MyLog.log("Move Mouse: " + dx + "," + dy);
			mouseMoveEvent(dx, dy);
			break;

		case MOUSE_WHEEL_EVENT:
			bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer, 2, 4);
			int ticks = bb.getInt(0);
			
			if(D) MyLog.log("Move Wheel: " + ticks);
			mouseWheelEvent(ticks);
			break;
		case MOUSE_BUTTON_EVENT:
			mouseButtonEvent(commandBuffer[2], commandBuffer[3]);
			break;
		case KEY_EVENT:
			bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.put(commandBuffer, 3, 4);
			int keyCode = bb.getInt(0);
			keyEvent(keyCode, KeyEventType.get(commandBuffer[2]));
			break;
		}
	}

	private void keyEvent(int keyCode, KeyEventType keyEventType) {
		try{
			switch(keyEventType){
			case PRESS:
				keyPress(keyCode);
				break;
			case RELEASE:
				keyRelease(keyCode);
				break;
			}
		}catch(IllegalArgumentException ex)
		{
			ex.printStackTrace();
		}
		if(mCmdEventListener != null)
		{
			mCmdEventListener.onKeyEvent(keyCode, keyEventType);
		}
	}

	private void keyRelease(int keyCode) {
		switch(keyCode)
		{
		case KeyCode.VK_ASTERISK:
			mRobot.keyRelease(KeyCode.VK_8);
			break;
		case KeyCode.VK_PLUS:
			mRobot.keyRelease(KeyCode.VK_EQUALS);
			break;
		default:
			mRobot.keyRelease(keyCode);
			break;
			
		}
	}

	private void keyPress(int keyCode) {
		switch(keyCode)
		{
		case KeyCode.VK_ASTERISK:
			mRobot.keyPress(KeyCode.VK_SHIFT);
			mRobot.keyPress(KeyCode.VK_8);
			mRobot.keyRelease(KeyCode.VK_SHIFT);
			break;
		case KeyCode.VK_PLUS:
			mRobot.keyPress(KeyCode.VK_SHIFT);
			mRobot.keyPress(KeyCode.VK_EQUALS);
			mRobot.keyRelease(KeyCode.VK_SHIFT);
			break;
		default:
			mRobot.keyPress(keyCode);
			break;
			
		}
	}

	private void mouseWheelEvent(int ticks) {
		mRobot.mouseWheel(ticks);
		if(mCmdEventListener != null)
		{
			mCmdEventListener.onMouseWheelEvent(ticks);
		}
	}

	private void mouseMoveEvent(int dx, int dy) {
		Point currentMouse = MouseInfo.getPointerInfo().getLocation();
		mRobot.mouseMove(dx + currentMouse.x, dy + currentMouse.y);

		if(mCmdEventListener != null)
		{
			mCmdEventListener.onMouseMove(dx, dy);
		}
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
			if(D) MyLog.log("Mouse Press: " + MB.toString());
			mRobot.mousePress(robotButtonCode);
			break;
		case RELEASE:
			if(D) MyLog.log("Mouse Release: " + MB.toString());
			mRobot.mouseRelease(robotButtonCode);	
			break;
		}
		
		if(mCmdEventListener != null)
		{
			mCmdEventListener.onMouseButtonEvent(MB, MBE);
		}
	}
	
	private void sendVersion() {
		byte[] packet = {BTProtocol.PACKET_PREAMBLE, PacketID.RET_VERSION.getCode(), MajorVersion, MinorVersion, BTProtocol.CR, BTProtocol.LF};
		mBTServer.write(packet, packet.length);
	}

	public void setStatusListener(StatusEventListener listener) {
		mStatusEventListener = listener;
	}
	
	public void setEventListener(CommandEventListener listener) {
		mCmdEventListener = listener;
	}

	
	public static interface StatusEventListener {

		public void onStateChanged(int oldState, int newState);

		public void onError(int errorCode);

		public void onDisconnect();
		
	}
	
	public static interface CommandEventListener extends StatusEventListener{
		
		public void onMouseMove(int dx, int dy);
		
		public void onMouseWheelEvent(int ticks);
		
		public void onMouseButtonEvent(MouseButton MB, MouseButtonEvent MBE);
		
		public void onKeyEvent(int keyCode, KeyEventType keyEventType); 
	}

}

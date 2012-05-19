package com.cfms.android.mousedroid.test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.BTProtocol;
import com.cfms.android.mousedroid.KeyCode;
import com.cfms.android.mousedroid.activity.PreferencesActivity;

public class ProtocolTest extends
		ActivityInstrumentationTestCase2<PreferencesActivity> {

	PreferencesActivity mActivity;
	
	public ProtocolTest()
	{
		super(PreferencesActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }

    public void testPreamble(){
    	assertEquals(BTProtocol.PACKET_PREAMBLE, (byte)0xAA);
    }
    
    public void testDisconnect(){
    	byte[] packet  = BTProtocol.getDisconnectPacket();
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x01);
    	assertEquals(packet[2], (byte)0x0D);
    	assertEquals(packet[3], (byte)0x0A);
    }
    
    
    public void testGetVersion(){
    	byte[] packet  = BTProtocol.getGetVersionPacket();
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x02);
    	assertEquals(packet[2], (byte)0x0D);
    	assertEquals(packet[3], (byte)0x0A);
    }
    
    public void testReturnVersion(){
    	byte[] packet  = BTProtocol.getRetVersionPacket((byte)1, (byte)5);
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x03);
    	assertEquals(packet[2], (byte)0x01);
    	assertEquals(packet[3], (byte)0x05);
    	assertEquals(packet[4], (byte)0x0D);
    	assertEquals(packet[5], (byte)0x0A);
    }
    
    
    public void testMouseMove(){
    	byte[] packet  = BTProtocol.getMouseMovePacket((short)0x0f01, (short)0xfff0);
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x04);
    	assertEquals(packet[2], (byte)0x01);
    	assertEquals(packet[3], (byte)0x0f);
    	assertEquals(packet[4], (byte)0xf0);
    	assertEquals(packet[5], (byte)0xff);
    	assertEquals(packet[6], (byte)0x0D);
    	assertEquals(packet[7], (byte)0x0A);
    }

    public void testMouseButtonEvent(){
    	byte[] packet  = BTProtocol.getMouseButtonEventPacket(BTProtocol.MouseButton.BUTTON3, BTProtocol.MouseButtonEvent.PRESS);
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x05);
    	assertEquals(packet[2], (byte)0x03);
    	assertEquals(packet[3], (byte)0x01);
    	assertEquals(packet[4], (byte)0x0D);
    	assertEquals(packet[5], (byte)0x0A);
    	
    	byte[] packet1  = BTProtocol.getMouseButtonEventPacket(BTProtocol.MouseButton.BUTTON1, BTProtocol.MouseButtonEvent.RELEASE);
    	assertEquals(packet1[0], (byte)0xAA);
    	assertEquals(packet1[1], (byte)0x05);
    	assertEquals(packet1[2], (byte)0x01);
    	assertEquals(packet1[3], (byte)0x02);
    	assertEquals(packet1[4], (byte)0x0D);
    	assertEquals(packet1[5], (byte)0x0A);
    }
    

    public void testMouseWheelEvent(){
    	byte[] packet  = BTProtocol.getMouseWheelEventPacket((short) 0xf01);
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x06);
    	assertEquals(packet[2], (byte)0x01);
    	assertEquals(packet[3], (byte)0x0f);
    	assertEquals(packet[4], (byte)0x0D);
    	assertEquals(packet[5], (byte)0x0A);

    	byte[] packet1  = BTProtocol.getMouseWheelEventPacket((short) 0xfff1);
    	assertEquals(packet1[0], (byte)0xAA);
    	assertEquals(packet1[1], (byte)0x06);
    	assertEquals(packet1[2], (byte)0xf1);
    	assertEquals(packet1[3], (byte)0xff);
    	assertEquals(packet1[4], (byte)0x0D);
    	assertEquals(packet1[5], (byte)0x0A);
    }
    
    public void testKeyEventPress(){
    	byte[] packet  = BTProtocol.getKeyEventPacket(BTProtocol.KeyEventType.PRESS, KeyCode.VK_BACK_SPACE);
    	ByteBuffer bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN);
    	bb.putInt(KeyCode.VK_BACK_SPACE);
    	
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x07);
    	assertEquals(packet[2], (byte)0x01);
    	assertEquals(packet[3], bb.get(0));
    	assertEquals(packet[4], bb.get(1));
    	assertEquals(packet[5], bb.get(2));
    	assertEquals(packet[6], bb.get(3));
    	assertEquals(packet[7], (byte)0x0D);
    	assertEquals(packet[8], (byte)0x0A);

    }

    public void testKeyEventRelease(){
    	byte[] packet  = BTProtocol.getKeyEventPacket(BTProtocol.KeyEventType.RELEASE, KeyCode.VK_A);
    	ByteBuffer bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN);
    	bb.putInt(KeyCode.VK_A);
    	
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x07);
    	assertEquals(packet[2], (byte)0x02);
    	assertEquals(packet[3], bb.get(0));
    	assertEquals(packet[4], bb.get(1));
    	assertEquals(packet[5], bb.get(2));
    	assertEquals(packet[6], bb.get(3));
    	assertEquals(packet[7], (byte)0x0D);
    	assertEquals(packet[8], (byte)0x0A);

    }
    
    public void testKeyEventReleaseWithKeyCode(){
    	byte[] packet  = BTProtocol.getKeyEventPacket(BTProtocol.KeyEventType.RELEASE,  new KeyCode(KeyCode.VK_G));
    	ByteBuffer bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN);
    	bb.putInt(KeyCode.VK_G);
    	
    	assertEquals(packet[0], (byte)0xAA);
    	assertEquals(packet[1], (byte)0x07);
    	assertEquals(packet[2], (byte)0x02);
    	assertEquals(packet[3], bb.get(0));
    	assertEquals(packet[4], bb.get(1));
    	assertEquals(packet[5], bb.get(2));
    	assertEquals(packet[6], bb.get(3));
    	assertEquals(packet[7], (byte)0x0D);
    	assertEquals(packet[8], (byte)0x0A);

    }
}

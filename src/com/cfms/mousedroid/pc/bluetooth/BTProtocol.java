package com.cfms.mousedroid.pc.bluetooth;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class Protocol.
 */
public class BTProtocol {

	/** The Constant PACKET_PREAMBLE. */
	public final static byte PACKET_PREAMBLE = (byte) 0xAA;
	
	public static enum PacketID {
		DISCONNECT(1), GET_VERSION(2), RET_VERSION(3), MOVE_MOUSE(4), MOUSE_BUTTON_EVENT(5);
		
		private static final Map<Byte,PacketID> lookup 
        = new HashMap<Byte,PacketID>();

	   static {
	        for(PacketID mbs : EnumSet.allOf(PacketID.class))
	             lookup.put(mbs.getCode(), mbs);
	   }
		
		private byte code;
		
		private PacketID(int c){
			code = (byte) c;
		}
		
		public byte getCode(){
			return code;
		}
		
		public static PacketID get(byte code) { 
	          return lookup.get(code);
	    }
	}
	
	public static enum MouseButton {
		BUTTON1(1), BUTTON2(2), BUTTON3(3);
		
		private static final Map<Byte,MouseButton> lookup 
        = new HashMap<Byte,MouseButton>();

	   static {
	        for(MouseButton mbs : EnumSet.allOf(MouseButton.class))
	             lookup.put(mbs.getCode(), mbs);
	   }
		
		private byte code;
		
		private MouseButton(int c){
			code = (byte) c;
		}
		
		public byte getCode(){
			return code;
		}
		
		public static MouseButton get(byte code) { 
	          return lookup.get(code);
	    }
	}
	
	public static enum MouseButtonEvent {
		PRESS(1), RELEASE(2);
		

		private static final Map<Byte,MouseButtonEvent> lookup 
        = new HashMap<Byte,MouseButtonEvent>();

	   static {
	        for(MouseButtonEvent mbs : EnumSet.allOf(MouseButtonEvent.class))
	             lookup.put(mbs.getCode(), mbs);
	   }
		
		private byte code;
		
		private MouseButtonEvent(int c){
			code = (byte) c;
			
		}
		
		public byte getCode(){
			return code;
		}

		public static MouseButtonEvent get(byte code) { 
	          return lookup.get(code);
	    }
	}
	
	
	/** The Constant CR. */
	public final static byte CR = (byte) 0x0D;
	
	/** The Constant LF. */
	public final static byte LF = (byte) 0x0A;
	
	
}

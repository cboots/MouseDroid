package com.cfms.android.mousedroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class BluetoothProtocol.
 */
public class BTProtocol {

	/** The Constant PACKET_PREAMBLE. */
	public final static byte PACKET_PREAMBLE = (byte) 0xAA;

	/**
	 * The Enum PacketID.
	 */
	public static enum PacketID {
		DISCONNECT(1), GET_VERSION(2), RET_VERSION(3), MOVE_MOUSE(4), MOUSE_BUTTON_EVENT(
				5), MOUSE_WHEEL_EVENT(6), KEY_EVENT(7);

		private static final Map<Byte, PacketID> lookup = new HashMap<Byte, PacketID>();

		static {
			for (PacketID mbs : EnumSet.allOf(PacketID.class))
				lookup.put(mbs.getCode(), mbs);
		}

		private byte code;

		private PacketID(int c) {
			code = (byte) c;
		}

		public byte getCode() {
			return code;
		}

		public static PacketID get(byte code) {
			return lookup.get(code);
		}
	}

	/**
	 * The Enum MouseButton.
	 */
	public static enum MouseButton {
		BUTTON1(1), BUTTON2(2), BUTTON3(3);

		private static final Map<Byte, MouseButton> lookup = new HashMap<Byte, MouseButton>();

		static {
			for (MouseButton mbs : EnumSet.allOf(MouseButton.class))
				lookup.put(mbs.getCode(), mbs);
		}

		private byte code;

		private MouseButton(int c) {
			code = (byte) c;
		}

		public byte getCode() {
			return code;
		}

		public static MouseButton get(byte code) {
			return lookup.get(code);
		}
	}

	/**
	 * The Enum MouseButtonEvent.
	 */
	public static enum MouseButtonEvent {
		PRESS(1), RELEASE(2);

		private static final Map<Byte, MouseButtonEvent> lookup = new HashMap<Byte, MouseButtonEvent>();

		static {
			for (MouseButtonEvent mbs : EnumSet.allOf(MouseButtonEvent.class))
				lookup.put(mbs.getCode(), mbs);
		}

		private byte code;

		private MouseButtonEvent(int c) {
			code = (byte) c;

		}

		public byte getCode() {
			return code;
		}

		public static MouseButtonEvent get(byte code) {
			return lookup.get(code);
		}
	}

	/**
	 * The Enum KeyEventType.
	 */
	public static enum KeyEventType {
		PRESS(1), RELEASE(2);

		private static final Map<Byte, KeyEventType> lookup = new HashMap<Byte, KeyEventType>();

		static {
			for (KeyEventType mbs : EnumSet.allOf(KeyEventType.class))
				lookup.put(mbs.getCode(), mbs);
		}

		private byte code;

		private KeyEventType(int c) {
			code = (byte) c;

		}

		public byte getCode() {
			return code;
		}

		public static KeyEventType get(byte code) {
			return lookup.get(code);
		}
	}

	/** The Constant CR. */
	public final static byte CR = (byte) 0x0D;

	/** The Constant LF. */
	public final static byte LF = (byte) 0x0A;

	/**
	 * Gets the disconnect packet.
	 * 
	 * @return the disconnect packet
	 */
	public static byte[] getDisconnectPacket() {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.DISCONNECT.getCode(), CR, LF };
		return packet;
	}

	/**
	 * Gets the gets the version packet.
	 * 
	 * @return the gets the version packet
	 */
	public static byte[] getGetVersionPacket() {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.GET_VERSION.getCode(), CR, LF };
		return packet;
	}

	/**
	 * Gets the ret version packet.
	 * 
	 * @param majorVersion
	 *            the major version
	 * @param minorVersion
	 *            the minor version
	 * @return the ret version packet
	 */
	public static byte[] getRetVersionPacket(byte majorVersion,
			byte minorVersion) {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.RET_VERSION.getCode(), majorVersion, minorVersion, CR,
				LF };
		return packet;
	}

	/**
	 * Gets the mouse button event packet.
	 * 
	 * @param MB
	 *            the mB
	 * @param MBE
	 *            the mBE
	 * @return the mouse button event packet
	 */
	public static byte[] getMouseButtonEventPacket(MouseButton MB,
			MouseButtonEvent MBE) {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.MOUSE_BUTTON_EVENT.getCode(), MB.getCode(),
				MBE.getCode(), CR, LF };
		return packet;
	}

	/**
	 * Gets the mouse move packet.
	 * 
	 * @param dx
	 *            the dx
	 * @param dy
	 *            the dy
	 * @return the mouse move packet
	 */
	public static byte[] getMouseMovePacket(int dx, int dy) {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.MOVE_MOUSE.getCode(), 0, 0, 0, 0, 0, 0, 0, 0, CR, LF };
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(dx);
		bb.putInt(dy);

		packet[2] = bb.get(0);
		packet[3] = bb.get(1);
		packet[4] = bb.get(2);
		packet[5] = bb.get(3);
		packet[6] = bb.get(4);
		packet[7] = bb.get(5);
		packet[8] = bb.get(6);
		packet[9] = bb.get(7);
		
		return packet;
	}

	/**
	 * Gets the mouse wheel event packet.
	 * 
	 * @param ticks
	 *            the ticks
	 * @return the mouse wheel event packet
	 */
	public static byte[] getMouseWheelEventPacket(int ticks) {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.MOUSE_WHEEL_EVENT.getCode(), 0, 0, 0, 0, CR, LF };

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(ticks);

		packet[2] = bb.get(0);
		packet[3] = bb.get(1);
		packet[4] = bb.get(2);
		packet[5] = bb.get(3);
		
		return packet;
	}

	/**
	 * Gets the key event packet.
	 * 
	 * @param ket
	 *            the key event type
	 * @param keycode
	 *            the keycode
	 * @return the key event packet
	 */
	public static byte[] getKeyEventPacket(KeyEventType ket, KeyCode keycode) {
		return getKeyEventPacket(ket, keycode.getKeyCode());
	}

	/**
	 * Gets the key event packet.
	 * 
	 * @param ket
	 *            the key event type
	 * @param keycode
	 *            the keycode
	 * @return the key event packet
	 */
	public static byte[] getKeyEventPacket(KeyEventType ket, int keycode) {
		byte[] packet = new byte[] { PACKET_PREAMBLE,
				PacketID.KEY_EVENT.getCode(), ket.getCode(), 0, 0, 0, 0, CR, LF };

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(keycode);

		packet[3] = bb.get(0);
		packet[4] = bb.get(1);
		packet[5] = bb.get(2);
		packet[6] = bb.get(3);

		return packet;
	}
}
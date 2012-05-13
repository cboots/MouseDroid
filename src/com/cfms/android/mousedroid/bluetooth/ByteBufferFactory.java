package com.cfms.android.mousedroid.bluetooth;

import java.util.ArrayList;
import java.util.ListIterator;

import com.cfms.android.mousedroid.utils.DebugLog;

public class ByteBufferFactory {

	private static final String TAG = "ByteBufferFactory";

	public static final int BUFFER_SIZE = 1024;
	
	private static ArrayList<LockedBuffer> sBufferList = new ArrayList<LockedBuffer>(3);
	
	private static class LockedBuffer{
		public byte[] buffer = new byte[BUFFER_SIZE];
		public boolean locked = false;
	}
	
	public synchronized static byte[] getBuffer(){
		LockedBuffer buf = getNextFreeBuffer();
		buf.locked = true;
		return buf.buffer;
	}
	
	public synchronized static void releaseBuffer(byte[] array){
		LockedBuffer lockedBuf = findBuffer(array);
		if(lockedBuf == null){
			DebugLog.W(TAG, "releaseBuffer() array not found.");
			return;
		}
		
		lockedBuf.locked = false;
	}
	
	
	private synchronized static LockedBuffer findBuffer(byte[] array){
		ListIterator<LockedBuffer> buffers = sBufferList.listIterator();
		while(buffers.hasNext())
		{
			LockedBuffer buf = buffers.next();
			if(buf.buffer == array)
			{
				return buf;
			}
		}
		return null;
	}
	

	private synchronized static LockedBuffer getNextFreeBuffer(){
		ListIterator<LockedBuffer> buffers = sBufferList.listIterator();
		LockedBuffer buf;
		while(buffers.hasNext())
		{
			buf = buffers.next();
			if(!buf.locked)
			{
				return buf;
			}
		}
		buf = new LockedBuffer();
		sBufferList.add(buf);
		DebugLog.D(TAG, "New Buffer Created: count = "+ sBufferList.size());
		return buf;
	}
	
	
	public synchronized static void resetFactory(){
		sBufferList.clear();
	}
	
	
	public synchronized static int getBufferCount(){
		return sBufferList.size();
	}
}

package com.cfms.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.mousedroid.activity.SettingsActivity;
import com.cfms.mousedroid.bluetooth.ByteBufferFactory;

public class ByteBufferFactoryTest extends
		ActivityInstrumentationTestCase2<SettingsActivity> {

	SettingsActivity mActivity;
	
	public ByteBufferFactoryTest()
	{
		super(SettingsActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testByteBufferReset(){
    	ByteBufferFactory.resetFactory();
    	assertEquals(0, ByteBufferFactory.getBufferCount());
    }
    
    public void testGetBuffer(){
    	ByteBufferFactory.resetFactory();
    	byte[] buffer = ByteBufferFactory.getBuffer();
    	assertNotNull(buffer);
    	assertEquals(ByteBufferFactory.BUFFER_SIZE, buffer.length);
    	
    	ByteBufferFactory.resetFactory();
    }
    
    public void testBufferLock(){
    	ByteBufferFactory.resetFactory();
    	byte[] buffer = ByteBufferFactory.getBuffer();
    	assertNotNull(buffer);
    	assertEquals(1, ByteBufferFactory.getBufferCount());
    	
    	byte[] buffer1 = ByteBufferFactory.getBuffer();
    	assertNotNull(buffer1);
    	assertNotSame(buffer, buffer1);
    	assertEquals(2, ByteBufferFactory.getBufferCount());

    	ByteBufferFactory.resetFactory();
    }
    
    public void testBufferReleaseLock(){
    	ByteBufferFactory.resetFactory();
    	byte[] buffer = ByteBufferFactory.getBuffer();
    	byte[] buffer1 = ByteBufferFactory.getBuffer();
    	assertNotNull(buffer1);
    	assertEquals(2, ByteBufferFactory.getBufferCount());
    	ByteBufferFactory.releaseBuffer(buffer);
    	byte[] buffer2 = ByteBufferFactory.getBuffer();
    	assertEquals(2, ByteBufferFactory.getBufferCount());
    	assertEquals(buffer, buffer2);
    	ByteBufferFactory.resetFactory();
    	
    	
    }
}

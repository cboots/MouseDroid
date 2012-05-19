package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.cfms.android.mousedroid.activity.PreferencesActivity;
import com.cfms.android.mousedroid.bluetooth.BTProtocol;

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
    	int preamble = BTProtocol.PACKET_PREAMBLE & 0xFF;
    	assertEquals(preamble, 0xAA);
    }
    
    
}

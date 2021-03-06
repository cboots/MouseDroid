package com.cfms.mousedroid.test;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;

import com.cfms.mousedroid.R;
import com.cfms.mousedroid.activity.DeviceListActivity;
import com.cfms.mousedroid.activity.MouseDroidActivity;

public class MouseDroidActivityTest extends
		ActivityInstrumentationTestCase2<MouseDroidActivity> {

	MouseDroidActivity mActivity;
	
	public MouseDroidActivityTest()
	{
		super(MouseDroidActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	assertTrue("Bluetooth Must Be Enabled For These Tests To Run", BluetoothTestUtils.isBluetoothEnabled());
        super.setUp();
        mActivity = this.getActivity();
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    }
    
    public void testGetTag(){
    	assertEquals("MouseDroidActivity", mActivity.getTag());
    	
    }
    
    public void testMenuItemScan(){
    	ActivityMonitor am = getInstrumentation().addMonitor(DeviceListActivity.class.getName(), null, false);

    	// Click the menu option
    	getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
    	getInstrumentation().invokeMenuActionSync(mActivity, R.id.scan, 0);

    	Activity a = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
    	assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
    	a.finish();
    }
    
    @UiThreadTest
    public void testBluetoothBindingOnActivityResultBug(){
    	getInstrumentation().callActivityOnPause(mActivity);
    	getInstrumentation().callActivityOnStop(mActivity);
    	getInstrumentation().callActivityOnStart(mActivity);
    	getInstrumentation().callActivityOnResume(mActivity);
    	assertEquals(true, mActivity.isBoundToBTService());
    	assertNotNull(mActivity.getBTService());
    	
    	
    }
}

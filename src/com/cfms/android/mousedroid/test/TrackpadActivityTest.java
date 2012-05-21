package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.TrackpadActivity;
import com.cfms.android.mousedroid.activity.TrackpadFragment;

public class TrackpadActivityTest extends
		ActivityInstrumentationTestCase2<TrackpadActivity> {

	TrackpadActivity mActivity;
	TrackpadFragment mTrackpadFragment;
	public TrackpadActivityTest()
	{
		super(TrackpadActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
    	assertTrue("Bluetooth Must Be Enabled For These Tests To Run", BluetoothTestUtils.isBluetoothEnabled());
        super.setUp();
        mActivity = this.getActivity();
    	mTrackpadFragment = (TrackpadFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.trackpad_fragment);
        
    }
    
    public void testPreconditions() {
    	assertNotNull(mActivity);
    	assertNotNull(mTrackpadFragment);
    }
    

    public void testGetTag(){
    	assertEquals("TrackpadActivity", mActivity.getTag());
    	
    }

    
    
	boolean passPress = false;
	boolean passRelease= false;

    public void testButton1Click(){
    	passPress = false;
    	passRelease= false;
    	
    	mTrackpadFragment.setTrackpadListener(new TrackpadFragment.TrackpadListener() {
			
			@Override
			public void onScrollEvent(int ticks) {
				
			}
			
			@Override
			public void onMouseMove(int dx, int dy) {
				
			}
			
			@Override
			public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
				if(mb == MouseButton.BUTTON1){
					if(mbe == MouseButtonEvent.PRESS){
						passPress = true;
					}else if(mbe == MouseButtonEvent.RELEASE){
						if(passPress)
							passRelease = true;
					}
				}
			}
		});
    	
    	TouchUtils.clickView(this, mTrackpadFragment.getView().findViewById(R.id.trackpadButton1));
    	getInstrumentation().waitForIdleSync();
    	
    	assertEquals(true, passPress);
    	assertEquals(true, passRelease);
    }
    

    public void testButton2Click(){
    	passPress = false;
    	passRelease= false;
    	
    	mTrackpadFragment.setTrackpadListener(new TrackpadFragment.TrackpadListener() {
			
			@Override
			public void onScrollEvent(int ticks) {
				
			}
			
			@Override
			public void onMouseMove(int dx, int dy) {
				
			}
			
			@Override
			public void onMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb) {
				if(mb == MouseButton.BUTTON2){
					if(mbe == MouseButtonEvent.PRESS){
						passPress = true;
					}else if(mbe == MouseButtonEvent.RELEASE){
						if(passPress)
							passRelease = true;
					}
				}
			}
		});
    	
    	TouchUtils.clickView(this, mTrackpadFragment.getView().findViewById(R.id.trackpadButton2));
    	getInstrumentation().waitForIdleSync();
    	
    	assertEquals(true, passPress);
    	assertEquals(true, passRelease);
    }
}

package com.cfms.android.mousedroid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.R;
import com.cfms.android.mousedroid.activity.TrackpadActivity;
import com.cfms.android.mousedroid.activity.TrackpadFragment;
import com.cfms.android.mousedroid.view.ScrollSlider;
import com.cfms.android.mousedroid.view.Touchpad;

public class TrackpadActivityTest extends
		ActivityInstrumentationTestCase2<TrackpadActivity> {

	TrackpadActivity mActivity;
	TrackpadFragment mTrackpadFragment;

	public TrackpadActivityTest() {
		super(TrackpadActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
//		assertTrue("Bluetooth Must Be Enabled For These Tests To Run",
//				BluetoothTestUtils.isBluetoothEnabled());
		super.setUp();
		mActivity = this.getActivity();
		mTrackpadFragment = (TrackpadFragment) mActivity
				.getSupportFragmentManager().findFragmentById(
						R.id.trackpad_fragment);

	}

	public void testPreconditions() {
		assertNotNull(mActivity);
		assertNotNull(mTrackpadFragment);
	}

	public void testGetTag() {
		assertEquals("TrackpadActivity", mActivity.getTag());

	}

	boolean passPress = false;
	boolean passRelease = false;

	public void testButton1Click() {
		passPress = false;
		passRelease = false;

		mTrackpadFragment
				.setTrackpadListener(new TrackpadFragment.TrackpadListener() {

					@Override
					public void onScrollEvent(int ticks) {

					}

					@Override
					public void onMouseMove(int dx, int dy) {

					}

					@Override
					public void onMouseButtonEvent(MouseButtonEvent mbe,
							MouseButton mb) {
						if (mb == MouseButton.BUTTON1) {
							if (mbe == MouseButtonEvent.PRESS) {
								passPress = true;
							} else if (mbe == MouseButtonEvent.RELEASE) {
								if (passPress)
									passRelease = true;
							}
						}
					}
				});

		TouchUtils.clickView(this,
				mTrackpadFragment.getView().findViewById(R.id.trackpad_button1));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}

	public void testButton3Click() {
		passPress = false;
		passRelease = false;

		mTrackpadFragment
				.setTrackpadListener(new TrackpadFragment.TrackpadListener() {

					@Override
					public void onScrollEvent(int ticks) {

					}

					@Override
					public void onMouseMove(int dx, int dy) {

					}

					@Override
					public void onMouseButtonEvent(MouseButtonEvent mbe,
							MouseButton mb) {
						if (mb == MouseButton.BUTTON3) {
							if (mbe == MouseButtonEvent.PRESS) {
								passPress = true;
							} else if (mbe == MouseButtonEvent.RELEASE) {
								if (passPress)
									passRelease = true;
							}
						}
					}
				});

		TouchUtils.clickView(this,
				mTrackpadFragment.getView().findViewById(R.id.trackpad_button3));
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passPress);
		assertEquals(true, passRelease);
	}

	boolean passMove = false;

	public void testMouseMove() {
		passMove = false;

		mTrackpadFragment
				.setTrackpadListener(new TrackpadFragment.TrackpadListener() {

					@Override
					public void onScrollEvent(int ticks) {

					}

					@Override
					public void onMouseMove(int dx, int dy) {
						if(!passMove){
						assertEquals(10, dx);
						assertEquals(20, dy);
						}
						passMove = true;
					}

					@Override
					public void onMouseButtonEvent(MouseButtonEvent mbe,
							MouseButton mb) {

					}
				});
		getInstrumentation().setInTouchMode(true);
		getInstrumentation().waitForIdleSync();
		Touchpad tp = (Touchpad) mTrackpadFragment.getView().findViewById(
				R.id.touchpad);
		
		tp.selfTest();
		
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passMove);
	}


	public void testScrollSlider() {
		passMove = false;

		mTrackpadFragment
				.setTrackpadListener(new TrackpadFragment.TrackpadListener() {

					@Override
					public void onScrollEvent(int ticks) {
						if(!passMove){
							assertEquals(20, ticks);
						}
						passMove = true;
					}

					@Override
					public void onMouseMove(int dx, int dy) {
						
					}

					@Override
					public void onMouseButtonEvent(MouseButtonEvent mbe,
							MouseButton mb) {

					}
				});
		
		getInstrumentation().setInTouchMode(true);
		getInstrumentation().waitForIdleSync();
		ScrollSlider slider = (ScrollSlider) mTrackpadFragment.getView().findViewById(
				R.id.scroll_slider);
		
		slider.selfTest();
		
		getInstrumentation().waitForIdleSync();

		assertEquals(true, passMove);
	}

	
}

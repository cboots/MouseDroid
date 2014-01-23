package com.cfms.android.mousedroid.test;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.view.MotionEvent;
import android.widget.Button;

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

	@UiThreadTest
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
		Touchpad tp = (Touchpad) mTrackpadFragment.getView().findViewById(
				R.id.touchpad);
		
		sendTouchpadMove(tp, 0);
		

		assertEquals(true, passMove);
	}


	@UiThreadTest
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
		ScrollSlider slider = (ScrollSlider) mTrackpadFragment.getView().findViewById(
				R.id.scroll_slider);
		
		slider.selfTest();
		
		assertEquals(true, passMove);
	}

	@UiThreadTest
	public void testMultitouchClickAndDrag(){
		passMove = false;
		passPress = false;
		passRelease = false;

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
						if (mb == MouseButton.BUTTON1) {
							if (mbe == MouseButtonEvent.PRESS) {
								passPress = true;
							} else if (mbe == MouseButtonEvent.RELEASE) {
								if (passPress && passMove)
									passRelease = true;
							}
						}
					}
				});
		
		getInstrumentation().setInTouchMode(true);
		Touchpad tp = (Touchpad) mTrackpadFragment.getView().findViewById(
				R.id.touchpad);
		Button button1 = (Button) mTrackpadFragment.getView().findViewById(R.id.trackpad_button1);
		

		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN , button1.getLeft(), button1.getTop(), 0);
		
		//Press
		button1.dispatchTouchEvent(event);
		
		//Drag
		sendTouchpadMove(tp, 0);

		downTime = SystemClock.uptimeMillis();
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_UP, button1.getLeft(), button1.getTop(), 0);
		
		//Release
		button1.dispatchTouchEvent(event);
		

		assertEquals(true, passPress);
		assertEquals(true, passMove);
		assertEquals(true, passRelease);
	}
	
	private void sendTouchpadMove(Touchpad tp, int pointerNumber)
	{
		int actionDown= MotionEvent.ACTION_DOWN | (pointerNumber << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
		int actionMove = MotionEvent.ACTION_MOVE | (pointerNumber << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
		int actionUp = MotionEvent.ACTION_UP | (pointerNumber << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
	
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				actionDown, tp.getLeft(), tp.getTop(), 0);
		
		assertTrue(tp.dispatchTouchEvent(event));
		
		downTime = SystemClock.uptimeMillis();
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime,
				actionMove, tp.getLeft() + 10 , tp.getTop() + 20, 0);

		assertTrue(tp.dispatchTouchEvent(event));

		downTime = SystemClock.uptimeMillis();
		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime,
				actionUp, tp.getLeft() + 10 , tp.getTop() + 20, 0);

		assertTrue(tp.dispatchTouchEvent(event));
	}
}

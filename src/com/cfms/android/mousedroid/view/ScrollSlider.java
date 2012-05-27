package com.cfms.android.mousedroid.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cfms.android.mousedroid.utils.DebugLog;

/**
 * The Class Touchpad.
 */
public class ScrollSlider extends View {

	private static final String TAG = "Touchpad";

	static private boolean D = true;
	
	/** The m listener. */
	private ScrollListener mListener;

	/**
	 * Instantiates a new scroll slider.
	 *
	 * @param context the context
	 */
	public ScrollSlider(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new scroll slider.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public ScrollSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new scroll slider.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public ScrollSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	/**
	 * Self test function.  
	 * Generates a drag event from the upper left corner down and to the right dy = 20, dx = 10
	 */
	public void selfTest() {
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, getLeft(), getTop(), 0);
		
		this.onTouchEvent(event);
		
		event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_MOVE, getLeft() + 10 , getTop() + 20, 0);
		
		this.onTouchEvent(event);

		event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_UP, getLeft() + 10 , getTop() + 20, 0);
		
		this.onTouchEvent(event);
	}
	
	
	
	/** The dragging. */
	boolean dragging = false;
	
	/** The last x. */
	int lastX = -1;
	
	/** The last y. */
	int lastY = -1;
	
	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(D) DebugLog.D(TAG, "onTouchEvent: " + event.toString());
        int eventaction = event.getAction();   
      
        int X = (int)event.getX();
        int Y = (int)event.getY(); 
        
        switch (eventaction ) {
        case MotionEvent.ACTION_DOWN: 
			dragging = true;
			lastX = X;
			lastY = Y;
			break;
        case MotionEvent.ACTION_MOVE:
        	onScroll(Y - lastY);
			lastX = X;
			lastY = Y;
			break;
        case MotionEvent.ACTION_UP:
			dragging = false;
			onScroll(Y - lastY);
        }
		
		return true;
	}
	

	/**
	 * On move.
	 *
	 * @param dy
	 */
	private void onScroll(int dy) {
		if(mListener != null){
			if(D) DebugLog.D(TAG, "onScroll: " + dy);
			mListener.onScrollEvent(dy);
			
		}
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new  listener
	 */
	public void setScrollListener(ScrollListener listener) {
		mListener = listener;
	}

	/**
	 * The listener interface for receiving touchpad events.
	 * The class that is interested in processing a touchpad
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addTouchpadListener<code> method. When
	 * the touchpad event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see TouchpadEvent
	 */
	public interface ScrollListener {
		
		/**
		 * On touchpad scroll event.
		 *
		 * @param ticks the ticks
		 */
		public void onScrollEvent(int ticks);

	}
	

}

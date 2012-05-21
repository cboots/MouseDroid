package com.cfms.android.mousedroid.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.utils.DebugLog;

public class Touchpad extends View {

	private TouchpadListener mListener;

	public Touchpad(Context context) {
		super(context);
	}

	public Touchpad(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Touchpad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

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
	
	
	
	boolean dragging = false;
	int lastX = -1;
	int lastY = -1;
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		DebugLog.D("Touchpad", "onTouchEvent: " + event.toString());
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
        	onMove(X - lastX, Y - lastY);
			lastX = X;
			lastY = Y;
			break;
        case MotionEvent.ACTION_UP:
			dragging = false;
			onMove(X - lastX, Y - lastY);
        }
		
		return true;
	}
	

	private void onMove(int dx, int dy) {
		if(mListener != null){
			mListener.onTouchpadMouseMove(dx, dy);
			
		}
	}

	public void setTouchpadListener(TouchpadListener listener) {
		mListener = listener;
	}

	public interface TouchpadListener {
		public void onTouchpadMouseMove(int dx, int dy);

		public void onTouchpadMouseButtonEvent(MouseButtonEvent mbe, MouseButton mb);

		public void onTouchpadScrollEvent(int ticks);

	}
	

}

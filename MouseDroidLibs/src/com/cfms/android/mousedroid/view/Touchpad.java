package com.cfms.android.mousedroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.cfms.android.mousedroid.BTProtocol.MouseButton;
import com.cfms.android.mousedroid.BTProtocol.MouseButtonEvent;
import com.cfms.android.mousedroid.utils.DebugLog;

/**
 * The Class Touchpad.
 */
public class Touchpad extends RelativeLayout {

	private static final String TAG = "Touchpad";

	private static final int INVALID_POINTER_ID = -1;

	static private boolean D = true;

	/** The m listener. */
	private TouchpadListener mListener;

	/**
	 * Instantiates a new touchpad.
	 * 
	 * @param context
	 *            the context
	 */
	public Touchpad(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new touchpad.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public Touchpad(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new touchpad.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public Touchpad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/** The dragging. */
	int mDraggingPointerId = INVALID_POINTER_ID;

	/** The last x. */
	int lastX = -1;

	/** The last y. */
	int lastY = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int eventaction = event.getActionMasked();
		
		switch (eventaction) {

		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();

			lastX = (int) x;
			lastY = (int) y;
			mDraggingPointerId = event.getPointerId(0);
			return true;

		case MotionEvent.ACTION_POINTER_DOWN:
			if (mDraggingPointerId == INVALID_POINTER_ID) {
				final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				if(pointerIndex == -1 || pointerIndex >= event.getPointerCount())
					break;
				final int pointerId = event.getPointerId(pointerIndex);
				x = event.getX(pointerIndex);
				y = event.getY(pointerIndex);

				lastX = (int) x;
				lastY = (int) y;
				mDraggingPointerId = pointerId;
			}
			return true;

		case MotionEvent.ACTION_MOVE:
			if (mDraggingPointerId != INVALID_POINTER_ID) {
				final int pointerIndex = event
						.findPointerIndex(mDraggingPointerId);
				if (pointerIndex == -1  || pointerIndex >= event.getPointerCount())
					break;
				int X = (int) event.getX(pointerIndex);
				int Y = (int) event.getY(pointerIndex);

				onMove(X - lastX, Y - lastY);
				lastX = X;
				lastY = Y;
			}
			return true;

		case MotionEvent.ACTION_UP:
			mDraggingPointerId = INVALID_POINTER_ID;
			return true;
		case MotionEvent.ACTION_POINTER_UP: {
			// Still have
			final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			if (pointerIndex == -1 || pointerIndex >= event.getPointerCount())
				break;
			
			final int pointerId = event.getPointerId(pointerIndex);

			if (pointerId == mDraggingPointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				lastX = (int) event.getX(newPointerIndex);
				lastY = (int) event.getY(newPointerIndex);
				mDraggingPointerId = event.getPointerId(newPointerIndex);
			}
			return true;
		}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * On move.
	 * 
	 * @param dx
	 *            the dx
	 * @param dy
	 *            the dy
	 */
	private void onMove(int dx, int dy) {
		if (mListener != null) {
			if (D)
				DebugLog.D(TAG, "onMove" + dx + "," + dy);
			mListener.onTouchpadMouseMove(dx, dy);

		}
	}

	/**
	 * Sets the touchpad listener.
	 * 
	 * @param listener
	 *            the new touchpad listener
	 */
	public void setTouchpadListener(TouchpadListener listener) {
		mListener = listener;
	}

	/**
	 * The listener interface for receiving touchpad events. The class that is
	 * interested in processing a touchpad event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addTouchpadListener<code> method. When
	 * the touchpad event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see TouchpadEvent
	 */
	public interface TouchpadListener {

		/**
		 * On touchpad mouse move.
		 * 
		 * @param dx
		 *            the dx
		 * @param dy
		 *            the dy
		 */
		public void onTouchpadMouseMove(int dx, int dy);

		/**
		 * On touchpad mouse button event.
		 * 
		 * @param mbe
		 *            the mbe
		 * @param mb
		 *            the mb
		 */
		public void onTouchpadMouseButtonEvent(MouseButtonEvent mbe,
				MouseButton mb);

		/**
		 * On touchpad scroll event.
		 * 
		 * @param ticks
		 *            the ticks
		 */
		public void onTouchpadScrollEvent(int ticks);

	}

}

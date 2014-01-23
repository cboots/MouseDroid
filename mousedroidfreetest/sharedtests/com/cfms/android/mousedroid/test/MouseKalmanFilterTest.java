package com.cfms.android.mousedroid.test;

import junit.framework.TestCase;
import Jama.Matrix;

import com.cfms.android.mousedroid.sensor.MouseKalmanFilter;

public class MouseKalmanFilterTest extends TestCase {

	public void testDiag() {
		Matrix mat = MouseKalmanFilter.diag(0.1, 0.2, 0.3, 0.4);
		assertEquals(4, mat.getRowDimension());
		assertEquals(4, mat.getColumnDimension());

		assertEquals(0.1, mat.get(0,0));
		assertEquals(0.2, mat.get(1,1));
		assertEquals(0.3, mat.get(2,2));
		assertEquals(0.4, mat.get(3,3));
		
		assertEquals(0.0, mat.get(0,1));
		assertEquals(0.0, mat.get(0,2));
		assertEquals(0.0, mat.get(0,3));
		
		assertEquals(0.0, mat.get(1,0));
		assertEquals(0.0, mat.get(1,2));
		assertEquals(0.0, mat.get(1,3));

		assertEquals(0.0, mat.get(2,0));
		assertEquals(0.0, mat.get(2,1));
		assertEquals(0.0, mat.get(2,3));
		
		assertEquals(0.0, mat.get(3,0));
		assertEquals(0.0, mat.get(3,1));
		assertEquals(0.0, mat.get(3,2));
	}

}

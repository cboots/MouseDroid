package com.cfms.android.mousedroid.sensor;

import org.ejml.simple.SimpleMatrix;

public class MouseKalmanFilter {

	private static final double GRAVITY = 9.81;
	private static final double GAMMA_A = 0.975;
	private static final double GAMMA_V = 0.9;
	private static final double[] DEFAULT_R = {0.2, 0.2, 0.2};
	private static final double[] DEFAULT_Q = {0.1, 0.1, 0.1, 0.2, 0.2, 0.2};
	
	SimpleMatrix mX;
	SimpleMatrix mP;
	SimpleMatrix mR;
	SimpleMatrix mQ;
	SimpleMatrix mF;
	SimpleMatrix mH;
	
	public MouseKalmanFilter()
	{
		resetFilter();
		mH = new SimpleMatrix(3,6);
		mH.set(0,0,1);
		mH.set(1,1,1);
		mH.set(2,2,1);
		
		mF = SimpleMatrix.diag(GAMMA_A, GAMMA_A, GAMMA_A, GAMMA_V, GAMMA_V, GAMMA_V);
		mR = SimpleMatrix.diag(DEFAULT_R);
		mQ = SimpleMatrix.diag(DEFAULT_Q);
		
	}
	
	public void resetFilter() {
		mX = new SimpleMatrix(6,1);
		mP = SimpleMatrix.diag(0.1, 0.1, 0.1, 0.25, 0.25, 0.25);
	}

	public void setState(SimpleMatrix X, SimpleMatrix P)
	{
		mX = X;
		mP = P;
	}
	
	public void update(double dt, double[] unbiased_accel)
	{
		//Update F with dt
		mF.set(4,0,dt);
		mF.set(5,1,dt);
		mF.set(6,2,dt);
		
		//Prediction step
		//x_hat = F*x_prev;
		//P = F*P*F'+Q;
		mX = mF.mult(mX);
		mP = ((mF.mult(mP)).mult(mF.transpose())).plus(mQ);
		
		
		//Measurement update
		SimpleMatrix z = new SimpleMatrix(3,1);
		z.set(0, 0, unbiased_accel[0]);
		z.set(1, 0, unbiased_accel[1]);
		z.set(2, 0, unbiased_accel[2]-GRAVITY);
		
		SimpleMatrix yhat = z.minus(mH.mult(mX));
		SimpleMatrix S = mH.mult(mP).mult(mH.transpose()).plus(mR);
		SimpleMatrix K = mP.mult(mH.transpose()).mult(S.invert());
		mX = mX.plus(K.mult(yhat));
		mP = SimpleMatrix.identity(6).minus(K.mult(mH)).mult(mP);
	}
}

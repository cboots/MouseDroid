package com.cfms.android.mousedroid.sensor;

import Jama.Matrix;

public class MouseKalmanFilter {

	private static final double DEFAULT_GAMMA_A = 0.975;
	
	private static final double DEFAULT_GAMMA_V = 0.9;
	private static final double[] DEFAULT_R = {0.2, 0.2, 0.2};
	private static final double[] DEFAULT_Q = {0.1, 0.1, 0.1, 0.2, 0.2, 0.2};
	
	Matrix mX;
	Matrix mP;
	Matrix mR;
	Matrix mQ;
	Matrix mF;
	Matrix mH;
	
	public static Matrix diag(double... elements)
	{
		Matrix diagMat = new Matrix(elements.length, elements.length);
		for(int i = 0; i < elements.length; i++)
		{
			diagMat.set(i, i, elements[i]); 
		}
		return diagMat;		
	}
	
	public MouseKalmanFilter()
	{
		resetFilter();
		mH = new Matrix(3,6);
		mH.set(0,0,1);
		mH.set(1,1,1);
		mH.set(2,2,1);
		
		mF = diag(DEFAULT_GAMMA_A, DEFAULT_GAMMA_A, DEFAULT_GAMMA_A, DEFAULT_GAMMA_V, DEFAULT_GAMMA_V, DEFAULT_GAMMA_V);
		mR = diag(DEFAULT_R);
		mQ = diag(DEFAULT_Q);
		
	}
	
	public void resetFilter() {
		mX = new Matrix(6,1);
		mP = diag(0.1, 0.1, 0.1, 0.25, 0.25, 0.25);
	}

	public void setState(Matrix X, Matrix P)
	{
		mX = X;
		mP = P;
	}
	
	public Matrix getState()
	{
		return mX;
	}
	
	public void setMeasurementNoiseModel(Matrix R){
		mR = R.copy();
	}
	
	public void setProcessNoiseModel(Matrix Q){
		mQ = Q.copy();
	}
	
	public void update(double dt, double[] linear_accel)
	{
		//Update F with dt
		mF.set(3,0,dt);
		mF.set(4,1,dt);
		mF.set(5,2,dt);
		
		//Prediction step
		//x_hat = F*x_prev;
		//P = F*P*F'+Q;
		mX = mF.times(mX);
		mP = ((mF.times(mP)).times(mF.transpose())).plus(mQ);
		
		
		//Measurement update
		Matrix z = new Matrix(3,1);
		z.set(0, 0, linear_accel[0]);
		z.set(1, 0, linear_accel[1]);
		z.set(2, 0, linear_accel[2]);
		
		Matrix yhat = z.minus(mH.times(mX));
		Matrix S = mH.times(mP).times(mH.transpose()).plus(mR);
		Matrix K = mP.times(mH.transpose()).times(S.inverse());
		mX = mX.plus(K.times(yhat));
		
		mP = Matrix.identity(6,6).minus(K.times(mH)).times(mP);
	}
}

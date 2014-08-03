/*
 * Copyright (C) 2014  Saul Rodriguez

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */



package com.rodriguez.saul.flightgearpfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MFD777View extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	Context mcontext;
		
	
	public static final int BASIC = 0;
	public static final int B777 = 1;
	public static final int B787 = 2;
	public static final int B747 = 3;
	public static final int A330 = 4;	
	public static final int A380 = 5;
		
	Plane plane;
	int planeType;
	
	int mwidth;
	int mheight;	
	int centerx;
	int centery;
	float scaleFactor;
			
	
	public MFD777View(Context context, AttributeSet attrs) {
		super(context, attrs);
				
		
		mcontext = context;
		// TODO Auto-generated constructor stub
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
				
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		mwidth = this.getWidth();
		mheight = this.getHeight();
		
		//Log.d("Saul", String.format("width = %d", mwidth));
		//Log.d("Saul", String.format("height = %d", mheight));

		centerx = mwidth/2;
		centery = mheight/2;
		
		plane.centerx = centerx;
		plane.centery = centery;
		//Calculate the scale factor
		int maskHeight = plane.mask.getHeight();
		
		//scaleFactor = (float) 0.5; //Only for test and new features
		scaleFactor = (float)(mheight)/(float)maskHeight;
				
		plane.scaleFactor = scaleFactor;
		//Draw the view
		draw();
		
				
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public void draw() {
		
		long time, time2;
		//time = System.currentTimeMillis();
			
		//Lock the canvas and start drawing
        Canvas canvas = surfaceHolder.lockCanvas();
        
        plane.draw(canvas);
        
        surfaceHolder.unlockCanvasAndPost(canvas);
        
        //time2 = System.currentTimeMillis();
  		//Log.d("777View", String.format("%d", (time2-time)));
       
	}

	
	
	//Setters
	void setPlane(int planetype) 
	{
		planeType = planetype;
		//Log.d("SELECTED PLANE",String.format("%d",planeType));
		
		switch (planeType) {
			case BASIC: 
						plane = new PlaneFree(mcontext);
						break;
			case B777:  
						plane = new Plane777(mcontext);
						break;
			case B787:  
						plane = new Plane787(mcontext);
						break;
			case B747:  
						plane = new Plane747(mcontext);
						break;
						
			case A330:  
						plane = new PlaneA330(mcontext);
						break;
						
			case A380:  
						plane = new PlaneA380(mcontext);
						break;
			default:	
						plane = new PlaneFree(mcontext);
						break;
						
			
		}
	}
	
	void SetSpeed(float newSpeed) 
	{
		plane.speed = newSpeed;
	}
	
	void setAltitude(float newAltitude)
	{
		plane.altitude = newAltitude;
	}
	
	void setVerticalSpeed(float newverticalSpeed) 
	{
		plane.verticalSpeed = newverticalSpeed;		
	}
	
	void setRoll(float newRoll)
	{
		plane.horizontRollAngle = -newRoll;	//the roll angle of FGFS is anti-clockwise whereas the rotation of matrix in android is clockwise
	}
	
	void setPitch(float newPitch)
	{
		plane.horizontPitchAngle = newPitch;
	}
	
	void setHeading(float newHeading)
	{
		plane.heading = newHeading;		
	}
	
	void setNAV1Quality(float newQuality)
	{
		plane.locnavQuality = newQuality;
	}
	
	void setNAV1loc(boolean newNavLoc)
	{
		plane.locnav = newNavLoc;		
	}
	
	void setNAV1deflection(float newDeflection)
	{
		plane.headingLoc = newDeflection;
	}
	
	void setGSActive(boolean newGSactive)
	{
		plane.gsActive = newGSactive;		
	}
	
	void setGSInRange(boolean newgsInRange)
	{
		plane.gsInRange = newgsInRange;		
	}
	
	void setGSdeflection(float newgsDeflection)
	{
		plane.gsDeflection = newgsDeflection;
	}
	
	void setRadioaltimeter(int newRadioaltimeter)
	{
		plane.radioaltimeter = newRadioaltimeter;		
	}
	
	void setMach(float newMach)
	{
		plane.mach = newMach;
	}
	
	void setStallSpeed(float newStallSpeed)
	{
		plane.stallspeed = newStallSpeed; 
	}
	
	void setStallWarning(boolean newStallWarning)
	{
		plane.stallwarning = newStallWarning;		
	}
	
	void setFlaps(float newFlaps)
	{
		plane.flaps = newFlaps;
	}
	
	void setMaxSpeed(float newMaxSpeed)
	{
		plane.maxspeed = newMaxSpeed;
	}
	
	void setApIndicator(String newApIndicator)
	{
		plane.apIndicator = newApIndicator;
		//Log.d("Saul",apIndicator);
	}
	
	void setPitchMode(String newPitchMode)
	{
		plane.pitchMode = newPitchMode;
		//Log.d("Saul",pitchMode);
	}
	
	void setRollMode(String newRoleMode)
	{
		plane.rollMode = newRoleMode;
	}
	
	void setSpeedMode(String newSpeedMode)
	{
		plane.speedMode = newSpeedMode;
	}
	
	void setAPaltitude(float newApAltitude)
	{
		plane.apaltitude = newApAltitude;
	}
	
	void setAPactualaltitude(float newAPactualaltitude)
	{
		plane.apactualaltitude = newAPactualaltitude;
	}
	
	void setAPspeed(float newAPspeed)
	{
		plane.apspeed = newAPspeed;
	}
	
	void setAPheading(int newAPheading)
	{
		plane.apheading = newAPheading;		
	}
	
	void setDMEinrange(boolean inrange)
	{
		plane.dmeinrange = inrange;
	}
	
	void setDME(float newdme)
	{
		plane.dme = newdme;
	}
	
	
	
}

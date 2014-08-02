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
	
	public static final int B777 = 1;
	public static final int B787 = 2;
	public static final int A330 = 3;
	public static final int FREE = 4;
	public static final int B747 = 5;
	public static final int A380 = 6;
	
	//int selectedPlane = B777;
	
	//Plane777 plane;
	Plane787 plane;
	//PlaneA330 plane;
	//PlaneA380 plane;
	//PlaneFree plane;
	//Plane747 plane;
	
	int mwidth;
	int mheight;	
	int centerx;
	int centery;
	float scaleFactor;
		
	float horizontRollAngle;
	float horizontPitchAngle;	
	float verticalSpeed;
	float speed;
	float altitude;
	float heading;
	float locnavQuality; //the localizer for ILS in range when > 0.9
	boolean locnav;     //Is the localizer activated?
	float headingLoc;   //Localizer deflection (normalized) -1.0 to 1.0
	boolean gsInRange; //Is the glideslope in range?
	boolean gsActive; //Is glidescope activated?
	float gsDeflection; //Deflection of the glideslope normalizer (-1.0 to 1.0)
	int radioaltimeter; //radioaltimeter feet;
	float mach; 		//mach speed
	float stallspeed;  //min speed
	boolean stallwarning; //turn on when stallspeed is valid
	float flaps; //flap status
	float maxspeed; //maximum speed
	String apIndicator; //Autopilot indicator
	String pitchMode; // status of AP pitch conf.
	String rollMode; //  status of AP roll conf.
	String speedMode; // Status of AP speed conf.
	float apaltitude; // AP set altitude
	float apactualaltitude; //AP actual/current altitude
	float apspeed; // AP speed
	int apheading; //AP heading bug
	
	Bitmap mask = null;
	Bitmap horizont = null;
	Bitmap vs = null;
	Bitmap marks = null;	
	Bitmap compass = null;
	Bitmap bug = null;
	Bitmap bugfilled = null;
	Bitmap crophorizont = null;	
	Bitmap apalt = null;
	Bitmap apspeedind = null;
	Bitmap aphead = null;
	
	Matrix maskMatrix;
	Matrix horizontMatrix;
	Matrix vsMatrix;
	Matrix marksMatrix;
	Matrix compassMatrix;
	Matrix bugLocMatrix;
	Matrix apaltMatrix;
	Matrix apspeedindMatrix;
	Matrix apheadMatrix;
	
	public MFD777View(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//plane = new Plane747(context);
		//plane = new Plane777(context);
		plane = new Plane787(context);
		//plane = new PlaneFree(context);
		//plane = new PlaneA330(context);
		//plane = new PlaneA380(context);
		
		
		// TODO Auto-generated constructor stub
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		
		/*
		maskMatrix = new Matrix();
		horizontMatrix = new Matrix();
		vsMatrix = new Matrix();
		marksMatrix = new Matrix();
		compassMatrix = new Matrix();
		bugLocMatrix = new Matrix();
		apaltMatrix = new Matrix();
		apspeedindMatrix = new Matrix();
		apheadMatrix = new Matrix();
		
		scaleFactor = (float)1.0;
		mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
		horizont = BitmapFactory.decodeResource(getResources(), R.drawable.horizon);
		vs = BitmapFactory.decodeResource(getResources(), R.drawable.vs);
		marks = BitmapFactory.decodeResource(getResources(), R.drawable.speed_altitude);
		compass = BitmapFactory.decodeResource(getResources(), R.drawable.heading);
		bug = BitmapFactory.decodeResource(getResources(), R.drawable.bug);
		bugfilled = BitmapFactory.decodeResource(getResources(), R.drawable.bugfilled);
		apalt = BitmapFactory.decodeResource(getResources(), R.drawable.apalt);
		apspeedind = BitmapFactory.decodeResource(getResources(), R.drawable.apspeed);
		aphead = BitmapFactory.decodeResource(getResources(), R.drawable.aphdg);
		
		
		horizontRollAngle = 45;
		horizontPitchAngle = 0;
		speed = 200;
		altitude = 12400;
		verticalSpeed = 500;
		heading = 10;
		locnavQuality = (float)0.95;
		locnav = false;
		headingLoc = (float)-0.97;
		gsInRange = false; //Is the glideslope in range?
		gsActive = false; //Is glidescope activated?
		gsDeflection = (float)-0.90; //Deflection of the glideslope normalizer (-1.0 to 1.0)
		radioaltimeter = 10;
		mach = 0;
		stallspeed = 185;
		stallwarning = false;
		flaps = 0;
		maxspeed = 130;
		apIndicator = new String("A/P");		
		pitchMode = new String("V/S");
		rollMode = new String("HDG HOLD");
		speedMode = new String("A/T");
		apaltitude = 12500;
		apactualaltitude = 12800;
		apspeed = 200;
		apheading = 20; 
		*/
		
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
		/*
		//Prepare the mask
		maskMatrix.reset();
		maskMatrix.postTranslate(-mask.getWidth()/2, -mask.getHeight()/2 );
		maskMatrix.postScale(scaleFactor, scaleFactor);
        maskMatrix.postTranslate(centerx, centery);
       
               
        //Prepare vertical speed and gray background
        vsMatrix.reset();
        vsMatrix.postTranslate(-vs.getWidth()/2, -vs.getHeight()/2);
        vsMatrix.postScale(scaleFactor, scaleFactor);
        vsMatrix.postTranslate(centerx+(int)(44*scaleFactor), centery-(int)(10*scaleFactor));
        
        //Prepare marks, speed & altitude boxes
        marksMatrix.reset();
        marksMatrix.postTranslate(-marks.getWidth()/2, -marks.getHeight()/2);
        marksMatrix.postScale(scaleFactor, scaleFactor);
        marksMatrix.postTranslate(centerx+(int)(13*scaleFactor), centery + (int)(120*scaleFactor));
        
        //Prepare compass
        compassMatrix.reset();
        compassMatrix.postTranslate(-compass.getWidth()/2, -compass.getHeight()/2 );
        compassMatrix.postRotate(-heading);
        compassMatrix.postScale(scaleFactor, scaleFactor);
        compassMatrix.postTranslate(centerx, centery + (int)(548*scaleFactor));
        
       //Prepare artificial horizont Alternative Code
        int aux_x = horizont.getWidth()/2;
        int aux_y = horizont.getHeight()/2 -(int)calculatePitchshift();
        
        crophorizont = Bitmap.createBitmap(horizont,aux_x - 300, aux_y - 300, 600, 600);
                
        horizontMatrix.reset();
        horizontMatrix.postTranslate(-crophorizont.getWidth()/2, -crophorizont.getHeight()/2);
        horizontMatrix.postRotate(horizontRollAngle);
        horizontMatrix.postScale(scaleFactor, scaleFactor);
        horizontMatrix.postTranslate(centerx, centery);
                           
        */
		//Lock the canvas and start drawin
        Canvas canvas = surfaceHolder.lockCanvas();
        /*
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //paint.setDither(true);
        canvas.drawColor(Color.BLACK);       
        //canvas.drawBitmap(horizont, horizontMatrix, paint);
        canvas.drawBitmap(crophorizont, horizontMatrix, paint);
        canvas.drawBitmap(vs, vsMatrix, null);
        drawAltitudeLine(canvas,paint);
        drawSpeedLine(canvas,paint);
        
        canvas.drawBitmap(mask,maskMatrix,paint);
        canvas.drawBitmap(compass, compassMatrix, paint);
        
        drawAPsettings(canvas,paint);
        canvas.drawBitmap(marks, marksMatrix, paint);
        
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(35*scaleFactor));
        
        //draw speed kts, match, altitude, and radioaltimeter
        canvas.drawText(String.format("%d", (int)speed), (centerx - (int)(350*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        canvas.drawText(String.format("%4.3f",mach), (centerx - (int)(340*scaleFactor)), centery + (int)(280*scaleFactor), paint);
        canvas.drawText(String.format("%d", (int)altitude), (centerx + (int)(365*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        drawRadioAltimeter(canvas, paint);
                
        drawVerticalSpeed(canvas,paint);
        drawLocalizer(canvas,paint);
        drawGlideslope(canvas,paint);
        drawMinSpeed(canvas,paint);
        drawMaxSpeed(canvas,paint);
        drawAPStatus(canvas,paint);
        */
        plane.draw(canvas);
        
        surfaceHolder.unlockCanvasAndPost(canvas);
        
        //time2 = System.currentTimeMillis();
  		//Log.d("777View", String.format("%d", (time2-time)));
       
	}

	
	
	//Setters
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

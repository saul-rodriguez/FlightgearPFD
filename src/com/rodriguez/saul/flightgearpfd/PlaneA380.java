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
import android.util.AttributeSet;

public class PlaneA380 extends Plane {
	/*
	public int centerx;
	public int centery;
	public float scaleFactor;
	
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
	boolean dmeinrange; // is DME in range ?
	float dme; // dme distance
	
	public Bitmap mask = null; */
	
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
	
	Context mContext;
	
	public PlaneA380(Context context) {
		
		//Set the context from tje Activity
		mContext = context;
		
		//Initialize the matrices
		maskMatrix = new Matrix();
		horizontMatrix = new Matrix();
		vsMatrix = new Matrix();
		marksMatrix = new Matrix();
		compassMatrix = new Matrix();
		bugLocMatrix = new Matrix();
		apaltMatrix = new Matrix();
		apspeedindMatrix = new Matrix();
		apheadMatrix = new Matrix();
		
		//Load the bitmaps		
		mask = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.maska330);
		horizont = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.horizon);
		vs = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vsa330);
		marks = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.speed_altitudea330);
		compass = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heading);
		bug = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bug);
		bugfilled = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bugfilled);
		apalt = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.apalta330);
		apspeedind = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.apspeeda330);
		aphead = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.aphdga330);
		
		
		//Initialize all the parameters
		scaleFactor = (float)1.0;
		
		horizontRollAngle = 45;
		horizontPitchAngle = 0;
		speed = 200;
		altitude = 12400;
		verticalSpeed = 500;
		heading = 170;
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
		apspeed = 210;
		apheading = 160; 
		dmeinrange = false;
		dme = 0;
		
	}
	

	public void draw(Canvas canvas) {
		
		long time, time2;
		time = System.currentTimeMillis();
		
		//Prepare the mask
		maskMatrix.reset();
		maskMatrix.postTranslate(-mask.getWidth()/2, -mask.getHeight()/2 );
		maskMatrix.postScale(scaleFactor, scaleFactor);
        maskMatrix.postTranslate(centerx, centery);
       
               
        //Prepare vertical speed and gray background
        vsMatrix.reset();
        vsMatrix.postTranslate(-vs.getWidth()/2, -vs.getHeight()/2);
        vsMatrix.postScale(scaleFactor, scaleFactor);
        vsMatrix.postTranslate(centerx+(int)(44*scaleFactor), centery + (int)(50*scaleFactor));
        
        //Prepare marks, speed & altitude boxes
        marksMatrix.reset();
        marksMatrix.postTranslate(-marks.getWidth()/2, -marks.getHeight()/2);
        marksMatrix.postScale(scaleFactor, scaleFactor);
        marksMatrix.postTranslate(centerx+(int)(27*scaleFactor), centery + (int)(139*scaleFactor));
        
        //Prepare compass
        //compassMatrix.reset();
        //compassMatrix.postTranslate(-compass.getWidth()/2, -compass.getHeight()/2 );
        //compassMatrix.postRotate(-heading);
        //compassMatrix.postScale(scaleFactor, scaleFactor);
        //compassMatrix.postTranslate(centerx, centery + (int)(548*scaleFactor));
        
       //Prepare artificial horizont Alternative Code
        int aux_x = horizont.getWidth()/2;
        int aux_y = horizont.getHeight()/2 -(int)calculatePitchshift();
        
        crophorizont = Bitmap.createBitmap(horizont,aux_x - 300, aux_y - 300, 600, 600);
                
        horizontMatrix.reset();
        horizontMatrix.postTranslate(-crophorizont.getWidth()/2, -crophorizont.getHeight()/2);
        horizontMatrix.postRotate(horizontRollAngle);
        horizontMatrix.postScale(scaleFactor, scaleFactor);
        horizontMatrix.postTranslate(centerx, centery);
                           
        
		//Lock the canvas and start drawin
        //Canvas canvas = surfaceHolder.lockCanvas();
        
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
        drawHeadingLines(canvas, paint);
        
        canvas.drawBitmap(mask,maskMatrix,paint);
       // canvas.drawBitmap(compass, compassMatrix, paint);
        
        //drawAPsettings(canvas,paint);
        canvas.drawBitmap(marks, marksMatrix, paint);
        drawAPsettings(canvas,paint);
        
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(35*scaleFactor));
        
        //draw speed kts, match, altitude, and radioaltimeter
        //canvas.drawText(String.format("%d", (int)speed), (centerx - (int)(350*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        canvas.drawText(String.format("%4.3f",mach), (centerx - (int)(340*scaleFactor)), centery + (int)(280*scaleFactor), paint);
        paint.setColor(Color.GREEN);
        canvas.drawText(String.format("%d", (int)altitude), (centerx + (int)(360*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        drawRadioAltimeter(canvas, paint);
                
        drawVerticalSpeed(canvas,paint);
        drawLocalizer(canvas,paint);
        drawGlideslope(canvas,paint);
        drawMinSpeed(canvas,paint);
        drawMaxSpeed(canvas,paint);
        drawAPStatus(canvas,paint);
        drawDME(canvas,paint);
	}
	
	double calculatePitchshift()
	{
		double aux;

		final double pitchDistance = 75.0; //vertical distance in number of pixels per 10 degree pitch
		
		aux = horizontPitchAngle*(pitchDistance/10.0);
		return aux;
	}
	
	void drawAltitudeLine(Canvas canvas, Paint paint) {
		int offsetx;
		//Paint paint;
		final float verticalPitchScale = (float) (100./200); // 100pixels/200 feet = 0.5 pixels/ft
		offsetx = centerx + (int)(273*scaleFactor); //Border
		
		//paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(30*scaleFactor));
		paint.setStrokeWidth((int)(2*scaleFactor));
		
		//Calculate first horizontal line
		float frest = altitude - 200*(int)((altitude)/200); //float residual
		
		int rest = (int)(altitude)%200; //residual to 1st lower mark in ft
		int altitude1 = (int)(altitude) - rest; // altitude in feet 1st lower mark
		int offset1 = (int) (frest*(verticalPitchScale*scaleFactor));
		int y1 = centery + offset1;
		
		int y[] = new int[6];
		int alt[] = new int[6];
		
		//Marks with text
		for (int i = 0; i<6; i++){
			y[i] = y1 - (int)((300 - i*100)*scaleFactor);
			alt[i] = altitude1 + (600 - i*200);
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);
			canvas.drawText(String.format("%d", alt[i]), offsetx + (int)(30*scaleFactor), y[i]+(int)(10*scaleFactor), paint);
			
		}
		
		//Marks without text
		for (int i = 0; i<6; i++){
			y[i] = y1 - (int)((350 - i*100)*scaleFactor); 
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);			
		}
	}
	
	void drawSpeedLine(Canvas canvas, Paint paint) {
		int offsetx;
		//Paint paint;
		final float verticalPitchScale = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
		offsetx = centerx - (int)(300*scaleFactor); //Border
		
		//paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(30*scaleFactor);
		paint.setStrokeWidth((int)(2*scaleFactor));
		
					
		//Calculate first horizontal line
		float frest = speed - 20*(int)((speed)/20); //float residual
		int rest = (int)(speed)%20; //residual to 1st lower mark in kts
		int speed1 = (int)(speed) - rest; // speed in kts of 1st lower mark
		int offset1 = (int) (frest*verticalPitchScale*scaleFactor);
		int y1 = centery + offset1;
		
		int y[] = new int[8];
		int alt[] = new int[8];
		
		//Marks with text
		for (int i = 0; i<8; i++){
			y[i] = y1 - (int)(((75*4) - i*75)*scaleFactor);
			alt[i] = speed1 + (4*20) - i*20;
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);
			canvas.drawText(String.format("%d", alt[i]), offsetx - (int)(60*scaleFactor), y[i]+(int)(10*scaleFactor), paint);
			
		}
		
		//Marks without text
		for (int i = 0; i<8; i++){
			y[i] = y1 - (int)(((75*4+37) - i*75)*scaleFactor); 
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);			
		}		
				
	}
	
	void drawHeadingLines(Canvas canvas, Paint paint)
	{
		int offsety = centery + (int)(290*scaleFactor);		
		final float headingPitchScale = (float) (100./10); // 100pixels/10 degees = 10 pixels/degree
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(30*scaleFactor);
		paint.setStrokeWidth((int)(2*scaleFactor));
		paint.setTextAlign(Align.CENTER);
		
		//Calculate first verticall line
		float frest = heading - 10*(int)((heading)/10); //float residual
		int rest = (int)(heading)%10; //residual to 1st lower mark in kts
		int heading1 = (int)(heading) - rest; // speed in kts of 1st lower mark
		int offset1 = (int) (frest*headingPitchScale*scaleFactor);
		int x1 = centerx - offset1;
				
		int x[] = new int[7];
		int alt[] = new int[7];
				
				//Marks with text
		for (int i = 0; i<7; i++){
			x[i] = x1 - (int)(((100*3) - i*100)*scaleFactor);
			alt[i] = heading1 - (3*10) + i*10;
			
			if (alt[i] < 0) {
				alt[i] += 360;
			} else if (alt[i] >= 360) {
				alt[i] -= 360;
			}
			//canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);
			//canvas.drawText(String.format("%d", alt[i]), offsetx - (int)(60*scaleFactor), y[i]+(int)(10*scaleFactor), paint);
			canvas.drawLine(x[i], offsety, x[i], offsety+(int)(30*scaleFactor), paint);
			canvas.drawText(String.format("%d", alt[i]/10), x[i], offsety + (int)(60*scaleFactor),paint);
			
		}
				
			//Marks without text
		for (int i = 0; i<7; i++){
			x[i] = x1 - (int)(((100*3+50) - i*100)*scaleFactor); 
			canvas.drawLine(x[i], offsety, x[i], offsety+(int)(15*scaleFactor), paint);		
		}	
				
	}
	
	void drawMinSpeed(Canvas canvas, Paint paint)
	{
		int offsetx = centerx - (int)(267*scaleFactor); //Border
		final float verticalPitchScale = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
		// Draw stall speed
				
			
		if (radioaltimeter < 40) {
			return;
		} else {
			stallspeed = 140;
		}
		
		
		//Stall speed to low to be shown
		if ((speed - stallspeed) > 50) 
			return;
		
		//Stall speed to high to be shown
		if ((stallspeed - speed) > 70) 
			stallspeed = speed + 70;
			
		
		float ystall = (speed + 10 - stallspeed)*verticalPitchScale*scaleFactor; 
		
		//Paint paint;
		//paint = new Paint();
		//paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStrokeWidth((10*scaleFactor));
		paint.setPathEffect(new DashPathEffect(new float[] {(8*scaleFactor),(15*scaleFactor)}, 0));
		canvas.drawLine(offsetx, centery +  (int)ystall, offsetx, centery + (int)(240*scaleFactor) , paint);
		
		paint.setPathEffect(null);		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((2*scaleFactor));
		paint.setColor(Color.YELLOW);
		
		int x1 = offsetx - (int)(5*scaleFactor);
		int y1 = centery + (int)ystall;
		int x2 = offsetx + (int)(15*scaleFactor);
		int y2 = centery + (int)(ystall - 37.5*scaleFactor);
		
		canvas.drawLine(x1, y1, x2, y1, paint);
		canvas.drawLine(x2, y1, x2, y2, paint);
		canvas.drawLine(x2, y2, x1, y2, paint);
		
	}
	
	void drawMaxSpeed(Canvas canvas, Paint paint)
	{
		int offsetx = centerx - (int)(267*scaleFactor); //Border
		final float verticalPitchScale = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
		
		
		 
			maxspeed = 330; //No flaps
			if (flaps >= 0.28)
				maxspeed = 230;
			if (flaps >= 0.596)
				maxspeed = 215;
			if (flaps >= 0.745)
				maxspeed = 200;
			if (flaps >= 1)
				maxspeed = 185;	
		
		//Max speed too high to be shown
		if ((maxspeed - speed) > 55) 
			return;
				
		//Max speed too low to be shown
		if ((speed - maxspeed) > 60) 
			maxspeed = speed - 60;
		
		float ystall = (maxspeed - speed + 10)*verticalPitchScale*scaleFactor;
		
		//Paint paint;
		//paint = new Paint();
		//paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStrokeWidth((10*scaleFactor));
		paint.setPathEffect(new DashPathEffect(new float[] {(8*scaleFactor),(15*scaleFactor)}, 0));
		canvas.drawLine(offsetx, centery -  (int)ystall, offsetx, centery - (int)(255*scaleFactor) , paint);
		
		paint.setPathEffect(null);		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((2*scaleFactor));
		paint.setColor(Color.YELLOW);
		
		int x1 = offsetx - (int)(5*scaleFactor);
		int y1 = centery - (int)ystall;
		int x2 = offsetx + (int)(15*scaleFactor);
		int y2 = centery - (int)(ystall - 37.5*scaleFactor);
		
		canvas.drawLine(x1, y1, x2, y1, paint);
		canvas.drawLine(x2, y1, x2, y2, paint);
		canvas.drawLine(x2, y2, x1, y2, paint);
		
	}
	
	
	void drawVerticalSpeed(Canvas canvas, Paint paint)
	{
		int x1,x2,y1,y2;
		//Paint paint;
		
		//float scaleVs = (float) (180./3000.); //180 pixels / 3000 fpm (Full-scale +-180 pixels)
		final float scaleVs = (float) (120./2000.); //180 pixels / 3000 fpm (Full-scale +-180 pixels)
			
		final float scaleVsTop = (float) (60./4000.); //The scale at top/bottom is 60 pixels / 4000 fpm
		
		//x1 = centerx + 475; //right border
		x1 = centerx + (int)(525*scaleFactor); //right point
		x2 = centerx + (int)(435*scaleFactor); //
		y1 = centery;
		y2 = centery;
		
		//Calculation of line deviation
		if (verticalSpeed <= 2000 && verticalSpeed >= -2000) {
			y2 = centery - (int)(verticalSpeed*scaleVs*scaleFactor);
			y1 = centery - (int)(0.5*verticalSpeed*scaleVs*scaleFactor);
		} else if (verticalSpeed > 2000 && verticalSpeed <= 6000) {
			float aux = verticalSpeed - 2000;
			y2 = centery -(int)(2000.*scaleVs*scaleFactor) -(int)(aux*scaleVsTop*scaleFactor); 
			y1 = centery -(int)(0.5*2000.*scaleVs*scaleFactor) -(int)(0.5*aux*scaleVsTop*scaleFactor);
		} else if (verticalSpeed > 6000) {
			float aux = 6000 - 2000; //Limit vertical speed indicator to fullscale at 6000 fpm
			y2 = centery -(int)(2000.*scaleVs*scaleFactor) -(int)(aux*scaleVsTop*scaleFactor);
			y1 = centery -(int)(0.5*2000.*scaleVs*scaleFactor) -(int)(0.5*aux*scaleVsTop*scaleFactor);
		} else if(verticalSpeed < -2000 && verticalSpeed >= -6000) {
			float aux = verticalSpeed + 2000;
			y2 = centery -(int)(-2000.*scaleVs*scaleFactor) -(int)(aux*scaleVsTop*scaleFactor);
			y1 = centery -(int)(-0.5*2000.*scaleVs*scaleFactor) -(int)(0.5*aux*scaleVsTop*scaleFactor);
		} else if(verticalSpeed < -6000) {
			float aux = -6000 + 2000; //Limit vertical speed indicator to fullscale at -6000 fpm
			y2 = centery -(int)(-2000.*scaleVs*scaleFactor) -(int)(aux*scaleVsTop*scaleFactor);
			y1 = centery -(int)(-0.5*2000.*scaleVs*scaleFactor) -(int)(0.5*aux*scaleVsTop*scaleFactor);
		}
				
		
		//paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(24*scaleFactor);	
		paint.setStrokeWidth((int)(3*scaleFactor));
		
		//Draw VS indicator line
		paint.setColor(Color.GREEN);
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		//Dra2 Textbox and text
		int x3 = centerx + (int)(460*scaleFactor);
		int squaresize = (int)(15*scaleFactor);
				
		int auxvs = (int)((Math.abs(verticalSpeed)/1000)*10);
		String vspeed;
		if (auxvs < 10) {
			vspeed = String.format("0%d", auxvs);
		} else {
			vspeed = String.format("%d", auxvs);
		}
			
		if (verticalSpeed > 200){	
			y2 -= (int)(7*scaleFactor);
			paint.setColor(Color.BLACK);
			canvas.drawRect(x3 - squaresize, y2 - 2*squaresize + (int)(6*scaleFactor), x3 + 2*squaresize, y2+(int)(6*scaleFactor), paint);
			paint.setColor(Color.GREEN);
			canvas.drawText(vspeed, x3, y2, paint);		
		} else if (verticalSpeed < -200) {
			y2 += (int)(26*scaleFactor);
			paint.setColor(Color.BLACK);
			canvas.drawRect(x3 - squaresize, y2 - 2*squaresize + (int)(6*scaleFactor), x3 + 2*squaresize, y2+(int)(6*scaleFactor), paint);
			paint.setColor(Color.GREEN);
			canvas.drawText(vspeed, x3+(int)(5*scaleFactor), y2, paint);			
		}
		
	}
	
	void drawLocalizer(Canvas canvas, Paint paint)
	{
		final int lineLenght = 20; 	
		
		//Check if the localizer is selected and in range
		if ((locnavQuality < 0.93) || locnav == false) { 
			return;
		}
		
		//Paint paint;
		//paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
        paint.setFilterBitmap(true);	    		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);			
		paint.setStrokeWidth((int)(3*scaleFactor));
		
		//Draw center line line
		canvas.drawLine(centerx, centery + (int)(250.*scaleFactor), centerx, centery + (int)(275.*scaleFactor), paint);
				
		int y2 = centery + (int)(260*scaleFactor);  
		
		if (headingLoc >= -0.33 && headingLoc <= 0.33) {
			
			final int side = 10;
			
			canvas.drawRect(centerx - (int)((-113-side)*scaleFactor), y2 - (int)(side*scaleFactor), centerx - (int)((-113+side)*scaleFactor), y2 + (int)(side*scaleFactor), paint);
			canvas.drawRect(centerx + (int)((-113-side)*scaleFactor), y2 - (int)(side*scaleFactor), centerx + (int)((-113+side)*scaleFactor), y2 + (int)(side*scaleFactor), paint);
			
			int deflectionx = centerx + (int)(3.*226.*headingLoc*scaleFactor);
			
			bugLocMatrix.reset();
			bugLocMatrix.postTranslate(-bug.getWidth()/2, -bug.getHeight()/2);
			bugLocMatrix.postScale(scaleFactor, scaleFactor);
			bugLocMatrix.postTranslate(deflectionx, y2);
			
			canvas.drawBitmap(bugfilled, bugLocMatrix, paint);
			
			
		} else if (headingLoc <= -0.95 || headingLoc >= 0.95) { // Draw the empty bug close to the borders
			
			canvas.drawCircle(centerx - (int)(75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx - (int)(2*75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx + (int)(75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx + (int)(2*75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			
			//Calculation of the deflection
			int deflectionx = centerx + (int)(226.*headingLoc*scaleFactor);
			
			bugLocMatrix.reset();
			bugLocMatrix.postTranslate(-bug.getWidth()/2, -bug.getHeight()/2);
			bugLocMatrix.postScale(scaleFactor, scaleFactor);
			bugLocMatrix.postTranslate(deflectionx, y2);
			
			canvas.drawBitmap(bug, bugLocMatrix, paint);
			
			
		} else if (headingLoc > -0.95 && headingLoc < 0.95) { // Draw normal bug
			canvas.drawCircle(centerx - (int)(75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx - (int)(2*75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx + (int)(75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			canvas.drawCircle(centerx + (int)(2*75.33*scaleFactor), y2 , (int)(10*scaleFactor), paint);
			
			//Calculation of the deflection
			int deflectionx = centerx + (int)(226.*headingLoc*scaleFactor);
			
			bugLocMatrix.reset();
			bugLocMatrix.postTranslate(-bug.getWidth()/2, -bug.getHeight()/2);
			bugLocMatrix.postScale(scaleFactor, scaleFactor);
			bugLocMatrix.postTranslate(deflectionx, y2);
			
			canvas.drawBitmap(bugfilled, bugLocMatrix, paint);
			
		}		
	}
	
	void drawGlideslope(Canvas canvas, Paint paint)
	{
		//Check if the glidescope is activated and in range
		if (gsActive == false || gsInRange == false)
		{
			return;			
		}
				
		int offsetx;
		
		offsetx = centerx + (int)(235*scaleFactor); //Border
		
		//Paint paint;
		//paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
        paint.setFilterBitmap(true);	    		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);			
		paint.setStrokeWidth((int)(3*scaleFactor));
		
		//Draw center line line
		//canvas.drawLine(centerx, centery + (int)(230.*scaleFactor), centerx, centery + (int)(255.*scaleFactor), paint);
		canvas.drawLine(offsetx, centery, offsetx + (int)(25.*scaleFactor), centery, paint);
		
		canvas.drawCircle(offsetx+(int)(12*scaleFactor), centery - (int)(75.33*scaleFactor), (int)(10*scaleFactor), paint);
		canvas.drawCircle(offsetx+(int)(12*scaleFactor), centery - (int)(2*75.33*scaleFactor), (int)(10*scaleFactor), paint);
		canvas.drawCircle(offsetx+(int)(12*scaleFactor), centery + (int)(75.33*scaleFactor), (int)(10*scaleFactor), paint);
		canvas.drawCircle(offsetx+(int)(12*scaleFactor), centery + (int)(2*75.33*scaleFactor), (int)(10*scaleFactor), paint);
		
		
		int deflectiony = centery - (int)(226.*gsDeflection*scaleFactor);
		
		bugLocMatrix.reset();
		bugLocMatrix.postTranslate(-bug.getWidth()/2, -bug.getHeight()/2);
		bugLocMatrix.postRotate((float)90);
		bugLocMatrix.postScale(scaleFactor, scaleFactor);
		bugLocMatrix.postTranslate(offsetx + (int)(12*scaleFactor), deflectiony);
		
		if (gsDeflection <= -0.95 || gsDeflection >= 0.95) { // Draw the empty bug close to the borders
			
			canvas.drawBitmap(bug, bugLocMatrix, paint);
			
		} else if (gsDeflection > -0.95 && gsDeflection < 0.95) { // Draw normal bug
			
			canvas.drawBitmap(bugfilled, bugLocMatrix, paint);
		}		
				
	}
	
	void drawRadioAltimeter(Canvas canvas, Paint paint)
	{
		if (radioaltimeter > 2400) {
			return;
		}
		//Paint paint = new Paint();
		//paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.rgb(168, 88, 15));
		canvas.drawRect(centerx - (int)(50*scaleFactor), centery + (int)(180*scaleFactor) , centerx + (int)(50*scaleFactor), centery + (int)(220*scaleFactor), paint);
		
		paint.setColor(Color.YELLOW);
		paint.setTextSize(35*scaleFactor);	
		paint.setStrokeWidth((int)(3*scaleFactor));
		paint.setTextAlign(Align.CENTER);
		
		int offset = 0;
		radioaltimeter /= 10;
		radioaltimeter *=10;
		
		if (radioaltimeter < 1000)
			offset += (int)(10*scaleFactor);
		
		if (radioaltimeter < 100)
			offset += (int)(10*scaleFactor);
		
		if (radioaltimeter < 10)
			offset += (int)(10*scaleFactor);
		
		canvas.drawText(String.format("%d",radioaltimeter), centerx, centery + (int)(215*scaleFactor), paint);
	}
	
	void drawAPStatus(Canvas canvas, Paint paint)
	{
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth((int)(1*scaleFactor));
		paint.setPathEffect(null);
		paint.setColor(Color.GREEN);
		paint.setTextAlign(Paint.Align.CENTER);
		
		//AP status
		paint.setTextSize(35*scaleFactor);
		canvas.drawText(apIndicator, centerx, centery - (int)(240*scaleFactor), paint);
		
		
		paint.setTextSize(30*scaleFactor);
				
		//Pitch Mode	
		if (pitchMode.equals("vertical-speed-hold")) {
			pitchMode = "V/S";
		} else if (pitchMode.equals("altitude-hold")) {
			pitchMode = "ALT";
		} else if (pitchMode.equals("gs1-hold")) {
			pitchMode = "G/S";
		}
		
		canvas.drawText(pitchMode, centerx , centery - (int)(320*scaleFactor), paint);
		
		//Roll Mode
		if (rollMode.equals("wing-leveler")) {
			rollMode = "HDG HOLD";
		} else if (rollMode.equals("nav1-hold")) {
			rollMode = "LOC";
		} else if (rollMode.equals("dg-heading-hold")) {
			rollMode = "HDG";
		}
		canvas.drawText(rollMode, centerx + (int)(165*scaleFactor) , centery - (int)(320*scaleFactor), paint);
		
		//Speed Mode
		if (speedMode.equals("speed-with-throttle")) {
			speedMode = "A/THR";
		} else if (speedMode.equals("speed-with-pitch-trim")) {
			speedMode = "THR";
		}
		paint.setColor(Color.WHITE);
		canvas.drawText(speedMode, centerx + (int)(350*scaleFactor), centery - (int)(320*scaleFactor), paint);
	}
	
	void drawAPsettings(Canvas canvas, Paint paint)
	{
		int offsetx, offsety;
		final float verticalPitchScale = (float) (100./200); // 100pixels/200 feet = 0.5 pixels/ft
		final float verticalPitchScaleSpeed = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
		
		//Selected AP Altitude 
		offsetx = centerx + (int)(330*scaleFactor); //Border
		paint.setColor(Color.CYAN);
		paint.setTextSize(35*scaleFactor);
		paint.setTextAlign(Align.CENTER);
				
		canvas.drawText(String.format("%d",(int)apaltitude), offsetx, centery - (int)(270*scaleFactor), paint);
		
		//Actual AP Altitude
		if ((apactualaltitude - altitude) > 500) { //Out of scale
			apactualaltitude = altitude + 500;
		}
		
		if ((altitude - apactualaltitude > 500)) {
			apactualaltitude = altitude - 500;
		}
		
		
		offsetx = centerx + (int)(280*scaleFactor); //Border
		offsety = centery - (int)((apactualaltitude - altitude)*verticalPitchScale*scaleFactor);   
				
		apaltMatrix.reset();
		apaltMatrix.postTranslate(-apalt.getWidth()/2, -apalt.getHeight()/2);
		apaltMatrix.postScale(scaleFactor, scaleFactor);
		apaltMatrix.postTranslate(offsetx, offsety);
		
		canvas.drawBitmap(apalt, apaltMatrix, paint);
		
		//AP Speed Text
		offsetx = centerx - (int)(330*scaleFactor); //Border
		canvas.drawText(String.format("%d",(int)apspeed), offsetx, centery - (int)(270*scaleFactor), paint);
		
		//AP speed indicator
		if ((apspeed - speed) > 70) {
			apspeed = speed + 70;
		}
		
		if ((speed - apspeed) > 65) {
			apspeed = speed - 65;
		}
		
		offsetx = centerx - (int)(260*scaleFactor); //Border
		offsety = centery - (int)((apspeed - speed)*verticalPitchScaleSpeed*scaleFactor);
		
		apspeedindMatrix.reset();
		apspeedindMatrix.postTranslate(-apspeedind.getWidth()/2, -apspeedind.getHeight()/2);
		apspeedindMatrix.postScale(scaleFactor, scaleFactor);
		apspeedindMatrix.postTranslate(offsetx, offsety);
		
		canvas.drawBitmap(apspeedind, apspeedindMatrix, paint);
		
		//AP Heading Text
		//offsetx = centerx - (int)(50*scaleFactor);
		//offsety = centery + (int)(360*scaleFactor);
		//paint.setTextSize(30*scaleFactor);
		
		//if (apheading < 10) {
		//	canvas.drawText(String.format("00%dH",apheading), offsetx, offsety, paint);
		//} else if (apheading < 100) {
		//	canvas.drawText(String.format("0%dH",apheading), offsetx, offsety, paint);
		//} else {
		//	canvas.drawText(String.format("%dH",apheading), offsetx, offsety, paint);
		//}
				
		//AP Heading indicator
		//float rotation = heading - (float)(apheading);
		
		apheadMatrix.reset();
		apheadMatrix.postTranslate(-aphead.getWidth()/2, -aphead.getHeight()/2);
		apheadMatrix.postScale(scaleFactor, scaleFactor);
		apheadMatrix.postTranslate(0, -(280*scaleFactor));
		
		
		offsety = centery + (int)(280*scaleFactor);		
		final float headingPitchScale = (float) (100./10); // 100pixels/10 degees = 10 pixels/degree
		float aux_apheading, aux_heading;
		
		//manipulation of angles within the visible region
		aux_apheading = apheading;
		aux_heading = heading;
				
		if( (heading < 30) && (aux_apheading > 330)) {
			aux_apheading -= 360;
			//heading = -360 + heading;
		} else if ((heading > 330) && (aux_apheading < 30)) {
			//aux_apheading = -360 + aux_apheading;
			aux_heading -= 360;
		}
				
		float aux = aux_apheading - aux_heading;	
		
		//Calculation of closets side from heading to apheading
		float aux2 = apheading - heading;	
		boolean flag = false;
		if (aux2 >= 0) {
			//positive
			if (aux2 >= 180) {
				flag = false; // Left
			} else {
				flag = true; //right
			}
			
		} else {
			//negative
			if (aux2 >= -180) {
				flag = false; //left
			} else {
				flag = true; //right
			}				
		}
		
		
		if (Math.abs(aux) <= 25) { 
			//Heading bug indicator visible
			offsetx = centerx + (int)((aux)*headingPitchScale*scaleFactor);
			
			apheadMatrix.reset();
			apheadMatrix.postTranslate(-aphead.getWidth()/2, -aphead.getHeight()/2);
			apheadMatrix.postScale(scaleFactor, scaleFactor);
			apheadMatrix.postTranslate(offsetx,offsety);
			canvas.drawBitmap(aphead, apheadMatrix, paint);
		
		} else if (flag) {
			//Right TEXT
			offsetx = centerx + (int)(250*scaleFactor);
			offsety = centery + (int)(350*scaleFactor);
		
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);	
			canvas.drawRect(offsetx - (int)(35*scaleFactor),offsety - (int)(30*scaleFactor),offsetx + (int)(35*scaleFactor),offsety + (int)(5*scaleFactor),  paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.WHITE);
			canvas.drawRect(offsetx - (int)(35*scaleFactor),offsety - (int)(30*scaleFactor),offsetx + (int)(35*scaleFactor),offsety + (int)(5*scaleFactor),  paint);
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.CYAN);
			
			if (apheading < 10) {
				canvas.drawText(String.format("00%d",apheading), offsetx, offsety, paint);
			} else if (apheading < 100) {
				canvas.drawText(String.format("0%d",apheading), offsetx, offsety, paint);
			} else {
				canvas.drawText(String.format("%d",apheading), offsetx, offsety, paint);
			}
			
		} else  {
			//Left TEXT
			offsetx = centerx - (int)(250*scaleFactor);
			offsety = centery + (int)(350*scaleFactor);
		
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);	
			canvas.drawRect(offsetx - (int)(35*scaleFactor),offsety - (int)(30*scaleFactor),offsetx + (int)(35*scaleFactor),offsety + (int)(5*scaleFactor),  paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.WHITE);
			canvas.drawRect(offsetx - (int)(35*scaleFactor),offsety - (int)(30*scaleFactor),offsetx + (int)(35*scaleFactor),offsety + (int)(5*scaleFactor),  paint);
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.CYAN);
			
			if (apheading < 10) {
				canvas.drawText(String.format("00%d",apheading), offsetx, offsety, paint);
			} else if (apheading < 100) {
				canvas.drawText(String.format("0%d",apheading), offsetx, offsety, paint);
			} else {
				canvas.drawText(String.format("%d",apheading), offsetx, offsety, paint);
			}
			
		}
			
	}
	
	void drawDME(Canvas canvas, Paint paint)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.MAGENTA);		
		paint.setTextSize(30*scaleFactor);
		
		if (dmeinrange == false)
			return;
		
		// Convert from meters to nautical miles
		dme /= 1852.;
				
		canvas.drawText(String.format("DME"), centerx - (int)(350*scaleFactor), centery + (int)(320*scaleFactor), paint);
		canvas.drawText(String.format("%3.1fNM", dme ), centerx - (int)(350*scaleFactor), centery + (int)(350*scaleFactor), paint);
		
	}
	
}

package com.rodriguez.saul.flightgearpfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MFD777View extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	
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
	
	Bitmap mask = null;
	Bitmap horizont = null;
	Bitmap vs = null;
	Bitmap marks = null;	
	Bitmap compass = null;
	Bitmap bug = null;
	Bitmap bugfilled = null;
	Bitmap crophorizont = null;	
	
	Matrix maskMatrix;
	Matrix horizontMatrix;
	Matrix vsMatrix;
	Matrix marksMatrix;
	Matrix compassMatrix;
	Matrix bugLocMatrix;
	
	
	public MFD777View(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		
		maskMatrix = new Matrix();
		horizontMatrix = new Matrix();
		vsMatrix = new Matrix();
		marksMatrix = new Matrix();
		compassMatrix = new Matrix();
		bugLocMatrix = new Matrix();
		
		scaleFactor = (float)1.0;
		mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
		horizont = BitmapFactory.decodeResource(getResources(), R.drawable.horizon);
		vs = BitmapFactory.decodeResource(getResources(), R.drawable.vs);
		marks = BitmapFactory.decodeResource(getResources(), R.drawable.speed_altitude);
		compass = BitmapFactory.decodeResource(getResources(), R.drawable.heading);
		bug = BitmapFactory.decodeResource(getResources(), R.drawable.bug);
		bugfilled = BitmapFactory.decodeResource(getResources(), R.drawable.bugfilled);
		
		
		horizontRollAngle = 0;
		horizontPitchAngle = 0;
		speed = 200;
		altitude = 12400;
		verticalSpeed = 500;
		heading = 0;
		locnavQuality = (float)0.95;
		locnav = false;
		headingLoc = (float)-0.97;
		gsInRange = false; //Is the glideslope in range?
		gsActive = false; //Is glidescope activated?
		gsDeflection = (float)-0.90; //Deflection of the glideslope normalizer (-1.0 to 1.0)
		
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
		
		Log.d("Saul", String.format("width = %d", mwidth));
		Log.d("Saul", String.format("height = %d", mheight));

		centerx = mwidth/2;
		centery = mheight/2;
		
		//Calculate the scale factor
		int maskHeight = mask.getHeight();
		
		//scaleFactor = (float) 0.5; //Only for test and new features
		scaleFactor = (float)(mheight)/(float)maskHeight;
				
		//Draw the view
		draw();
		
				
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public void draw() {
		
		long time, time2;
		time = System.currentTimeMillis();
		
		//Prepare the mask
		maskMatrix.reset();
		maskMatrix.postTranslate(-mask.getWidth()/2, -mask.getHeight()/2 );
		maskMatrix.postScale(scaleFactor, scaleFactor);
        maskMatrix.postTranslate(centerx, centery);
        
        //Prepare the artificial horizont
        /*
        horizontMatrix.reset();
        horizontMatrix.postTranslate(-horizont.getWidth()/2, (-horizont.getHeight()/2 + (float)calculatePitchshift()) );
        horizontMatrix.postRotate(horizontRollAngle);
        horizontMatrix.postScale(scaleFactor, scaleFactor);
        horizontMatrix.postTranslate(centerx, centery);
        */
        
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
        
        
        
        
		//Lock the canvas and start drawin
        Canvas canvas = surfaceHolder.lockCanvas();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //paint.setDither(true);
        canvas.drawColor(Color.BLACK);       
        //canvas.drawBitmap(horizont, horizontMatrix, paint);
        canvas.drawBitmap(crophorizont, horizontMatrix, paint);
        canvas.drawBitmap(vs, vsMatrix, null);
        drawAltitudeLine(canvas);
        drawSpeedLine(canvas);
        
        canvas.drawBitmap(mask,maskMatrix,paint);
        canvas.drawBitmap(compass, compassMatrix, paint);
        canvas.drawBitmap(marks, marksMatrix, paint);
        
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(35*scaleFactor));
        canvas.drawText(String.format("%d", (int)speed), (centerx - (int)(380*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        canvas.drawText(String.format("%d", (int)altitude), (centerx + (int)(315*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        
        drawVerticalSpeed(canvas);
        drawLocalizer(canvas);
        drawGlideslope(canvas);
        
        surfaceHolder.unlockCanvasAndPost(canvas);
        
        time2 = System.currentTimeMillis();
  		Log.d("777View", String.format("%d", (time2-time)));
       
	}

	double calculatePitchshift()
	{
		double aux;

		final double pitchDistance = 75.0; //vertical distance in number of pixels per 10 degree pitch
		
		aux = horizontPitchAngle*(pitchDistance/10.0);
		return aux;
	}
	
	void drawAltitudeLine(Canvas canvas) {
		int offsetx;
		Paint paint;
		final float verticalPitchScale = (float) (100./200); // 100pixels/200 feet = 0.5 pixels/ft
		offsetx = centerx + (int)(273*scaleFactor); //Border
		
		paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(30*scaleFactor));
		paint.setStrokeWidth((int)(2*scaleFactor));
		
		//Calculate first horizontal line
		int rest = (int)(altitude)%200; //residual to 1st lower mark in ft
		int altitude1 = (int)(altitude) - rest; // altitude in feet 1st lower mark
		int offset1 = (int) (rest*(verticalPitchScale*scaleFactor));
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
		
	void drawSpeedLine(Canvas canvas) {
			int offsetx;
			Paint paint;
			final float verticalPitchScale = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
			offsetx = centerx - (int)(300*scaleFactor); //Border
			
			paint = new Paint();
			paint.setAntiAlias(true);
			//paint.setDither(true);
			paint.setColor(Color.WHITE);
			paint.setTextSize(30*scaleFactor);
			paint.setStrokeWidth((int)(2*scaleFactor));
			
			//Calculate first horizontal line
			int rest = (int)(speed)%20; //residual to 1st lower mark in kts
			int speed1 = (int)(speed) - rest; // speed in kts of 1st lower mark
			int offset1 = (int) (rest*verticalPitchScale*scaleFactor);
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
		
	void drawVerticalSpeed(Canvas canvas)
	{
		int x1,x2,y1,y2;
		Paint paint;
		
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
				
		
		paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(24);	
		paint.setStrokeWidth((int)(3*scaleFactor));
		
		//Draw VS indicator line
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		//Draw VS text
		String vspeed = String.format("%d", (int)Math.abs(verticalSpeed));
		int x3 = centerx + (int)(420*scaleFactor);
		
		if (verticalSpeed > 500) {		
			canvas.drawText(vspeed, x3, (centery - (int)(210*scaleFactor)), paint);
		} else if (verticalSpeed < -500) {
			canvas.drawText(vspeed, x3, (centery + (int)(220*scaleFactor)), paint);
		}
	}
	
	void drawLocalizer(Canvas canvas)
	{
		final int lineLenght = 20; 	
		
		//Check if the localizer is selected and in range
		if ((locnavQuality < 0.93) || locnav == false) { 
			return;
		}
		
		
		Paint paint;
		paint = new Paint();
		paint.setAntiAlias(true);
		//paint.setDither(true);
        paint.setFilterBitmap(true);	    		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);			
		paint.setStrokeWidth((int)(3*scaleFactor));
		
		//Draw center line line
		canvas.drawLine(centerx, centery + (int)(230.*scaleFactor), centerx, centery + (int)(255.*scaleFactor), paint);
				
		int y2 = centery + (int)(240*scaleFactor);  
		
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
	
	void drawGlideslope(Canvas canvas)
	{
		//Check if the glidescope is activated and in range
		if (gsActive == false || gsInRange == false)
		{
			return;			
		}
				
		int offsetx;
		
		offsetx = centerx + (int)(235*scaleFactor); //Border
		
		Paint paint;
		paint = new Paint();
		paint.setAntiAlias(true);
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
	
	void SetSpeed(float newSpeed) 
	{
		speed = newSpeed;
	}
	
	void setAltitude(float newAltitude)
	{
		altitude = newAltitude;
	}
	
	void setVerticalSpeed(float newverticalSpeed) 
	{
		verticalSpeed = newverticalSpeed;		
	}
	
	void setRoll(float newRoll)
	{
		horizontRollAngle = -newRoll;	//the roll angle of FGFS is anti-clockwise whereas the rotation of matrix in android is clockwise
	}
	
	void setPitch(float newPitch)
	{
		horizontPitchAngle = newPitch;
	}
	
	void setHeading(float newHeading)
	{
		heading = newHeading;		
	}
	
	void setNAV1Quality(float newQuality)
	{
		locnavQuality = newQuality;
	}
	
	void setNAV1loc(boolean newNavLoc)
	{
		locnav = newNavLoc;		
	}
	
	void setNAV1deflection(float newDeflection)
	{
		headingLoc = newDeflection;
	}
	
	void setGSActive(boolean newGSactive)
	{
		gsActive = newGSactive;		
	}
	
	void setGSInRange(boolean newgsInRange)
	{
		gsInRange = newgsInRange;		
	}
	
	void setGSdeflection(float newgsDeflection)
	{
		gsDeflection = newgsDeflection;
	}
	
}

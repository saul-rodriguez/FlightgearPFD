

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
import android.graphics.Path;
import android.util.Log;

public class Plane787 extends Plane {
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
	
	Bitmap mask = null; */
	
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
	Bitmap boxglidescope = null;
	
	Matrix maskMatrix;
	Matrix horizontMatrix;
	Matrix vsMatrix;
	Matrix marksMatrix;
	Matrix compassMatrix;
	Matrix bugLocMatrix;
	Matrix apaltMatrix;
	Matrix apspeedindMatrix;
	Matrix apheadMatrix;
	Matrix boxglidescopeMatrix;
	Matrix boxlocalizerMatrix;
	
	Context mContext;
	
	
	//Alternative horizon 787
	Bitmap althorBitmap = null;
	Bitmap althorCropBitmap = null;
	
	Matrix althorMatrix;
	int altwidth;
	int altheight;
	Canvas altcanvas;
	
	public Plane787(Context context) {
		
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
		boxglidescopeMatrix = new Matrix();
		boxlocalizerMatrix = new Matrix();
		
		//Load the bitmaps		
		mask = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.mask);
		horizont = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.horizon787s);
		vs = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vs787);
		marks = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.speed_altitude);
		compass = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heading787);
		bug = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bug);
		bugfilled = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bugfilled);
		apalt = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.apalt);
		apspeedind = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.apspeed);
		aphead = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.aphdg);
		boxglidescope = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.glidescope);
		
		
		//Initialize all the parameters
		scaleFactor = (float)1.0;
		
		horizontRollAngle = 90;
		horizontPitchAngle = 90;
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
		dmeinrange = false;
		dme = 0;
		
		
		//Alternative horizont 787
		
		altwidth = 2500;
		altheight = 1300;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		althorBitmap = Bitmap.createBitmap(altwidth, altheight, conf);
		althorMatrix = new Matrix();
		altcanvas  = new Canvas(althorBitmap);
		
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
        
        /*
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
        //Canvas canvas = surfaceHolder.lockCanvas();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //paint.setDither(true);
        canvas.drawColor(Color.BLACK);   
        drawHorizont(canvas,paint);
        //canvas.drawBitmap(horizont, horizontMatrix, paint);
        //canvas.drawBitmap(crophorizont, horizontMatrix, paint);
        
        
        canvas.drawBitmap(vs, vsMatrix, null);
        drawAltitudeLine(canvas,paint);
        drawSpeedLine(canvas,paint);
        
        //canvas.drawBitmap(mask,maskMatrix,paint);
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
        drawDME(canvas,paint);
	}
	
	void drawHorizont(Canvas canvas, Paint paint)
	{
		final int width = altwidth;
		final int height = altheight;
		int borderx1 = -width/2;
		int bordery1 = -height/2;
		int borderx2 = width/2;		
		int bordery2 = height/2;
		int offx = width/2;
		int offy = height/2;
		
		
		paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
				
		
		if (horizontRollAngle == 90 || horizontRollAngle == -90) {
			horizontRollAngle += 0.01;
		}
		
		double m = Math.tan(horizontRollAngle/180*Math.PI);
		
		double x1,x2,y1,y2,x3,y3,aux_x1, aux_x2;
		
		double pitchOffset = scaleFactor*calculatePitchshift();
				
		//CEnter point
		x1 = 0;
		y1 = 0;
		//x1 = pitchOffset*Math.cos((horizontRollAngle+90)/180*Math.PI);
		//y1 = pitchOffset*Math.sin((horizontRollAngle+90)/180*Math.PI);;
		
		//Left  border point
		x2 = borderx1;
		y2 = y1 + m*(x2 - x1);

		//Right border point
		x3 = borderx2;
		y3 = y1 + m*(x3 - x1);
		
		//auxiliar points
		
		//left 
		aux_x1 = (bordery1 - y1)/m + x1;
		//right
		aux_x2 = (bordery2 - y1)/m + x1;
			
		
		//Apply offset to the points;
		
		borderx1 += offx;
		borderx2 += offx;
		bordery1 += offy;
		bordery2 += offy;
			
		Path path = new Path();
		path.moveTo(borderx1, bordery1);
		path.lineTo(borderx2, bordery1);
		path.lineTo(borderx2, bordery2);
		path.lineTo(borderx1, bordery2);
				
		//Draw background
		altcanvas.drawPath(path, paint);
	
		x1 += offx;
		y1 += offy;		
		x2 += offx;
		y2 += offy;		
		x3 += offx;		
		y3 += offy;
		
		aux_x1 += offx;
		aux_x2 += offx;
				
		paint.setColor(Color.rgb(169, 88, 15));
		Path ground = new Path();
		//ground down
		if (horizontRollAngle >= 0 && horizontRollAngle < 90) {
		
			if (y2 < bordery1) {
				ground.moveTo(borderx1, bordery1); //upper corner
				ground.lineTo((float)(aux_x1), bordery1); //point1
				
			} else {
				ground.moveTo((float)x2,(float)y2);				
			}
			
			if (y3 > bordery2) {
				ground.lineTo((float) (aux_x2), (float)bordery2);
				ground.lineTo((float) borderx2, (float) bordery2);
			} else {
				ground.lineTo((float)x3, (float)y3);
				ground.lineTo((float)x3, bordery2);
				
			}
			
			ground.lineTo((float)borderx1, (float)bordery2);
						
		}
		
		if (horizontRollAngle < 0 && horizontRollAngle > -90) {
			
			if (y2 > bordery2) {
				ground.moveTo(borderx1, bordery2); //upper corner
				ground.lineTo((float)(aux_x2), bordery2); //point1
				
			} else {
				ground.moveTo((float)x2,(float)y2);				
			}
			
			if (y3 < bordery1) {
				ground.lineTo((float) (aux_x1), (float)bordery1);
				ground.lineTo((float) borderx2, (float) bordery1);
				ground.lineTo((float) borderx2, (float) bordery2);
				
			} else {
				ground.lineTo((float)x3, (float)y3);
				ground.lineTo((float)x3, bordery2);
				
			}
			
			ground.lineTo((float)borderx1, (float)bordery2);
						
		}
		
		if (horizontRollAngle >= 90 && horizontRollAngle <= 180) {
			if (y2 > bordery2) {
				ground.moveTo(borderx1, bordery2); //upper corner
				ground.lineTo((float)(aux_x2), bordery2); //point1
				
			} else {
				ground.moveTo((float)x2,(float)y2);				
			}
			
			if (y3 < bordery1) {
				ground.lineTo((float) (aux_x1), (float)bordery1);
				ground.lineTo((float) borderx1, (float) bordery1);
			} else {
				ground.lineTo((float)x3, (float)y3);
				ground.lineTo((float)x3, (float)bordery1);
				ground.lineTo((float)borderx1, bordery1);
				
			}						
		}	
		
		if (horizontRollAngle <= -90 && horizontRollAngle >= -180) {
				
			if (y2 < bordery1) {
				
				//ground.moveTo(borderx1, bordery1); //upper corner
				ground.moveTo((float)(aux_x1), (float)bordery1); //point1
				ground.lineTo((float)(borderx2), (float)bordery1); //point1
				
				
			} else {
				ground.moveTo((float)x2, (float)y2); //upper corner
				ground.lineTo((float)x3,(float)y3);				
			}
			
			if (y3 > bordery2) {
				ground.lineTo((float) borderx2, (float)bordery2);
				ground.lineTo((float) aux_x2, (float) bordery2);
			} else {
				//ground.lineTo((float)x3, (float)y3);
				ground.lineTo((float)x3, (float)bordery1);
				ground.lineTo((float)borderx1, (float)bordery1);
				
			}						
		}	
		
		altcanvas.drawPath(ground, paint);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth((float)(2*scaleFactor));
		
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        			
		altcanvas.drawPath(ground, paint);
		
		paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        
		paint.setStyle(Paint.Style.FILL);
		
		int aux_x = horizont.getWidth()/2;
	    int aux_y = horizont.getHeight()/2 -(int)calculatePitchshift();
	    //int aux_y = horizont.getHeight()/2;
	    
	    //Change to centered square!
	    crophorizont = Bitmap.createBitmap(horizont,0, aux_y - 250, horizont.getWidth(), 500);
	                
	    horizontMatrix.reset();
	    horizontMatrix.postTranslate(-crophorizont.getWidth()/2, -crophorizont.getHeight()/2 -2);
	    horizontMatrix.postRotate(horizontRollAngle);
	    horizontMatrix.postScale(scaleFactor, scaleFactor);
	    
	    //move it to the center of the screen + offset!
	    horizontMatrix.postTranslate(centerx, centery);
		
	    //altcanvas.drawBitmap(crophorizont,horizontMatrix, paint);
		
		// Crop the alternative horizont and paste it on the final canvas 
		
		offx -= pitchOffset*Math.cos((horizontRollAngle+90)/180*Math.PI);
		offy -= pitchOffset*Math.sin((horizontRollAngle+90)/180*Math.PI);;
		
		//althorCropBitmap = Bitmap.createBitmap(althorBitmap,offx - 500,offy - 300 - (int)(55/scaleFactor), 1000, 600);
		althorCropBitmap = Bitmap.createBitmap(althorBitmap,offx - 500,offy - 300 - (int)(55), 1000, 600);
		
		althorMatrix.reset();
		althorMatrix.postTranslate(-althorCropBitmap.getWidth()/2, -althorCropBitmap.getHeight()/2);		
		althorMatrix.postScale(scaleFactor, scaleFactor);
		althorMatrix.postTranslate(centerx, centery - (int)(55*scaleFactor) );
				
        
		canvas.drawBitmap(althorCropBitmap, althorMatrix, paint);		
		
		paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        
		canvas.drawBitmap(crophorizont,horizontMatrix, paint);
		
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
			
			if (y[i] < (centery - (int)(250*scaleFactor)))
					continue;
			
			if (y[i] > (centery + (int)(250*scaleFactor)))
				continue;
			
			alt[i] = altitude1 + (600 - i*200);
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);
			canvas.drawText(String.format("%d", alt[i]), offsetx + (int)(30*scaleFactor), y[i]+(int)(10*scaleFactor), paint);
			
		}
		
		//Marks without text
		for (int i = 0; i<6; i++){
			y[i] = y1 - (int)((350 - i*100)*scaleFactor); 

			if (y[i] < (centery - (int)(250*scaleFactor)))
					continue;
			
			if (y[i] > (centery + (int)(250*scaleFactor)))
				continue;
			
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
			
			if (y[i] < (centery - (int)(250*scaleFactor)))
				continue;
		
			if (y[i] > (centery + (int)(250*scaleFactor)))
				continue;
			
			alt[i] = speed1 + (4*20) - i*20;
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);
			canvas.drawText(String.format("%d", alt[i]), offsetx - (int)(60*scaleFactor), y[i]+(int)(10*scaleFactor), paint);
			
		}
		
		//Marks without text
		for (int i = 0; i<8; i++){
			y[i] = y1 - (int)(((75*4+37) - i*75)*scaleFactor); 
			
			if (y[i] < (centery - (int)(250*scaleFactor)))
				continue;
		
			if (y[i] > (centery + (int)(250*scaleFactor)))
				continue;
			
			canvas.drawLine(offsetx, y[i], offsetx+(int)(30*scaleFactor), y[i], paint);			
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
		canvas.drawLine(x1, y1, x2, y2, paint);
		
		//Draw VS text
		int auxvs = (int)(Math.abs(verticalSpeed)/100)*100;
		String vspeed = String.format("%d", auxvs);
		int x3 = centerx + (int)(440*scaleFactor);
		
		if (verticalSpeed > 400) {		
			canvas.drawText(vspeed, x3, (centery - (int)(210*scaleFactor)), paint);
		} else if (verticalSpeed < -400) {
			canvas.drawText(vspeed, x3, (centery + (int)(220*scaleFactor)), paint);
		}
	}
	
	void drawLocalizer(Canvas canvas, Paint paint)
	{
		final int lineLenght = 20; 	
		
		//Check if the localizer is selected and in range
		if ((locnavQuality < 0.93) || locnav == false) { 
			return;
		}
		
		//Draw box
		//Prepare the mask
		boxlocalizerMatrix.reset();
		boxlocalizerMatrix.postTranslate(-boxglidescope.getWidth()/2, -boxglidescope.getHeight()/2 );
		boxlocalizerMatrix.postRotate(90);
		boxlocalizerMatrix.postScale(scaleFactor, scaleFactor);
		boxlocalizerMatrix.postTranslate(centerx, centery + (int)(230*scaleFactor));
				
		canvas.drawBitmap(boxglidescope, boxlocalizerMatrix, paint);
				
		
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
				
		int y2 = centery + (int)(230*scaleFactor);  
		canvas.drawLine(centerx, y2 - (int)(10*scaleFactor), centerx, y2 + (int)(15.*scaleFactor), paint);
		
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
		
		//Draw box
		//Prepare the mask
		boxglidescopeMatrix.reset();
		boxglidescopeMatrix.postTranslate(-boxglidescope.getWidth()/2, -boxglidescope.getHeight()/2 );
		boxglidescopeMatrix.postScale(scaleFactor, scaleFactor);
		boxglidescopeMatrix.postTranslate(centerx + (int)(247*scaleFactor), centery);
		
		canvas.drawBitmap(boxglidescope, boxglidescopeMatrix, paint);
		
		
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
		paint.setColor(Color.BLACK);
		canvas.drawRect(centerx - (int)(50*scaleFactor), centery + (int)(170*scaleFactor) , centerx + (int)(50*scaleFactor), centery + (int)(210*scaleFactor), paint);
		
		paint.setColor(Color.WHITE);
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
		
		canvas.drawText(String.format("%d",radioaltimeter), centerx, centery + (int)(205*scaleFactor), paint);
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
		canvas.drawText(pitchMode, centerx + (int)(165*scaleFactor), centery - (int)(320*scaleFactor), paint);
		
		//Roll Mode
		canvas.drawText(rollMode, centerx, centery - (int)(320*scaleFactor), paint);
		
		//Speed Mode
		canvas.drawText(speedMode, centerx - (int)(165*scaleFactor), centery - (int)(320*scaleFactor), paint);
	}
	
	void drawAPsettings(Canvas canvas, Paint paint)
	{
		int offsetx, offsety;
		final float verticalPitchScale = (float) (100./200); // 100pixels/200 feet = 0.5 pixels/ft
		final float verticalPitchScaleSpeed = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
		
		//Selected AP Altitude 
		offsetx = centerx + (int)(330*scaleFactor); //Border
		//paint.setColor(Color.MAGENTA);
		paint.setTextSize(35*scaleFactor);
		paint.setTextAlign(Align.CENTER);
				
		paint.setColor(Color.BLACK);
		canvas.drawRect(offsetx - (int)(60*scaleFactor), centery - (int)(305*scaleFactor), offsetx + (int)(60*scaleFactor), centery - (int)(265*scaleFactor), paint);
		
		paint.setColor(Color.MAGENTA);		
		
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
		paint.setColor(Color.BLACK);
		canvas.drawRect(offsetx - (int)(40*scaleFactor), centery - (int)(305*scaleFactor), offsetx + (int)(40*scaleFactor), centery - (int)(265*scaleFactor), paint);
		
		paint.setColor(Color.MAGENTA);		
		canvas.drawText(String.format("%d",(int)apspeed), offsetx, centery - (int)(270*scaleFactor), paint);
		
		//AP speed indicator
		if ((apspeed - speed) > 70) {
			apspeed = speed + 70;
		}
		
		if ((speed - apspeed) > 65) {
			apspeed = speed - 65;
		}
		
		offsetx = centerx - (int)(270*scaleFactor); //Border
		offsety = centery - (int)((apspeed - speed)*verticalPitchScaleSpeed*scaleFactor);
		
		apspeedindMatrix.reset();
		apspeedindMatrix.postTranslate(-apspeedind.getWidth()/2, -apspeedind.getHeight()/2);
		apspeedindMatrix.postScale(scaleFactor, scaleFactor);
		apspeedindMatrix.postTranslate(offsetx, offsety);
		
		canvas.drawBitmap(apspeedind, apspeedindMatrix, paint);
		
		//AP Heading Text
		offsetx = centerx - (int)(50*scaleFactor);
		offsety = centery + (int)(360*scaleFactor);
		paint.setTextSize(30*scaleFactor);
		
		if (apheading < 10) {
			canvas.drawText(String.format("00%dH",apheading), offsetx, offsety, paint);
		} else if (apheading < 100) {
			canvas.drawText(String.format("0%dH",apheading), offsetx, offsety, paint);
		} else {
			canvas.drawText(String.format("%dH",apheading), offsetx, offsety, paint);
		}
		
		
		//AP Heading indicator
		float rotation = heading - (float)(apheading);
		
		apheadMatrix.reset();
		apheadMatrix.postTranslate(-aphead.getWidth()/2, -aphead.getHeight()/2);
		apheadMatrix.postScale(scaleFactor, scaleFactor);
		apheadMatrix.postTranslate(0, -(280*scaleFactor));
		apheadMatrix.postRotate(-rotation);
		
		apheadMatrix.postTranslate(centerx,centery + (int)(548*scaleFactor));
		
		
		canvas.drawBitmap(aphead, apheadMatrix, paint);
			
	}
	
	void drawDME(Canvas canvas, Paint paint)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);		
		paint.setTextSize(25*scaleFactor);
		
		if (dmeinrange == false)
			return;
		
		//Change dme from meters to nautical miles
		dme /= 1852.;
		
		canvas.drawText(String.format("DME %3.1f", dme ), centerx - (int)(190*scaleFactor), centery - (int)(240*scaleFactor), paint);
		
	}
	
}

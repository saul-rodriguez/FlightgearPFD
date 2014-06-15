package com.rodriguez.saul.flightgearpfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
	
	private final double pitchDistance = 75.0; //vertical distance in number of pixels per 10 degree pitch
	
	Bitmap mask = null;
	Bitmap horizont = null;
	Bitmap vs = null;
	Bitmap marks = null;		
	
	Matrix maskMatrix;
	Matrix horizontMatrix;
	Matrix vsMatrix;
	Matrix marksMatrix;
	
	public MFD777View(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		
		maskMatrix = new Matrix();
		horizontMatrix = new Matrix();
		vsMatrix = new Matrix();
		marksMatrix = new Matrix();
		
		scaleFactor = (float)0.5;
		mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
		horizont = BitmapFactory.decodeResource(getResources(), R.drawable.horizon);
		vs = BitmapFactory.decodeResource(getResources(), R.drawable.vs);
		marks = BitmapFactory.decodeResource(getResources(), R.drawable.speed_altitude);
				
		horizontRollAngle = 45;
		horizontPitchAngle = 10;
		speed = 200;
		altitude = 12450;
		verticalSpeed = 500;
		
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
		int maskWidth = mask.getHeight();
		
		scaleFactor = (float)(mheight)/(float)maskWidth;
		
		//Draw the view
		draw();
		
				
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public void draw() {
				
		//Prepare the mask
		maskMatrix.reset();
		maskMatrix.postTranslate(-mask.getWidth()/2, -mask.getHeight()/2 );
		maskMatrix.postScale(scaleFactor, scaleFactor);
        maskMatrix.postTranslate(centerx, centery);
        
        //Prepare the artificial horizont
        horizontMatrix.reset();
        horizontMatrix.postTranslate(-horizont.getWidth()/2, (-horizont.getHeight()/2 + (float)calculatePitchshift()) );
        horizontMatrix.postRotate(horizontRollAngle);
        horizontMatrix.postScale(scaleFactor, scaleFactor);
        horizontMatrix.postTranslate(centerx, centery);
        
        
        //Prepare vertical speed and gray background
        vsMatrix.reset();
        vsMatrix.postTranslate(-vs.getWidth()/2, -vs.getHeight()/2);
        vsMatrix.postScale(scaleFactor, scaleFactor);
        vsMatrix.postTranslate(centerx+(int)(44*scaleFactor), centery-(int)(10*scaleFactor));
        
        //Prepare marks, speed & altitude boxes
        marksMatrix.reset();
        marksMatrix.postTranslate(-marks.getWidth()/2, -marks.getHeight()/2);
        marksMatrix.postScale(scaleFactor, scaleFactor);
        marksMatrix.postTranslate(centerx+(int)(13*scaleFactor), centery);
        
        
		//Lock the canvas and start drawin
        Canvas canvas = surfaceHolder.lockCanvas();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        //paint.setColor(Color.BLUE);
        canvas.drawColor(Color.WHITE);       
        canvas.drawBitmap(horizont, horizontMatrix, paint);
        canvas.drawBitmap(vs, vsMatrix, null);
        drawAltitudeLine(canvas);
        drawSpeedLine(canvas);
        
        
        canvas.drawBitmap(mask,maskMatrix,paint);
        canvas.drawBitmap(marks, marksMatrix, paint);
        
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(35*scaleFactor));
        canvas.drawText(String.format("%d", (int)speed), (centerx - (int)(380*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        canvas.drawText(String.format("%d", (int)altitude), (centerx + (int)(310*scaleFactor)), centery + (int)(10*scaleFactor), paint);
        
        drawVerticalSpeed(canvas);
        
        surfaceHolder.unlockCanvasAndPost(canvas);

		
	}

	double calculatePitchshift()
	{
		double aux;
		
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
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(30*scaleFactor));
		paint.setStrokeWidth(2);
		
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
			paint.setDither(true);
			paint.setColor(Color.WHITE);
			paint.setTextSize(30*scaleFactor);
			paint.setStrokeWidth(2);
			
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
		
		float scaleVs = (float) (180./3000.); //180 pixels / 3000 kfpm (Full-scale +-180 pixels)
				
		//x1 = centerx + 475; //right border
		x1 = centerx + (int)(550*scaleFactor); //right point
		x2 = centerx + (int)(435*scaleFactor); //
		y1 = centery;
		
		//Calculation of line deviation
		y2 = centery - (int)(verticalSpeed*scaleVs*scaleFactor);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		//paint.setTextSize(30);
		paint.setStrokeWidth(3);
		canvas.drawLine(x1, y1, x2, y2, paint);
		
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
	
}

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
		
		mask = BitmapFactory.decodeResource(getResources(),R.drawable.mask);
		horizont = BitmapFactory.decodeResource(getResources(), R.drawable.horizon);
		vs = BitmapFactory.decodeResource(getResources(), R.drawable.vs);
		marks = BitmapFactory.decodeResource(getResources(), R.drawable.speed_altitude);
				
		horizontRollAngle = 0;
		horizontPitchAngle = 0;
		speed = 0;
		altitude = 0;
		verticalSpeed = 0;
		
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
        maskMatrix.postTranslate(centerx, centery);
        
        //Prepare the artificial horizont
        horizontMatrix.reset();
        horizontMatrix.postTranslate(-horizont.getWidth()/2, (-horizont.getHeight()/2 + (float)calculatePitchshift()) );
        horizontMatrix.postRotate(horizontRollAngle);
        horizontMatrix.postTranslate(centerx, centery);
        
        //Prepare vertical speed and gray background
        vsMatrix.reset();
        vsMatrix.postTranslate(-vs.getWidth()/2, -vs.getHeight()/2);
        vsMatrix.postTranslate(centerx+44, centery-10);
        
        //Prepare marks, speed & altitude boxes
        marksMatrix.reset();
        marksMatrix.postTranslate(-marks.getWidth()/2, -marks.getHeight()/2);
        marksMatrix.postTranslate(centerx+13, centery);
                
		
		/*
				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			 	Canvas canvas = surfaceHolder.lockCanvas();
			    canvas.drawColor(Color.BLACK);
			    paint.setColor(Color.BLUE);
			    canvas.drawCircle(300, 200, 50, paint);
			    surfaceHolder.unlockCanvasAndPost(canvas);
		*/
		
		
		
		//Create background bitmap
		//Bitmap bg = Bitmap.createBitmap(mwidth, mheight, Bitmap.Config.ARGB_8888);
		
		//Canvas bgcanvas = new Canvas(bg); 
        //bgcanvas.drawColor(Color.BLACK);
        
		//Lock the canvas and start drawin
        Canvas canvas = surfaceHolder.lockCanvas();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        //paint.setColor(Color.BLUE);
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(300, 200, 50, paint);
        canvas.drawBitmap(horizont, horizontMatrix, paint);
        canvas.drawBitmap(vs, vsMatrix, null);
        drawAltitudeLine(canvas);
        drawSpeedLine(canvas);
        
        
        canvas.drawBitmap(mask,maskMatrix,null);
        canvas.drawBitmap(marks, marksMatrix, null);
        
        paint.setColor(Color.WHITE);
        paint.setTextSize(35);
        canvas.drawText(String.format("%d", (int)speed), (centerx - 380), centery + 10, paint);
        canvas.drawText(String.format("%d", (int)altitude), (centerx + 310), centery + 10, paint);
        
        drawVerticalSpeed(canvas);
        //canvas.drawTe
        
        
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
		offsetx = centerx + 273; //Border
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		paint.setStrokeWidth(2);
		
		//Calculate first horizontal line
		int rest = (int)(altitude)%200; //residual to 1st lower mark in ft
		int altitude1 = (int)(altitude) - rest; // altitude in feet 1st lower mark
		int offset1 = (int) (rest*verticalPitchScale);
		int y1 = centery + offset1;
		
		int y[] = new int[6];
		int alt[] = new int[6];
		
		//Marks with text
		for (int i = 0; i<6; i++){
			y[i] = y1 - 300 + i*100;
			alt[i] = altitude1 + 600 - i*200;
			canvas.drawLine(offsetx, y[i], offsetx+30, y[i], paint);
			canvas.drawText(String.format("%d", alt[i]), offsetx + 30, y[i]+10, paint);
			
		}
		
		//Marks without text
		for (int i = 0; i<6; i++){
			y[i] = y1 - 350 + i*100; 
			canvas.drawLine(offsetx, y[i], offsetx+30, y[i], paint);			
		}
	}
		
	void drawSpeedLine(Canvas canvas) {
			int offsetx;
			Paint paint;
			final float verticalPitchScale = (float) (75./20); // 75pixels/20 kts = 3.75 pixels/kts
			offsetx = centerx - 300; //Border
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setColor(Color.WHITE);
			paint.setTextSize(30);
			paint.setStrokeWidth(2);
			
			//Calculate first horizontal line
			int rest = (int)(speed)%20; //residual to 1st lower mark in kts
			int speed1 = (int)(speed) - rest; // speed in kts of 1st lower mark
			int offset1 = (int) (rest*verticalPitchScale);
			int y1 = centery + offset1;
			
			int y[] = new int[8];
			int alt[] = new int[8];
			
			//Marks with text
			for (int i = 0; i<8; i++){
				y[i] = y1 - (75*4) + i*75;
				alt[i] = speed1 + (4*20) - i*20;
				canvas.drawLine(offsetx, y[i], offsetx+30, y[i], paint);
				canvas.drawText(String.format("%d", alt[i]), offsetx - 60, y[i]+10, paint);
				
			}
			
			//Marks without text
			for (int i = 0; i<8; i++){
				y[i] = y1 - (75*4+37) + i*75; 
				canvas.drawLine(offsetx, y[i], offsetx+30, y[i], paint);			
			}		
	}
		
	void drawVerticalSpeed(Canvas canvas)
	{
		int x1,x2,y1,y2;
		Paint paint;
		
		float scaleVs = (float) (180./3000.); //180 pixels / 3000 kfps (Full-scale +-180 pixels)
				
		//x1 = centerx + 475; //right border
		x1 = centerx + 550; //right point
		x2 = centerx + 435; //
		y1 = centery;
		
		//Calculation of line deviation
		y2 = centery - (int)(verticalSpeed*scaleVs);
		
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

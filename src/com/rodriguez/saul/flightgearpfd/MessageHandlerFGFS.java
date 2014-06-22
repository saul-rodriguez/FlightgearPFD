package com.rodriguez.saul.flightgearpfd;

import java.util.Date;

public class MessageHandlerFGFS {
	String[] data;
	String[] outData;
	private Date date = new Date();
	//private MovingAverage[] averages;
	
	public MessageHandlerFGFS() 
	{
		data = null;	
		
	}
	
	public void parse(final String input) 
	{
		// strip string with new line
		String realInput = input.substring(0, input.indexOf("\n"));
		data = realInput.split(":");

		date = new Date();

		// check that we have the desired number of parameters
		// just read the last data. If throws IndexOutOfBounds, the
		// other extreme is sending wrong data
				
		getFloat(GSDEF);
	}
	
	public static final int SPEED = 0; // speed, in knots
	public static final int ALTITUDE = 1; // altitude, in feet, according to the instruments
	public static final int VS = 2; //vertical speed (rate of climb) in fpm (needs to be transformed to fps!)
	public static final int PITCH = 3; //pitch angle degrees
	public static final int ROLL = 4; //roll angle in degrees	
	public static final int HEADING = 5; // heading in degrees
	public static final int NAV1QUALITY = 6; // is a selected NAV localizer in range? 
	public static final int NAV1LOC = 7; //is the selected NAV present?
	public static final int NAV1DEF = 8; // NAV1 normalizedr deflection (-1 to 1)
	public static final int GSACTIVATED = 9; // is the glideslope activated?
	public static final int GSINRANGE = 10; // is the glideslope in range?
	public static final int GSDEF = 11; // glideslope normalized deflection (-1 to 1)
	
	public int getInt(int i) 
	{
		if (data == null) {
			return 0;
		}
		return Integer.valueOf(data[i]);
	}

	public float getFloat(int i) 
	{
		if (data == null) {
			return 0;
		}
		//MovingAverage ma = this.averages[i];
		//if (ma==null) {
		//return Float.valueOf(data[i]);
		//} else {
		//return ma.getData(Float.valueOf(data[i]));
		//}
		return Float.valueOf(data[i]);
	}

	public String getString(int i) 
	{
		if (data == null) {
			return "";
		}
		return data[i];
	}

	public boolean getBool(int i)
	{
		if (data == null) {
			return false;
		}
		return data[i].equals("1");
	}

	public Date getDate() 
	{
		return date;
	}

	public boolean hasData() 
	{
		return data != null;
	}
			
}

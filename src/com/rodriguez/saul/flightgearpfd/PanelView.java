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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class PanelView extends Activity {

	//Communications related members
	private int udpPort;	
	private UDPReceiver udpReceiver = null;
	public static final int SOCKET_TIMEOUT = 10000;
	
	//Selected plane
	private int selPlane;
	
	MFD777View mMFD777;
	
	//Debug constant
	private static final String MLOG = "PANELVIEW";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Orientation allways landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//Set Full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_panel_view);
		
		// Get the configuration via intent
		Intent intent = getIntent();				
		udpPort = intent.getIntExtra(MainActivity.MESS_PORT, 5502);		
		selPlane = intent.getIntExtra(MainActivity.SELECTED_PLANE, 0);
		
		//Attach the custom view to a MFD777 object
		mMFD777 = (MFD777View)findViewById(R.id.myFDMView);
		mMFD777.setPlane(selPlane);
				
		Log.d(MLOG,"Port: " + String.format("%d", udpPort));
	}
	
	 @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			
			if (udpReceiver != null) {
			     udpReceiver.cancel(true);
			     udpReceiver = null;
			}
		}


		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			Log.d(MLOG, "Pausing threads");
			
			if (udpReceiver != null) {
				 
			     udpReceiver.cancel(true); 
			     udpReceiver = null;
			}
			
			super.onPause();
		}

		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			Log.d(MLOG,"Starting threads");
			
			if (udpReceiver == null) {
			     udpReceiver = (UDPReceiver) new UDPReceiver().execute(udpPort);
			}
					
			Toast toast= Toast.makeText(this,"Connecting..." ,Toast.LENGTH_LONG);
			toast.show();
					
		}
		
		/*
		 *  The UDPReceiver AsyncTask receiver uses 1st parameter (sent to doInBackground) for the UDP port number (integer).
		 *  The 2nd parameter is an object of the helper class MessageHandlerFGFS (sent to onProgressUpdate). The 3rd parameter is 
		 *  a String containing the result of the doInBackground task ( it is supposed to be sent to a method onPostExecute, but for simplicity it is 
		 *  not used in this example).
		 * */
		
		private class UDPReceiver extends AsyncTask<Integer, MessageHandlerFGFS, String> {

			/*
			 *  doInBackground() opens a socket and receives the data from fgfs. It will wait SOCKET_TIMOUT ms before throwing an
			 *  exception. 
			 *  The incoming data is parsed by the parse() method where the buffer is split in multiple Strings. Each String contains 
			 *  an updated parameter. After parsing the buffer, the onProgressUpdate is called by using this.pubishProgress(pd). 
			 * */
			@Override
			protected String doInBackground(Integer... params) {
				// TODO Auto-generated method stub
				DatagramSocket socket;
				byte[] buf = new byte[512];
				
				boolean canceled = false;
				String msg = null;
				MessageHandlerFGFS pd = new MessageHandlerFGFS();
				
				try {
						socket = new DatagramSocket(params[0]);
						socket.setSoTimeout(SOCKET_TIMEOUT);
				} catch (SocketException e) {
						Log.d(MLOG, e.toString());
						return e.toString();
				}
				
				Log.d(MLOG,"UDP Thread started and liseting on port: " + params[0]);
										
				while (!canceled) {
					DatagramPacket p = new DatagramPacket(buf, buf.length);
					
					try {
						socket.receive(p);
						pd.parse(new String(p.getData()));
						
						//Log.d(MLOG,"pd parsed");
						
						//Add here a call to progress with the pd as param
						this.publishProgress(pd);
						//Check if the asynctask was cancelled somewhere else 
						canceled = this.isCancelled();
						
					} catch (SocketTimeoutException e) {	
						Log.d(MLOG,"Socket Timeout Exception");
						canceled = true;
					} catch (Exception e) {
						Log.d(MLOG,"Socket exception");
						canceled = true;
					}
					
				}
				
				socket.close();
				
				Log.d(MLOG, "UDP thread finished");
				
				return msg;
			}

			/*
			 * onProgressUpdate() updates the Activity fields
			 * */
			@Override
			protected void onProgressUpdate(MessageHandlerFGFS... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
											
				mMFD777.SetSpeed(values[0].getFloat(MessageHandlerFGFS.SPEED));
				mMFD777.setAltitude(values[0].getFloat(MessageHandlerFGFS.ALTITUDE));
				mMFD777.setVerticalSpeed(values[0].getFloat(MessageHandlerFGFS.VS));
				mMFD777.setPitch(values[0].getFloat(MessageHandlerFGFS.PITCH));
				mMFD777.setRoll(values[0].getFloat(MessageHandlerFGFS.ROLL));
				mMFD777.setHeading(values[0].getFloat(MessageHandlerFGFS.HEADING));
				mMFD777.setNAV1Quality(values[0].getFloat(MessageHandlerFGFS.NAV1QUALITY));
				mMFD777.setNAV1loc(values[0].getBool(MessageHandlerFGFS.NAV1LOC));
				mMFD777.setNAV1deflection((float)values[0].getFloat(MessageHandlerFGFS.NAV1DEF));
				mMFD777.setGSActive(values[0].getBool(MessageHandlerFGFS.GSACTIVATED));
				mMFD777.setGSInRange(values[0].getBool(MessageHandlerFGFS.GSINRANGE));
				mMFD777.setGSdeflection((float)(values[0].getFloat(MessageHandlerFGFS.GSDEF)));
				mMFD777.setRadioaltimeter(values[0].getInt(MessageHandlerFGFS.RADIOALTIMETER));
				mMFD777.setMach(values[0].getFloat(MessageHandlerFGFS.MACHSPEED));
				mMFD777.setStallSpeed(values[0].getFloat(MessageHandlerFGFS.STALLSPEED));
				mMFD777.setStallWarning(values[0].getBool(MessageHandlerFGFS.STALLWARNING));
				mMFD777.setFlaps(values[0].getFloat(MessageHandlerFGFS.FLAPS));
				mMFD777.setMaxSpeed(values[0].getFloat(MessageHandlerFGFS.MAXSPEED));
				mMFD777.setApIndicator(values[0].getString(MessageHandlerFGFS.AP));	
				mMFD777.setPitchMode(values[0].getString(MessageHandlerFGFS.PITCHMODE));
				mMFD777.setRollMode(values[0].getString(MessageHandlerFGFS.ROLLMODE));
				mMFD777.setSpeedMode(values[0].getString(MessageHandlerFGFS.SPEEDMODE));
				mMFD777.setAPaltitude(values[0].getFloat(MessageHandlerFGFS.APALTITUDE));
				mMFD777.setAPactualaltitude(values[0].getFloat(MessageHandlerFGFS.APACTUALALT));				
				mMFD777.setAPspeed(values[0].getFloat(MessageHandlerFGFS.APSPEED));
				mMFD777.setAPheading(values[0].getInt(MessageHandlerFGFS.APHEADING));	
				mMFD777.setDMEinrange(values[0].getBool(MessageHandlerFGFS.DMEINRANGE));
				mMFD777.setDME(values[0].getFloat(MessageHandlerFGFS.DME));
				
				mMFD777.draw();
				
				
			}		
	    	
	    }
}

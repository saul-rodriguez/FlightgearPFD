package com.rodriguez.saul.flightgearpfd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


import android.app.Activity;
import android.content.Intent;
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
	
	MFD777View mMFD777;
	
	//Debug constant
	private static final String MLOG = "PANELVIEW";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Set Full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_panel_view);
		
		// Get the configuration via intent
		Intent intent = getIntent();				
		udpPort = intent.getIntExtra(MainActivity.MESS_PORT, 5502);
		
		//Attach the custom view to a MFD777 object
		mMFD777 = (MFD777View)findViewById(R.id.myFDMView);
				
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
						
						Log.d(MLOG,"pd parsed");
						
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
				
				/*
				String aux; 
				aux = "Speed: " + values[0].data[MessageHandlerFGFS.SPEED];						
				//speed.setText(aux);
				Log.d(MLOG,aux);
				
				aux = "Atitude: " + values[0].data[MessageHandlerFGFS.ALTITUDE];			
				//altitude.setText(aux);
				Log.d(MLOG,aux);
				
				aux = "Vertical Speed: " + values[0].data[MessageHandlerFGFS.VS];
				Log.d(MLOG,aux);
				
				aux = "Pitch: " + values[0].data[MessageHandlerFGFS.PITCH];
				Log.d(MLOG,aux);
				
				aux = "Roll: " + values[0].data[MessageHandlerFGFS.ROLL];
				Log.d(MLOG,aux);
				*/
				
				mMFD777.SetSpeed(values[0].getFloat(MessageHandlerFGFS.SPEED));
				mMFD777.setAltitude(values[0].getFloat(MessageHandlerFGFS.ALTITUDE));
				mMFD777.setVerticalSpeed(values[0].getFloat(MessageHandlerFGFS.VS));
				mMFD777.setPitch(values[0].getFloat(MessageHandlerFGFS.PITCH));
				mMFD777.setRoll(values[0].getFloat(MessageHandlerFGFS.ROLL));
				mMFD777.setHeading((float)values[0].getInt(MessageHandlerFGFS.HEADING));
				
				mMFD777.draw();
				
				
			}		
	    	
	    }
}

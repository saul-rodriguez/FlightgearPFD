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


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
			
	private EditText port;
	private int m_port;
	private TextView instructions;
		
	//Debug constant
	private static final String MLOG = "MAINACTIVITY";
	
	public final static String MESS_PORT = "MESSPORT";
	
	public static final int B777 = 1;
	public static final int B787 = 2;
	public static final int A330 = 3;
	public static final int FREE = 4;
	
	int plane = FREE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Close the soft keyboard on start/resume activity
		getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		port = (EditText)findViewById(R.id.editTextPort);
		instructions = (TextView)findViewById(R.id.textViewConf);
		
		m_port = 5502;
		port.setText(String.format("%d", m_port));
		
		//instructions.setText();
		ShowInstructions();
		
		
		//Log.d(MLOG, "IP: " + ip);
		
	}
	
	
	public void onConnect(View view)
	{
		String aux;
		aux = port.getText().toString();
		m_port = Integer.valueOf(aux);		
				
		Log.d(MLOG, "Sending intent port: " + String.format("%d", m_port));
		
		Intent intent = new Intent(this, PanelView.class);
		
		//String message = "Hola";
		intent.putExtra(MESS_PORT,m_port);		
				
		startActivity(intent);	
	}
	
	public void onWebsite(View view)
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sites.google.com/site/flightgearandroid/flightgear-pfd"));
		startActivity(browserIntent);
	}
	
	void ShowInstructions()
	{
		
		//Find IP address
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		
		instructions.setTextSize(18);
		String text;
		text = "Version 0.1";
		text += "\nINSTRUCTIONS \n\n";
		
		if (plane == B777) {
			text += "1 Download the protocol file androidpfd777.xml from: https://github.com/saul-rodriguez/FlightgearPFD\n";
		} else if (plane == B787) {
			text += "1 Download the protocol file androidpfd787.xml from: https://github.com/saul-rodriguez/FlightgearPFD\n";
		} else if (plane == A330) {
			text += "1 Download the protocol file androidpfd330.xml from: https://github.com/saul-rodriguez/FlightgearPFD\n";
		} else if (plane == FREE) {
			text += "1 Download the files androidpfd777.xml and  androidpfd787.xml from: https://github.com/saul-rodriguez/FlightgearPFD\n";
		}
				
		text += "2 Copy these files in the directory $FG_ROOT/Protocol/\n";
		text += "3 Enable WiFi in your android device\n";
		text += "4 Launch flightgear with the option: --generic=socket,out,[Frequency],[IP android],[port],udp,[protocol filename] where:\n";
		text += "[Frequency] = Refresh rate in Hz\n";
		text += "[IP android] = The IP address of this device: " +  ip + " \n";
		text += "[port] = Port number (must match field PORT NUMBER entered above)\n";
		text += "[protocol filename  without .xml] = either androidpfd777 or  androidpfd787\n\n";
		text += "Example:\n";
		text += "fgfs --generic=socket,out,20,"+ ip +",5502,udp,androidpfd777\n";
		text += "5 Wait until flightgear finishes to start (cockpit visible), and click “Connect” in the android device.\n\n";

		text += "Detailed instructions available at:  https://sites.google.com/site/flightgearandroid/flightgear-pfd\n";
		
		
		instructions.setText(text);
		
		
	}
   
}

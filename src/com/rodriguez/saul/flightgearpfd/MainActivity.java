package com.rodriguez.saul.flightgearpfd;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
			
	private EditText port;
	private int m_port;
		
	//Debug constant
	private static final String MLOG = "MAINACTIVITY";
	
	public final static String MESS_PORT = "MESSPORT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		port = (EditText)findViewById(R.id.editTextPort);
		
		m_port = 5502;
		port.setText(String.format("%d", m_port));
		
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
   
}

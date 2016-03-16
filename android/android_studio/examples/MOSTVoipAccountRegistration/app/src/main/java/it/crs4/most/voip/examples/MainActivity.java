/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.examples;

import java.util.ArrayList;
import java.util.HashMap;

import it.crs4.most.voip.Utils;
import it.crs4.most.voip.VoipLib;
import it.crs4.most.voip.VoipLibBackend;
import it.crs4.most.voip.VoipEventBundle;
import it.crs4.most.voip.enums.VoipEvent;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server </li>
 * <li>unregister an account to a remote Sip Server </li>
 * <li>deinitialize the Voip Lib </li>
 * </ul>
 *  
 *
 */
public class MainActivity extends Activity implements Handler.Callback {
	private static final String TAG = "VoipTestActivity";
	private final Handler handler = new Handler(this);
	
	private ArrayList<String> infoArray = null;
	private VoipLib myVoip =  null;
	private ArrayAdapter<String> arrayAdapter = null;
	
	
	private VoipEvent [] expectedStates = { 
			VoipEvent.LIB_INITIALIZED, 
			VoipEvent.ACCOUNT_REGISTERING, 
			VoipEvent.ACCOUNT_REGISTERED, 
			VoipEvent.ACCOUNT_UNREGISTERING,
			VoipEvent.ACCOUNT_UNREGISTERED,
			VoipEvent.LIB_DEINITIALIZING,
			VoipEvent.LIB_DEINITIALIZED
	};

	private int curStateIndex = 0;
	// Change this parameter according to ypur sip server configuration
	private String sipServerIp = "156.148.33.193";
	private String sipServerPort = "5060";
	private String sipServerTransport = "udp";
	private String sipUsername = "vitto";
	private String sipPassword = "vitto";


	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeGUI();
        this.runExample();
    }
    
    private void initializeGUI()
    {
    	setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.listOperations);
        this.infoArray = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(arrayAdapter);
         
        //this.addInfoLine("Most Voip Lib Test Example 1");
    }
    
    private void waitForInitialization() {
    	/* Wait for GDB to init */
    	Log.d(TAG, "Waiting some second before initializing the lib...");
    	if ((this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
    		this.waitForSeconds(3);
		}
    }
    
    
    public void runExample()
    {
    	this.waitForInitialization();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params
        // put your own params here, according to your remote Sip Server Configuration
		HashMap<String,String> params = new HashMap<>();
		params.put("sipServerIp", sipServerIp);
		params.put("sipServerTransport", sipServerTransport);
		params.put("userName", sipUsername);
		params.put("userPwd", sipPassword);
		params.put("sipServerPort", sipServerPort);
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
		}
		
		// Initialize the library providing this context and custom initialization params and an handler where
		// to receive event notifications. Following Voip methods are called form the handleMassage() callback method
		boolean result = myVoip.initLib(this,params, handler);
	
    }
    
    private void waitForSeconds(int secs)
    {
    	try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    private void addInfoLine(String info)
    {
    	this.infoArray.add(info);
    	if (arrayAdapter!=null)
    		arrayAdapter.notifyDataSetChanged();
    }

	@Override
	public boolean handleMessage(Message voipMessage) {
		//int msg_type = voipMessage.what;
		Log.d(TAG, "Called handleMessage with info...");
		VoipEventBundle myEvent = (VoipEventBundle) voipMessage.obj;
		String infoMsg = "Event:" + myEvent.getEvent() + ":" + myEvent.getInfo();
		Log.d(TAG, "Event info:" + infoMsg);
		this.addInfoLine(infoMsg);
		
		assert( myEvent.getEvent()==expectedStates[curStateIndex]);
		curStateIndex++;
		// Register the account after the Lib Initialization
		if (myEvent.getEvent()==VoipEvent.LIB_INITIALIZED)   myVoip.registerAccount();	
		 
		// Unregister the account
		else if (myEvent.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    myVoip.unregisterAccount();	
		// Deinitialize the Voip Lib and release all allocated resources
		else if (myEvent.getEvent()==VoipEvent.ACCOUNT_UNREGISTERED)  myVoip.destroyLib();
		     
		     
		return false;
	}
    
}
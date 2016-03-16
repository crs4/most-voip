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
import android.app.Service;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;	

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server (by specifying the address of the server and the username and password of the Sip Account) </li>
 * <li>make a call to a remote user (by specifying an extension</li>
 * <li>unregister the previously registered account from the Sip Server </li>
 * <li>deinitialize the Voip Lib </li>
 * </ul>
 *
 */
public class MainActivity extends Activity {
	private static final String TAG = "VoipTestActivity";
	//private final Handler handler = new Handler(this);
	
	private ArrayList<String> infoArray = null;
	private VoipLib myVoip =  null;
	private ArrayAdapter<String> arrayAdapter = null;
	
	

	private static class AbstractAppHandler extends Handler{
		protected MainActivity app = null;
		protected VoipLib myVoip = null;
		protected  int curStateIndex = 0;
		
 		public AbstractAppHandler(MainActivity app, VoipLib myVoip) {
 			super();
			this.app = app;
			this.myVoip = myVoip;
		}
 		
 		protected VoipEventBundle getStateBundle(Message voipMessage)
 		{
 			//int msg_type = voipMessage.what;
			VoipEventBundle myState = (VoipEventBundle) voipMessage.obj;
			String infoMsg = "State:" + myState.getEvent() + ":" + myState.getInfo();
			Log.d(TAG, "Called handleMessage with state info:" + infoMsg);
			this.app.addInfoLine(infoMsg);
			return myState;
 		}
	}
	
	
	private class MakeCallHandler extends AbstractAppHandler {
		
		private String extension = null;
		
		private VoipEvent [] expectedStates = { VoipEvent.LIB_INITIALIZED , 
				VoipEvent.ACCOUNT_REGISTERING, 
				VoipEvent.ACCOUNT_REGISTERED, 
				VoipEvent.CALL_DIALING,
				VoipEvent.CALL_ACTIVE,
				VoipEvent.CALL_HANGUP,
				VoipEvent.ACCOUNT_UNREGISTERING,
				VoipEvent.ACCOUNT_UNREGISTERED,
				VoipEvent.LIB_DEINITIALIZING,
				VoipEvent.LIB_DEINITIALIZED};
		
		public MakeCallHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
			EditText txtView =(EditText) findViewById(R.id.txtExtension);
			this.extension = txtView.getText().toString();
		}

		@Override
		public void handleMessage(Message voipMessage) {
			VoipEventBundle myState = getStateBundle(voipMessage);
			
			assert( myState.getEvent()==expectedStates[curStateIndex]);
			curStateIndex++;
			// Register the account after the Lib Initialization
			if (myState.getEvent()==VoipEvent.LIB_INITIALIZED)   myVoip.registerAccount();	
			else if (myState.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    myVoip.makeCall(extension);	
			else if  (myState.getEvent()==VoipEvent.CALL_ACTIVE)    {
				//this.app.waitForSeconds(20);
				//myVoip.hangupCall();
			}
			// Unregister the account
			else if (myState.getEvent()==VoipEvent.CALL_HANGUP)    myVoip.unregisterAccount();	
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myState.getEvent()==VoipEvent.ACCOUNT_UNREGISTERED)  myVoip.destroyLib();
			     
		}
	   
	}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.waitForInitialization();
        this.initializeGUI();
        
        //this.runExample();
    }
    
    /**
     * Invoked when the 'Go' button is clicked
     */
    public void doVoipTest(View view) {
    	EditText txtView =(EditText) this.findViewById(R.id.txtServerIp);
    	String serverIp = txtView.getText().toString();
    	InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(txtView.getWindowToken(), 0); 
    	
    	this.runExample(serverIp);
    }
    
    private void initializeGUI()
    {
    	setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.listOperations);
        this.infoArray = new ArrayList<String>();
        arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
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
    
    
    public void runExample(String serverIp)
    {
    	
    	
    	this.clearInfoLines();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params

		HashMap<String,String> params = new HashMap<String,String>();
		params.put("sipServerIp",serverIp);
		params.put("sipServerTransport", "udp");
		params.put("userName","mauro");
		params.put("userPwd","mauro");
		//params.put("sipServerPort","5060"); // optional: default 5060
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
		}
		
		// Initialize the library providing custom initialization params and an handler where
		// to receive event notifications. Following Voip methods are called form the handleMassage() callback method
		//boolean result = myVoip.initLib(params, new RegistrationHandler(this, myVoip));
		boolean result = myVoip.initLib(this.getApplicationContext(),params, new MakeCallHandler(this, myVoip));
    }
    
    public void waitForSeconds(int secs)
    {
    	try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    private void clearInfoLines()
    {
    	this.infoArray.clear();
    	if (arrayAdapter!=null)
    		arrayAdapter.notifyDataSetChanged();
    }
    
    public void addInfoLine(String info)
    {
    	this.infoArray.add(info);
    	if (arrayAdapter!=null)
    		arrayAdapter.notifyDataSetChanged();
    }
}
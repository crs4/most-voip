package most.voip.example;
import java.util.ArrayList;
import java.util.HashMap;

import most.voip.api.Utils;
import most.voip.api.VoipLib;
import most.voip.api.VoipLibBackend;
import most.voip.api.VoipState;
import most.voip.api.VoipStateBundle;
import most.voip.example1.R;
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
import android.widget.TextView;

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server </li>
 * <li>unregister an account to a remote Sip Server </li>
 * <li>deinitialize the Voip Lib </li>
 * </ul>
 * @author crs4
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
 		
 		protected VoipStateBundle getStateBundle(Message voipMessage)
 		{
 			//int msg_type = voipMessage.what;
			VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
			String infoMsg = "State:" + myState.getState() + ":" + myState.getInfo();
			Log.d(TAG, "Called handleMessage with state info:" + infoMsg);
			this.app.addInfoLine(infoMsg);
			return myState;
 		}
	}
	
	private class RegistrationHandler extends AbstractAppHandler {
		
		private VoipState [] expectedStates = { VoipState.INITIALIZED , 
				VoipState.REGISTERING, 
				VoipState.REGISTERED, 
				VoipState.UNREGISTERING,
				VoipState.UNREGISTERED,
				VoipState.DEINITIALIZING,
				VoipState.DEINITIALIZE_DONE};
		
		public RegistrationHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
		}

		@Override
		public void handleMessage(Message voipMessage) {
			VoipStateBundle myState = getStateBundle(voipMessage);
			
			assert( myState.getState()==expectedStates[curStateIndex]);
			curStateIndex++;
			// Register the account after the Lib Initialization
			if (myState.getState()==VoipState.INITIALIZED)   myVoip.registerAccount();	
			// Unregister the account
			else if (myState.getState()==VoipState.REGISTERED)    myVoip.unregisterAccount();	
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myState.getState()==VoipState.UNREGISTERED)  myVoip.destroyLib();
			     
		}
	}
	

	private class MakeCallHandler extends AbstractAppHandler {
	
		private String extension = "1234";
		private VoipState [] expectedStates = { VoipState.INITIALIZED , 
				VoipState.REGISTERING, 
				VoipState.REGISTERED, 
				VoipState.CALL_DIALING,
				VoipState.CALL_ACTIVE,
				VoipState.CALL_HANGUP,
				VoipState.UNREGISTERING,
				VoipState.UNREGISTERED,
				VoipState.DEINITIALIZING,
				VoipState.DEINITIALIZE_DONE};
		
		public MakeCallHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
			//EditText txtView =(EditText) findViewById(R.id.txtExtension);
			//this.extension = txtView.getText().toString();
		}

		@Override
		public void handleMessage(Message voipMessage) {
			VoipStateBundle myState = getStateBundle(voipMessage);
			
			assert( myState.getState()==expectedStates[curStateIndex]);
			curStateIndex++;
			// Register the account after the Lib Initialization
			if (myState.getState()==VoipState.INITIALIZED)   myVoip.registerAccount();	
			else if (myState.getState()==VoipState.REGISTERED)    myVoip.makeCall(extension);	
			else if  (myState.getState()==VoipState.CALL_ACTIVE)    {
				//this.app.waitForSeconds(20);
				//myVoip.hangupCall();
			}
			// Unregister the account
			else if (myState.getState()==VoipState.CALL_HANGUP)    myVoip.unregisterAccount();	
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myState.getState()==VoipState.UNREGISTERED)  myVoip.destroyLib();
			     
		}
	   
	}
	
	private class MakeRemoteCallHandler extends AbstractAppHandler {
		
		private String extension = null;
		
		private VoipState [] expectedStates = { VoipState.INITIALIZED , 
				VoipState.REGISTERING, 
				VoipState.REGISTERED, 
				VoipState.CALL_DIALING,
				VoipState.CALL_ACTIVE,
				VoipState.CALL_HANGUP,
				VoipState.UNREGISTERING,
				VoipState.UNREGISTERED,
				VoipState.DEINITIALIZING,
				VoipState.DEINITIALIZE_DONE};
		
		public MakeRemoteCallHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
			EditText txtView =(EditText) findViewById(R.id.txtExtension);
			this.extension = txtView.getText().toString();
		}

		@Override
		public void handleMessage(Message voipMessage) {
			VoipStateBundle myState = getStateBundle(voipMessage);
			
			assert( myState.getState()==expectedStates[curStateIndex]);
			curStateIndex++;
			// Register the account after the Lib Initialization
			if (myState.getState()==VoipState.INITIALIZED)   myVoip.registerAccount();	
			else if (myState.getState()==VoipState.REGISTERED)    myVoip.makeCall(extension);	
			else if  (myState.getState()==VoipState.CALL_ACTIVE)    {
				//this.app.waitForSeconds(20);
				//myVoip.hangupCall();
			}
			// Unregister the account
			else if (myState.getState()==VoipState.CALL_HANGUP)    myVoip.unregisterAccount();	
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myState.getState()==VoipState.UNREGISTERED)  myVoip.destroyLib();
			     
		}
	   
	}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.waitForInitialization();
        this.initializeGUI();
        
        //this.runExample();
    }
    
    public void doVoipTest(View view) {
    	EditText txtView =(EditText) this.findViewById(R.id.txtServerIp);
    	String serverIp = txtView.getText().toString();
    	InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(txtView.getWindowToken(), 0); 
    	
    	this.runRegistrationExample(serverIp);
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
    
    
    public void runRegistrationExample(String serverIp)
    {
    	
    	
    	this.clearInfoLines();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params

		HashMap<String,String> params = new HashMap<>();
		params.put("sipServerIp",serverIp);  //"156.148.33.223";"192.168.1.83"
		params.put("userName","steand");
		params.put("userPwd","steand");
		//params.put("sipPort","5060"); // optional: default 5060
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
		}
		
		// Initialize the library providing custom initialization params and an handler where
		// to receive event notifications. Following Voip methods are called form the handleMassage() callback method
		//boolean result = myVoip.initLib(params, new RegistrationHandler(this, myVoip));
		boolean result = myVoip.initLib(params, new MakeRemoteCallHandler(this, myVoip));
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
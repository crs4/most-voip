package most.voip.example;
import java.util.ArrayList;
import java.util.HashMap;

import most.voip.api.BuddyInterface;
import most.voip.api.CallState;
import most.voip.api.Utils;
import most.voip.api.VoipLib;
import most.voip.api.VoipLibBackend;
import most.voip.api.VoipMessageType;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;	
import android.widget.TextView;

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server (by specifying its IP address) </li>
 * <li>make a call to a remote user (by specifying an extension</li>
 * <li>unregister the previously registered account from the Sip Server </li>
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
	
	
	private class AnswerCallHandler extends AbstractAppHandler {
	 
		private String buddyExtension = "ste";
		
		public AnswerCallHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
			//EditText txtView =(EditText) findViewById(R.id.txtExtension);
			//this.extension = txtView.getText().toString();
			
		}
		
		

		@Override
		public void handleMessage(Message voipMessage) {
			
			VoipStateBundle myState = getStateBundle(voipMessage);
			Log.d(TAG, "CHIAMATO HANDLE MESSAGE CON MESSAGE TYPE:" + myState.getMsgType());
			Log.d(TAG, "CHIAMATO HANDLE MESSAGE CON VOIP STATE:" + myState.getState());
			updateCallStateInfo();
			if (myState.getMsgType()==VoipMessageType.BUDDY_STATE)
			{
				Log.d(TAG, "In handle Message for BUDDY STATE");
				BuddyInterface myBuddy = (BuddyInterface) myState.getData();
				this.app.addInfoLine("Buddy (" + myBuddy.getUri() + ") ->" + myBuddy.getStatusText());
				updateBuddyStateInfo(myBuddy);
			}
			// Register the account after the Lib Initialization
			if (myState.getState()==VoipState.INITIALIZED)   myVoip.registerAccount();	
			else if (myState.getState()==VoipState.REGISTERED)    {this.app.addInfoLine("Ready to accept calls (adding buddy...)");
			 													   //add a buddy so that we can receive presence notifications from it
			                                                       myVoip.addBuddy(buddyExtension);
			                                                      }														
			else if (myState.getState()==VoipState.CALL_INCOMING)  handleIncomingCall();
			else if  (myState.getState()==VoipState.CALL_ACTIVE)    {
				//this.app.waitForSeconds(20);
				//myVoip.hangupCall();
			}
			// Unregister the account
			else if (myState.getState()==VoipState.CALL_HANGUP)    {    setupButtons(false);
				                                                        myVoip.unregisterAccount();	}
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myState.getState()==VoipState.UNREGISTERED)  myVoip.destroyLib();
			     
		} // end of handleMessage()
	   
	}
     
    private void handleIncomingCall()
    {
    	setupButtons(true);
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
    
    public void answerCall(View view) 
    {
    	this.myVoip.answerCall();
    }
    
    public void hangupCall(View view) 
    {
    	this.myVoip.hangupCall();
    }
    
    public void toggleHoldCall(View view) 
    {
    	if (myVoip==null || myVoip.getCallState()==CallState.IDLE)
    		return;
    	if (myVoip.getCallState()==CallState.ACTIVE)
    	{  Log.d(TAG,"trying to hold the call...");
    		this.myVoip.holdCall();
    	}
    	else if (myVoip.getCallState()==CallState.HOLDING)
    	{   
    		Log.d(TAG,"trying to unhold the call...");
    		this.myVoip.unholdCall();
    	}
    }
    
    
    private void initializeGUI()
    {
    	setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.listOperations);
        this.infoArray = new ArrayList<String>();
        arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(arrayAdapter);
        this.setupButtons(false);
        //this.addInfoLine("Most Voip Lib Test Example 1");
    }
    
    private void waitForInitialization() {
    	/* Wait for GDB to init */
    	Log.d(TAG, "Waiting some second before initializing the lib...");
    	if ((this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
    		this.waitForSeconds(3);
		}
    }
    
    void setupButtons(boolean active)
	{
		Button butAccept = (Button) findViewById(R.id.butAccept);
		butAccept.setEnabled(active);
		
		Button butToogleHold = (Button) findViewById(R.id.butToggleHold);
		butToogleHold.setEnabled(active);
		
		Button butHangup = (Button) findViewById(R.id.butHangup);
		butHangup.setEnabled(active);
	}
    
    public void runExample(String serverIp)
    {
    	this.clearInfoLines();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params

		HashMap<String,String> params = new HashMap<String,String>();
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
		boolean result = myVoip.initLib(params, new AnswerCallHandler(this, myVoip));
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
    
    private void updateCallStateInfo()
    { String callState = "Not available";
    
    	if (this.myVoip!=null)
    	{
    		callState = this.myVoip.getCallState().name();
    	}
    
		TextView labState = (TextView) findViewById(R.id.labCallState);
		labState.setText(callState);
    }
    
    private void updateBuddyStateInfo(BuddyInterface buddy)
    { 
    	TextView labBuddyState = (TextView) findViewById(R.id.labBuddyState);
    	labBuddyState.setText(buddy.getStatusText());
    }
    
    public void addInfoLine(String info)
    {   
    	String callStatus = "N.A";
    	if  (this.myVoip!=null) {
    		Log.d(TAG, "Voip Lib is not NULL: Test with multiple calls!");
    		callStatus = myVoip.getCallState().name();	
    	}
    	
    	String msg = "CallState:(" + callStatus + "):" + info;
    	this.infoArray.add(msg);
    	if (arrayAdapter!=null)
    		arrayAdapter.notifyDataSetChanged();
    }
}
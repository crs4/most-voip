package most.voip.example;
import java.util.ArrayList;
import java.util.HashMap;

import most.voip.api.Utils;
import most.voip.api.VoipLib;
import most.voip.api.VoipLibBackend;
import most.voip.api.VoipEventBundle;
import most.voip.api.enums.BuddyState;
import most.voip.api.enums.CallState;
import most.voip.api.enums.VoipEventType;
import most.voip.api.enums.VoipEvent;
import most.voip.api.interfaces.IBuddy;
import most.voip.api.interfaces.ICall;
import most.voip.example1.R;
import android.app.Activity;
import android.app.Service;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private ArrayList<IBuddy> buddiesArray = null;
	private VoipLib myVoip =  null;
	private ArrayAdapter<String> arrayAdapter = null;
	private ArrayAdapter<IBuddy> buddyArrayAdapter = null;
	private String serverIp = null;
   
	private AnswerCallHandler voipHandler = null; 
	
	private static class AbstractAppHandler extends Handler{
		protected MainActivity app = null;
		protected VoipLib myVoip = null;
		@SuppressWarnings("unused")
		protected  int curStateIndex = 0;
		
 		public AbstractAppHandler(MainActivity app, VoipLib myVoip) {
 			super();
			this.app = app;
			this.myVoip = myVoip;
		}
 		
 		protected VoipEventBundle getEventBundle(Message voipMessage)
 		{
 			//int msg_type = voipMessage.what;
			VoipEventBundle myState = (VoipEventBundle) voipMessage.obj;
			String infoMsg = "State:" + myState.getEvent() + ":" + myState.getInfo();
			Log.d(TAG, "Called handleMessage with state info:" + infoMsg);
			this.app.addInfoLine(infoMsg);
			return myState;
 		}
	}
	
	
	private class AnswerCallHandler extends AbstractAppHandler {
	    
		boolean reinitRequest = false;
		
		public AnswerCallHandler(MainActivity app, VoipLib myVoip) {
			super(app, myVoip);
			//EditText txtView =(EditText) findViewById(R.id.txtExtension);
			//this.extension = txtView.getText().toString();
		}
		
		

		@Override
		public void handleMessage(Message voipMessage) {
		
			if (voipMessage.what == -1) {
				Log.d(TAG,"App exiting request");
				myVoip.destroyLib();
				finish();
				Runtime.getRuntime().gc();
				android.os.Process.killProcess(android.os.Process.myPid());
				return;
			} 

			VoipEventBundle myEventBundle = getEventBundle(voipMessage);
			Log.d(TAG, "HANDLE MESSAGE TYPE:" + myEventBundle.getEventType() + " EVENT:" + myEventBundle.getEvent());
			
			updateCallStateInfo();
			updateServerStateInfo();
			
			
			if (myEventBundle.getEventType()==VoipEventType.BUDDY_EVENT)
			{
				Log.d(TAG, "In handle Message for BUDDY STATE");
				IBuddy myBuddy = (IBuddy) myEventBundle.getData();
				this.app.addInfoLine("Buddy (" + myBuddy.getUri() + ") ->" + myBuddy.getStatusText());
				updateBuddyStateInfo(myBuddy);
			}
			// Register the account after the Lib Initialization
			if (myEventBundle.getEvent()==VoipEvent.LIB_INITIALIZED)   {myVoip.registerAccount();
																			}	
			else if (myEventBundle.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    {
																	this.app.addInfoLine("Ready to accept calls (adding buddy...)");
			 													    //add a buddy so that we can receive presence notifications from it
			                                                        subscribeBuddies();
			                                                      }														
			else if (myEventBundle.getEvent()==VoipEvent.CALL_INCOMING)  handleIncomingCall();
			//else if  (myState.getState()==VoipState.CALL_ACTIVE)    {}
			
			// Unregister the account
			else if (myEventBundle.getEvent()==VoipEvent.CALL_HANGUP)    {    setupButtons(false);
																		ICall callInfo = (ICall) myEventBundle.getData();
																		Log.d(TAG, "Hangup from uri:" + callInfo.getRemoteUri());
																		IBuddy myBuddy = myVoip.getBuddy(callInfo.getRemoteUri());
																		Log.d(TAG, "Current Buddy Status Text:" + myBuddy.getStatusText());
																		updateBuddyStateInfo(myBuddy);
				                                                       // myVoip.unregisterAccount();
				                                                   }
			// Deinitialize the Voip Lib and release all allocated resources
			else if (myEventBundle.getEvent()==VoipEvent.LIB_DEINITIALIZED || myEventBundle.getEvent()==VoipEvent.LIB_DEINITIALIZATION_FAILED) 
			{
				Log.d(TAG,"Setting to null MyVoipLib");
				this.app.myVoip = null;
				
				if (this.reinitRequest)
				{	this.reinitRequest = false;
					this.app.runExample();
				}
			}
			     
		} // end of handleMessage()
	   
	}
    
	private void subscribeBuddies()
	{
		 String buddyExtension = "ste";
		 String buddyExtension2 = "ste2";
		 Log.d(TAG, "adding buddies...");
		 myVoip.addBuddy(getBuddyUri(buddyExtension));
         myVoip.addBuddy(getBuddyUri(buddyExtension2));
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
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 

		case R.id.action_quit:
			if (this.voipHandler!=null)
			{
				Message m = Message.obtain(this.voipHandler, -1);
				m.sendToTarget();
				break;
			}
			else
			{
				Log.d(TAG,"Exiting app");
				finish();
				Runtime.getRuntime().gc();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		default:
			break;
		}

		return true;
	} 	
    
    private String getBuddyUri(String extension)
	{
		return "sip:" + extension + "@" + this.serverIp ;
	}
    
    private boolean isAtleastOneBuddyOnPhone()
	{
		
			IBuddy [] buddies = this.myVoip.getBuddies();
			for (IBuddy buddy : buddies)
			{
				if (buddy.getState()==BuddyState.ON_LINE)
					return true;
			}
		
	 return false;
	}
    
    /**
     * Invoked when the 'Go' button is clicked
     */
    public void doVoipTest(View view) {
    	EditText txtView =(EditText) this.findViewById(R.id.txtServerIp);
    	this.serverIp = txtView.getText().toString();
    	InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(txtView.getWindowToken(), 0); 
    	
    	this.runExample();
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
        ListView buddiesView = (ListView) findViewById(R.id.listBuddies);
        
        this.infoArray = new ArrayList<String>();
        this.arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(arrayAdapter);
        
        this.buddiesArray = new ArrayList<IBuddy>();
        
        this.buddyArrayAdapter = new BuddyArrayAdapter(this, R.layout.buddy_row, this.buddiesArray);
        buddiesView.setAdapter(this.buddyArrayAdapter);

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
    
    
    private HashMap<String,String> buildParams()
    {
    	HashMap<String,String> params = new HashMap<String,String>();
		params.put("sipServerIp",serverIp);  //"156.148.33.223";"192.168.1.83"
		params.put("userName","steand");
		params.put("userPwd","steand");
		String onHoldSoundPath = Utils.getResourcePathByAssetCopy(this.getApplicationContext(), "", "test_hold.wav");
		Log.d(TAG,"OnHoldSoundPath:" + onHoldSoundPath);
		params.put("onHoldSound", onHoldSoundPath);
		//params.put("sipPort","5060"); // optional: default 5060
		return params;
    	
    }
    private void runExample()
    {
    	this.clearInfoLines();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params

		HashMap<String,String> params = buildParams();
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
			this.voipHandler = new AnswerCallHandler(this, myVoip);
			
			// Initialize the library providing custom initialization params and an handler where
			// to receive event notifications. Following Voip methods are called form the handleMassage() callback method
			//boolean result = myVoip.initLib(params, new RegistrationHandler(this, myVoip));
			myVoip.initLib(this.getApplicationContext(), params, this.voipHandler);
		}
		else 
			{
			Log.d(TAG,"Voip is not null... Destroying the lib before reinitializing.....");
			// Reinitialization will be done after deinitialization event callback
			this.voipHandler.reinitRequest = true;
			myVoip.destroyLib();
			}
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
    	this.buddiesArray.clear();
    	if (this.buddyArrayAdapter!=null)
    		this.buddyArrayAdapter.notifyDataSetChanged();
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
    
    private void updateServerStateInfo()
    {
    	TextView labBuddyState = (TextView) findViewById(R.id.labServerState);
    	labBuddyState.setText(myVoip.getServerState().toString());
    }
    
    private void updateBuddyStateInfo(IBuddy buddy)
    { Log.d(TAG, "Called updateBuddyStateInfo on buddy");
    	if (buddy==null)
    	{
    		Log.e(TAG, "Called updateBuddyStateInfo on NULL buddy");
    		return;
    	}
    	
    	Log.d(TAG, "Called updateBuddyStateInfo on buddy:" + buddy.getUri());
    	
    	int buddyPosition = this.buddyArrayAdapter.getPosition(buddy);
    	if (buddyPosition<0)
    	{
    		Log.d(TAG, "Adding buddy to listView!");
    		this.buddiesArray.add(buddy);
    		
    	}
    	else 
    	{
    		Log.d(TAG, "Replacing buddy into the listView!");
    		this.buddiesArray.set(buddyPosition, buddy);
    	}
    	this.buddyArrayAdapter.notifyDataSetChanged();
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
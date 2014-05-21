package most.voip.example;
import java.util.ArrayList;

import most.voip.api.Utils;
import most.voip.api.VoipLib;
import most.voip.api.VoipLibBackend;
import most.voip.api.VoipStateBundle;
import most.voip.example1.R;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity implements Handler.Callback {
	private static final String TAG = "VoipTestActivity";
	private final Handler handler = new Handler(this);
	
	private ArrayList<String> infoArray = null;
	private VoipLib myVoip =  null;
	private ArrayAdapter<String> arrayAdapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeGUI();
        this.runTest();
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
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
		}
    }
    private void runTest()
    {
    	this.waitForInitialization();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization
		String params = "{ 'SipTransport': 'UDP' }";
		
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
		}
		
		boolean result = myVoip.initialize(null, handler);
		
		// Account Registration
		
		Log.d(TAG, "Registering the account..");
		result = myVoip.registerAccount();
		//this.addInfoLine("Voip Lib account registration success ? " + String.valueOf(result));
		//this.waitForInitialization();
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
		VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
		String infoMsg = "State:" + myState.getState() + ":" + myState.getInfo();
		Log.d(TAG, "State info:" + infoMsg);
		this.addInfoLine(infoMsg);
		return false;
	}
    
}
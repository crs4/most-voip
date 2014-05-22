package most.voip.example;
import java.util.ArrayList;
import java.util.HashMap;

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
        this.runExample();
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
    
    
    public void runExample()
    {
    	this.waitForInitialization();
    	this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));
		
    	// Voip Lib Initialization Params

		HashMap<String,String> params = new HashMap<>();
		params.put("sipServerIp","192.168.1.83");  //"156.148.33.223";
		params.put("userName","ste");
		params.put("userPwd","ste");
		//params.put("sipPort","5060"); // optional: default 5060
		
		Log.d(TAG, "Initializing the lib...");
		if (myVoip==null)
		{
			Log.d(TAG,"Voip null... Initialization.....");
			myVoip = new  VoipLibBackend();
		}
		
		// Initialize the library providing custom initialization params and an handler where
		// to receive event notifications
		boolean result = myVoip.initialize(params, handler);
		
		// Register the account
		Log.d(TAG, "Registering the account..");
		result = myVoip.registerAccount();
		
		// Wait some seconds before unregistering the account
		this.waitForSeconds(3);
		
		// Unregister the account
		myVoip.unregisterAccount();
		
		// Wait some seconds before destroying the Voip Lib
		 this.waitForSeconds(3);
		
		// Deinitialize the Voip Lib and release all allocated resources
		myVoip.destroy();
		
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
		VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
		String infoMsg = "State:" + myState.getState() + ":" + myState.getInfo();
		Log.d(TAG, "State info:" + infoMsg);
		this.addInfoLine(infoMsg);
		return false;
	}
    
}
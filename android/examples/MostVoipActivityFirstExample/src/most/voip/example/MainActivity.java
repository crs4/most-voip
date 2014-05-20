package most.voip.example;
import java.util.ArrayList;

import most.voip.api.VoipLib;
import most.voip.api.VoipLibBackend;

import com.example.mostvoipactivityfirstexample.R;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "VoipTestActivity";
	private ArrayList<String> infoArray = null;
	private VoipLib myVoip =  null;
	
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
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(arrayAdapter);
        this.addInfoLine("Most Voip Lib Test Example 1");
    }
    
    private void waitForInitialization() {
    	/* Wait for GDB to init */
    	Log.d(TAG, "Waiting some second before initializing the lib...");
    	if ((this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		}
    }
    private void runTest()
    {
    	this.waitForInitialization();
    	
		// Voip Lib Initialization
		String params = "{ 'SipTransport': 'UDP' }";
		
		
		Log.d(TAG, "Initializing the lib...");
		myVoip = new  VoipLibBackend();
		boolean result = myVoip.initialize(null);
		this.addInfoLine("Voip Lib initialization success ? " + String.valueOf(result));
		
		// Account Registration
		
		Log.d(TAG, "Registering the account..");
		result = myVoip.registerAccount();
		this.addInfoLine("Voip Lib account registration success ? " + String.valueOf(result));
		this.waitForInitialization();
		
    }
    
    
    private void addInfoLine(String info)
    {
    	this.infoArray.add(info);
    }
    
}
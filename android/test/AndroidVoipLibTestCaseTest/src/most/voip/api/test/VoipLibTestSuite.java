package most.voip.api.test;

import junit.framework.TestCase;
import most.voip.api.*;
import android.os.*;
import android.util.Log;


public class VoipLibTestSuite extends TestCase implements Handler.Callback {
    
	private static final String TAG = "VoipTestActivity";
	
	private VoipState [] expectedStates = { VoipState.INITIALIZED , 
											VoipState.REGISTERING, 
											VoipState.REGISTERED, 
											VoipState.UNREGISTERING,
											VoipState.UNREGISTERED,
											VoipState.DEINITIALIZING,
											VoipState.DEINITIALIZE_DONE};
	
	private int curStateIndex = 0;
	
	private VoipLib myVoip =null;
	private final Handler handler = new Handler(this);
	
	
	public VoipLibTestSuite(String name) {
		super(name);
	}
   
	protected void setUp() throws Exception {
		myVoip = new MockVoipLib();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 *  This test calls the initialize() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipState.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipState.
	 */
	public void testVoip() {
		boolean result = myVoip.initLib(null, this.handler);
		Log.d(TAG,"testVoip...");
		assertTrue(result);
		
		while (curStateIndex<expectedStates.length)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d(TAG,"Test End");
	}

	@Override
	public boolean handleMessage(Message voipMessage) {
		//int msg_type = voipMessage.what;
		Log.d(TAG, "Called handleMessage with info...");
		VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
		String infoMsg = myState.getState() + ":" + myState.getInfo();
		Log.d(TAG, "Current State:" + infoMsg);
		
		assertEquals( myState.getState(), expectedStates[curStateIndex]);
		curStateIndex++;
		     if (myState.getState()==VoipState.INITIALIZED)   assertTrue(myVoip.registerAccount());	
		else if (myState.getState()==VoipState.REGISTERED)    assertTrue(myVoip.unregisterAccount());	
		else if (myState.getState()==VoipState.UNREGISTERED)  assertTrue(myVoip.destroyLib());

		return false;
	}

}

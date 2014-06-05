package most.voip.api.test;

import java.util.HashMap;

import most.voip.api.VoipLib;
import most.voip.api.VoipMessageType;
import most.voip.api.VoipState;
import most.voip.api.VoipStateBundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MockVoipLib implements VoipLib{
	
	private static final String TAG = "VoipLibMock";
	private Handler notificationHandler =null;
	
	private void notifyState(VoipStateBundle myStateBundle)
    {
		Log.d(TAG, "Called notifyState for state:" + myStateBundle.getState().name());
    	Message m = Message.obtain(this.notificationHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
		m.sendToTarget();
    }

	@Override
	public boolean initLib(HashMap<String, String> configParams,
			Handler notificationHandler) {
		Log.d(TAG, "Called initLib");
		this.notificationHandler = notificationHandler;
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.INITIALIZED, "Inizialization Ok", null));
		return true;
	}

	@Override
	public boolean destroyLib() {
		Log.d(TAG, "Called destroyLib");
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZING, "Voip Lib destroying...", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZE_DONE, "Voip Lib destroyed", null));
		return true;
	}
    
	private void simulatePause(int secs)
	{
//		try {
//			Thread.sleep(secs*1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	@Override
	public boolean registerAccount() {
		Log.d(TAG, "Called registerAccount");
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERING, "Account Registration request sent", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERED, "Account Registered", null));
		return true;
	}

	@Override
	public boolean unregisterAccount() {
		Log.d(TAG, "Called unregisterAccount");
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERING, "Account Unregistration request sent", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERED, "Account Unregistered", null));
		return true;
	}

	@Override
	public void makeCall(String extension) {
		Log.d(TAG, "Called makeCall");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void answerCall() {
		Log.d(TAG, "Called answerCall");
		
	}

	@Override
	public void holdCall() {
		Log.d(TAG, "Called holdCall");
		
	}

	@Override
	public void unholdCall() {
		Log.d(TAG, "Called unholdCall");
		
	}

	@Override
	public void hangupCall() {
		Log.d(TAG, "Called hangupCall");
		
	}

}

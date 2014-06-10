package most.voip.api.test;

import java.util.HashMap;

import most.voip.api.CallState;
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
	private CallState currentCallState = CallState.NONE;
	
	private void notifyState(VoipStateBundle myStateBundle)
    {
		Log.d(TAG, "Called notifyState for state:" + myStateBundle.getState().name());
		switch (myStateBundle.getState()){
			case CALL_ACTIVE: this.currentCallState = CallState.ACTIVE; break;
			case CALL_DIALING: this.currentCallState = CallState.DIALING; break;
			case CALL_INCOMING: this.currentCallState = CallState.INCOMING; break;
			case CALL_HOLDING: this.currentCallState = CallState.HOLDING; break;
			case CALL_REMOTE_HOLDING: this.currentCallState = CallState.REMOTE_HOLDING; break;
			case CALL_UNHOLDING: this.currentCallState = CallState.ACTIVE; break; // da gestire il caso di holding e remote holding...
			case CALL_HANGUP: this.currentCallState = CallState.NONE; break;
			case CALL_REMOTE_HANGUP: this.currentCallState = CallState.NONE; break;
		default:
			break;}
				
    	Message m = Message.obtain(this.notificationHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
		m.sendToTarget();
    }

	@Override
	public boolean initLib(HashMap<String, String> configParams,
			Handler notificationHandler) {
		Log.d(TAG, "Called initLib");
		this.currentCallState = CallState.NONE;
		this.notificationHandler = notificationHandler;
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.INITIALIZED, "Inizialization Ok", null));
		return true;
	}

	@Override
	public boolean destroyLib() {
		this.currentCallState = CallState.NONE;
		Log.d(TAG, "Called destroyLib");
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZING, "Voip Lib destroying...", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZE_DONE, "Voip Lib destroyed", null));
		return true;
	}
    
	private void simulatePause(int secs)
	{
		try {
			Thread.sleep(secs*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public boolean makeCall(String extension) {
		Log.d(TAG, "Called makeCall");
		{
		this.currentCallState = CallState.DIALING;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_DIALING, "Dialing call to:" + extension, null));
		this.simulatePause(1);
		this.currentCallState = CallState.ACTIVE;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_ACTIVE, "Call active with:" + extension, null));
		}
		return true;
	}

	@Override
	public boolean answerCall() {
		Log.d(TAG, "Called answerCall");
		this.currentCallState = CallState.ACTIVE;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_ACTIVE, "Call active after answering", null));
		return true;
	}

	@Override
	public boolean holdCall() {
		Log.d(TAG, "Called holdCall");
		this.currentCallState = CallState.HOLDING;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_HOLDING, "Call holding", null));
		return true;
	}

	@Override
	public boolean unholdCall() {
		Log.d(TAG, "Called unholdCall");
		this.currentCallState = CallState.ACTIVE;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_UNHOLDING, "Call unholding", null));
		return true;
	}

	@Override
	public boolean hangupCall() {
		this.currentCallState = CallState.NONE;
		notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_HANGUP, "Call hangup" , null));
		return true;
	}

	@Override
	public CallState getCallState() {
		return this.currentCallState;
	}
  
}

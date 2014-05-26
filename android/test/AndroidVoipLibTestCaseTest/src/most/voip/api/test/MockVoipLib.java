package most.voip.api.test;

import java.util.HashMap;

import most.voip.api.VoipLib;
import most.voip.api.VoipMessageType;
import most.voip.api.VoipState;
import most.voip.api.VoipStateBundle;
import android.os.Handler;
import android.os.Message;

public class MockVoipLib implements VoipLib{
	
	private Handler notificationHandler =null;
	
	private void notifyState(VoipStateBundle myStateBundle)
    {
    	Message m = Message.obtain(this.notificationHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
		m.sendToTarget();
    }

	@Override
	public boolean initialize(HashMap<String, String> configParams,
			Handler notificationHandler) {
		this.notificationHandler = notificationHandler;
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.INITIALIZED, "Inizialization Ok", null));
		return true;
	}

	@Override
	public boolean destroy() {
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
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERING, "Account Registration request sent", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERED, "Account Registered", null));
		return true;
	}

	@Override
	public boolean unregisterAccount() {
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERING, "Account Unregistration request sent", null));
		this.simulatePause(1);
		this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERED, "Account Unregistered", null));
		return true;
	}

	@Override
	public void makeCall(String extension) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void answerCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void holdCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unholdCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hangupCall() {
		// TODO Auto-generated method stub
		
	}

}

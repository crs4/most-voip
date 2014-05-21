package most.voip.api;

import android.os.Handler;

public interface VoipLib {
	
	public boolean initialize(String configParams, Handler notificationHandler);
	public boolean destroy();
   
 
	public boolean registerAccount();
	public boolean unregisterAccount();
       
    
    public void makeCall(String extension);
    
    public void answerCall();
    public void holdCall();
    public void unholdCall();
    public void hangupCall();
       
}

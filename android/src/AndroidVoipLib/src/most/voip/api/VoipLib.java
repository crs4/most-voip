package most.voip.api;

public interface VoipLib {
	
	public boolean initialize(String configParams);
	public boolean destroy();
   
 
	public boolean registerAccount();
	public boolean unregisterAccount();
       
    
    public void makeCall(String extension);
    
    public void answerCall();
    public void holdCall();
    public void unholdCall();
    public void hungupCall();
       
}

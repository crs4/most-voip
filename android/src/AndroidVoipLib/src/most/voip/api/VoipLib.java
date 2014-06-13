package most.voip.api;

import java.util.HashMap;

import android.os.Handler;

public interface VoipLib {
	
	/**
	 * Initialize the Voip Lib
	 * @param configParams All needed configuration string params. All the supported parameters are the following:
	 * <ul>
	 * <li>serverIp: the ip address of the Sip Server (e.g Asterisk)</li>
	 * <li>userName: the account name of the peer to register to the sip server </li>
	 * <li>userPwd: the account password of the peer to register to the sip server </li>
	 * <li>sipPort: the port of the sip server (default:"5060") </li>
	 * </ul>
	 * 
	 * @param notificationHandler
	 * @return
	 */
	public boolean initLib(HashMap<String,String> configParams, Handler notificationHandler);
	
	/**
	 * Destroy the Voip Lib
	 * @return <code>true</code> if no error occurred in the deinitialization process
	 */
	public boolean destroyLib();
   
    /**
     * Register the account according to the configuration params provided in the {@link #initLib(HashMap, Handler)} method
     * @return <code>true</code> if the registration request was sent to the sip server, <code>false</code> otherwise
     */
	public boolean registerAccount();
	
	/**
	 * Unregister the currently registered account [Not Implemented yet]
	 * @return <code>true</code> if the unregistration request was sent to the sip server, <code>false</code> otherwise
	 */
	public boolean unregisterAccount();
       
    /**
     * Make a call to the specific extension
     * 
     * @param extension The extension to dial
     * @return true if no error occurred during this operation, false otherwise
     */
    public boolean makeCall(String extension);
    
    /**
     * Answer a call
     * @return false if this command was ignored for some reasons (e.g there is already an active call), true otherwise
     */
    public boolean answerCall();
    
    /**
     * Put the active call on hold status
     * @return true if no error occurred during this operation, false otherwise
     */
    public boolean holdCall();
    
    /**
     *  Put the active call on active status
     *  @return true if no error occurred during this operation, false otherwise
     */
    public boolean unholdCall();
    
    /**
     * Close the current active call
     * @return true if no error occurred during this operation, false otherwise
     */
    public boolean hangupCall();
     
    /**
     * Get the state of the current call (if any)
     * @return the state of the current call (if any), CallState.NONE if there is no call
     */
    public CallState getCallState();
    
    /**
     * Add a buddy to this account.
     * @param extension the buddy extension
     * @return True if the buddy was added to the buddy list, False otherwise
     */
    public boolean addBuddy(String extension);
    
    /**
     * Remove the buddy from this account
     * @param extension The extension of the buddy to remove
     * @return True if the buddy was found and it saw successfully removed, False otherwise
     */
    public boolean removeBuddy(String extension);
    
    /**
     * Get the current state of the buddy with the given extension, or null if it is not found
     * @param extension the buddy extension
     * @return  the current state of the buddy with the provided extension, or null if it is not found
     */
    public BuddyState getBuddyState(String extension);
}

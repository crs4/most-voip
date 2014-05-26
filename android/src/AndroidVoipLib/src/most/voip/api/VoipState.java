package most.voip.api;


public enum VoipState {
	
  INITIALIZE,
  INITIALIZED,
  INITIALIZE_FAILED,
  CONNECTING,
  CONNECTED,
  CONNECTION_FAILED,
  REGISTERING ,
  UNREGISTERING ,
  REGISTERED ,
  UNREGISTERED ,
  REGISTRATION_FAILED,
  UNREGISTRATION_FAILED,
  REMOTE_USER_SUBSCRIBING ,
  REMOTE_USER_SUBSCRIBING_FAILED,
  REMOTE_USER_SUBSCRIBED,
  REMOTE_USER_CONNECTED ,
  REMOTE_USER_DISCONNECTED,
  CALL_DIALING ,   
  CALL_ACTIVE,
  CALL_HOLDING,
  CALL_REMOTE_HOLDING,
  CALL_UNHOLDING,
  CALL_REMTOTE_HANGUP ,
  REMOTE_DISCONNECTION_HANGUP,
  EXITING_DONE,
  DEINITIALIZING,
  DEINITIALIZE_DONE,
  DEINITIALIZE_FAILED
}
    /*
   
  public static final String NULL = NAME +"__NULL";
  public static final String INITIALIAZE =  NAME +"__INITIALIZE";
  public static final String INITIALIAZED = NAME +"__INITIALIZED";
  public static final String INITIALIZE_FAILED  = NAME +"__INITIALIZE_FAILED";
  public static final String CONNECTING = NAME +"__CONNECTING";
  public static final String CONNECTED = NAME +"__CONNECTED";
  public static final String CONNECTION_FAILED = NAME +"__CONNECTION_FAILED";
  public static final String REGISTERING = NAME +"__REGISTERING";
  public static final String REGISTERED = NAME +"__REGISTERED";
  public static final String UNREGISTERED = NAME +"__UNREGISTERED";
  public static final String REGISTRATION_FAILED = NAME +"_REGISTRATION_FAILED";
  public static final String REMOTE_USER_SUBSCRIBING = NAME +"__REMOTE_USER_SUBSCRIBING";
  public static final String REMOTE_USER_SUBSCRIBING_FAILED = NAME +"__REMOTE_USER_SUBSCRIBING_FAILED";
  public static final String REMOTE_USER_SUBSCRIBED = NAME +"__REMOTE_USER_SUBSCRIBED";
  public static final String REMOTE_USER_CONNECTED = NAME +"__REMOTE_USER_CONNECTED";
  public static final String REMOTE_USER_DISCONNECTED = NAME +"__REMOTE_USER_DISCONNECTED";
  public static final String CALL_DIALING = NAME +"__CALL_DIALING";
  public static final String CALL_ACTIVE = NAME +"__CALL_ACTIVE";
  public static final String CALL_HOLDING = NAME +"__CALL_HOLDING";
  public static final String CALL_REMOTE_HOLDING = NAME +"__CALL_REMOTE_HOLDING";
  public static final String CALL_UNHOLDING = NAME +"__CALL_UNHOLDING";
  public static final String CALL_REMTOTE_HANGUP = NAME +"__CALL_HANGUP";
  public static final String REMOTE_DISCONNECTION_HANGUP = NAME +"__REMOTE_DISCONNECTION_HANGUP";
  public static final String EXITING_DONE = NAME +"__EXITING_DONE";
  public static final String DEINITIALIZE_DONE = NAME +"__DEINITIALIZE_DONE";
    RemoteDisconnectionHungup     = '%s__CALL_REMOTE_DISCONNECTION_HUNGUP' % Name
    ExitingDone                   = '%s__EXITING_DONE' % Name
    DeinitializeDone              = '%s__DEINITIALIZE_DONE' % Name
    */

package most.voip.api;

public enum CallState {
	  NONE,   		  // No call 
      DIALING ,   	  // The outcoming call is ringing
	  INCOMING,       // The incoming call is ringing
	  ACTIVE,    	  // The call is active
	  HOLDING,   	  // The call has been put in holding state by the local user
	  REMOTE_HOLDING   // The call has been put in holding state by the remote  user
}

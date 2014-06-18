package most.voip.api.enums;

public enum CallState {
	  IDLE,   		  // No call 
      DIALING ,   	  // The outcoming call is ringing
	  INCOMING,       // The incoming call is ringing
	  ACTIVE,    	  // The call is active 
	  HOLDING   	  // The call has been put in holding state by the local user
}

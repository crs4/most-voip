package most.voip.api;

public enum VoipMessageType {
     LIB_STATE,  // voip library  (de)init
	 ACCOUNT_STATE, // account (un)registration
	 CALL_STATE, // incoming, dialing, active, (un)holding, hanging up CALL
	 BUDDY_STATE // buddy presence notification 
}

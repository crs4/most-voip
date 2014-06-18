package most.voip.api.enums;

public enum VoipEventType {
     LIB_EVENT,  // voip library  (de)init
	 ACCOUNT_EVENT, // account (un)registration
	 CALL_EVENT, // incoming, dialing, active, (un)holding, hanging up CALL
	 BUDDY_EVENT // buddy presence notification 
}

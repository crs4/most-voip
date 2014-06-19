package most.voip.api.enums;

public enum VoipEventType {
     LIB_EVENT,  // voip library general states: (de) init
	 ACCOUNT_EVENT, // account (un)registration
	 CALL_EVENT, // incoming, dialing, active, (un)holding, hanging up CALL
	 BUDDY_EVENT // buddy presence notification: remote (un)holding
}

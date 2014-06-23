package most.voip.api.enums;

public enum VoipEventType {
     LIB_EVENT,  // voip library general states: (de) init, server disconnection
	 ACCOUNT_EVENT, // account (un)registration
	 CALL_EVENT, // incoming, dialing, active, (un)holding, hanging up CALL
	 BUDDY_EVENT // buddy presence notification: (un)subsscribing, (dis)connection,  remote (un)holding
}

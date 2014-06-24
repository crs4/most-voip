package most.voip.api.interfaces;

import most.voip.api.enums.CallState;

public interface ICall {
	String getRemoteUri();
	String getLocalUri();
	CallState getState();
}

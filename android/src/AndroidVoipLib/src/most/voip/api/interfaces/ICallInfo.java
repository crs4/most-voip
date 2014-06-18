package most.voip.api.interfaces;

import most.voip.api.enums.CallState;

public interface ICallInfo {
	String getRemoteUri();
	String getLocalUri();
}

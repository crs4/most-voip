package most.voip.api;

import most.voip.api.enums.VoipEvent;
import most.voip.api.enums.VoipEventType;


public class VoipEventBundle {

	 
	private VoipEventType msgType = null;
	private VoipEvent event = null;
	private String info = null;
	private Object data = null;
	
	public VoipEventBundle(VoipEventType msgType, VoipEvent event, String info, Object data)
	{
		this.msgType = msgType;
		this.event = event;
		this.info = info;
		this.data = data;
	}

	public VoipEventType getMsgType() {
		return msgType;
	}

	public VoipEvent getEvent() {
		return event;
	}

	public String getInfo() {
		return info;
	}

	public Object getData() {
		return data;
	}
}

package most.voip.api;


public class VoipStateBundle {

	 
	private VoipMessageType msgType = null;
	private VoipState state = null;
	private String info = null;
	private Object data = null;
	
	public VoipStateBundle(VoipMessageType msgType, VoipState state, String info, Object data)
	{
		this.msgType = msgType;
		this.state = state;
		this.info = info;
		this.data = data;
	}

	public VoipMessageType getMsgType() {
		return msgType;
	}

	public VoipState getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

	public Object getData() {
		return data;
	}
}

package most.voip.api;

public class AccountInfo {

	private String username;
	private String pwd;
	private String transport;

	public String getUsername() {
		return username;
	}

	public String getPwd() {
		return pwd;
	}

	public String getTransport() {
		return transport;
	}

	public AccountInfo(String username, String pwd, String transport)
	{
		this.username = username;
		this.pwd = pwd;
		this.transport = transport;
	}
}

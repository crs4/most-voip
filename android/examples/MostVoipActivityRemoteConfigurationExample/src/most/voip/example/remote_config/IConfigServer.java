package most.voip.example.remote_config;

 
import org.json.JSONObject;

public interface IConfigServer {

	/***
	 * query the remote server for retrieving the configuration params of all available accounts
	 * @return a list of configuration params, one for each account
	 */
	JSONObject getAccountsConfig();
	
	/**
	 * query the remote server for retrieving the configuration params of the specified account
	 * @param accountId the id of the account
	 * @return the configuration params of the specified account
	 */
	JSONObject getAccountConfig(int accountId);
	
	/**
	 * query the remote server for retrieving the configuration params of the buddies of the specified account
	 * @param accountId he id of the account
	 * @return a list of configuration params, one for each buddy
	 */
	JSONObject getBuddiesConfig(int accountId);
}
	
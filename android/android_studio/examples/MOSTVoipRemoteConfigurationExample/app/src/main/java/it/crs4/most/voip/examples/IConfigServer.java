/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */


package it.crs4.most.voip.examples;

 
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
	
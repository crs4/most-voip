/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package most.voip.api.interfaces;

import most.voip.api.enums.AccountState;

public interface IAccount {
public String getUri();
public AccountState getState();

/**
 * Add a buddy to this account.
 * @param uri the buddy sip uri
 * @return True if the buddy was added to the buddy list, False otherwise
 */
public boolean addBuddy(String uri);

/**
 * Remove the buddy from this account
 * @param uri The sip uri of the buddy to remove
 * @return True if the buddy was found and it was successfully removed, False otherwise
 */
public boolean removeBuddy(String uri);

/**
 * Get  the buddy with the given extension, or null if it is not found
 * @param uri the buddy uri
 * @return  the buddy with the provided uri, or null if it is not found
 */
public IBuddy getBuddy(String uri);

/**
 * Get the list of buddies of the current registered account
 * @return the list of the buddies of the currently registered account
 */
public IBuddy [] getBuddies();
}

 

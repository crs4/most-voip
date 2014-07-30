/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package most.voip.api.enums;

public enum VoipEventType {
     LIB_EVENT,  // voip library general states: (de) init, server disconnection
	 ACCOUNT_EVENT, // account (un)registration
	 CALL_EVENT, // incoming, dialing, active, (un)holding, hanging up CALL
	 BUDDY_EVENT // buddy presence notification: (un)subsscribing, (dis)connection,  remote (un)holding
}

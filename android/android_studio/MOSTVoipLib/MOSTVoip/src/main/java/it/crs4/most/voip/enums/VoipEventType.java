/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.enums;

public enum VoipEventType {
    /**
     * Voip Library Events (Voip (de)initialization)
     */
    LIB_EVENT,
    /**
     * Voip Account Events ((un)registration)
     */
    ACCOUNT_EVENT,
    /**
     * Voip Call Events (incoming, dialing, active, (un)holding, hanging up)
     */
    CALL_EVENT,
    /**
     * Voip Buddy Events (buddy presence notification: (un)subsscribing, (dis)connection,  remote (un)holding)
     */
    BUDDY_EVENT
}

/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.enums;

public enum CallState {
    /**
     * No call
     */
    IDLE,
    /**
     * An outcoming call is ringing
     */
    DIALING,
    /**
     * The incoming call is ringing
     */
    INCOMING,
    /**
     * The call is active
     */
    ACTIVE,
    /**
     * The call is on hold state
     */
    HOLDING
}

/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.interfaces;

import it.crs4.most.voip.enums.BuddyState;


/**
 * An IBuddy is a remote Sip user that notify its presence status to sip accounts ({@link IAccount} objects) that are interested to get informations by them.
 */
public interface IBuddy {

    /**
     * get the current state of this buddy
     *
     * @return the current state of this buddy
     * @see IBuddy#refreshStatus()
     */
    BuddyState getState();

    /**
     * get the sip uri of this buddy
     *
     * @return the sip uri of this buddy
     */
    String getUri();

    /**
     * get a textual description of the current status of this buddy
     *
     * @return a textual description of the current status of this buddy
     */
    String getStatusText();

    /**
     * get the sip extension of this buddy
     *
     * @return the sip extension of this buddy
     */
    String getExtension();

    /**
     * Refreshes the current status of this buddy
     */
    void refreshStatus();
}

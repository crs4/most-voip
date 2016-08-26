/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.interfaces;

import it.crs4.most.voip.enums.CallState;

/**
 * Contains informations about a call between 2 sip accounts.
 */
public interface ICall {
    /**
     * get the uri of the remote sip account
     *
     * @return the uri of the remote sip account
     */
    String getRemoteUri();

    /**
     * get the uri of the local sip account
     *
     * @return the uri of the local sip account
     */
    String getLocalUri();

    /**
     * get the current state of this call
     *
     * @return the current state of this call
     */
    CallState getState();
}

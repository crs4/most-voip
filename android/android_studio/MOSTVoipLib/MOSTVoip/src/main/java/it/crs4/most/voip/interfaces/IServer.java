/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.interfaces;

import it.crs4.most.voip.enums.ServerState;

/**
 * Contains informations about the remote Sip Server (e.g Asterisk)
 */
public interface IServer {

    /**
     * get the current status of the sip server (see :class:`it.crs4.most.voip.constants.ServerState`)
     *
     * @return the current status of the sip server
     */
    ServerState getState();

    /**
     * get the ip address of the remote sip server
     *
     * @return the ip address of the remote sip server
     */
    String getIp();

    /**
     * get the port of the remote sip server
     *
     * @return the ip address of the remote sip server
     */
    String getPort();

}

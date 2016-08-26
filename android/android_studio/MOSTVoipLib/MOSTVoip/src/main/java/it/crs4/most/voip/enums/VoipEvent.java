/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.enums;

/**
 * Contains all events triggered by the library
 */
public enum VoipEvent {
    /**
     * The library is under initilization process
     */
    LIB_INITIALIZING,
    /**
     * The lib was successully initialied
     */
    LIB_INITIALIZED,
    /**
     * The library initialization process failed for some reason (e.g authentication failed)
     */
    LIB_INITIALIZATION_FAILED,
    /**
     * The library is under deinitilization process
     */
    LIB_DEINITIALIZING,
    /**
     * The lib was successully deinitialied
     */
    LIB_DEINITIALIZED,
    /**
     * The library deinitialization process failed for some reason (e.g authentication failed)
     */
    LIB_DEINITIALIZATION_FAILED,

    /**
     * The connection to the remote Sip Server failed (a Timeout occurred during account an registration request tothe remote Sip Server)
     */
    LIB_CONNECTION_FAILED,
    //CONNECTING,   // server connection is checked during each account registration renewal
    //CONNECTED,

    /**
     * The Sip user is under registration process (this event triggered only for explicit registration requests, so it is no called during automatic registration renewals)
     */
    ACCOUNT_REGISTERING,
    /**
     * The Sip user is under unregistration process
     */
    ACCOUNT_UNREGISTERING,
    /**
     * The sip user has been successfully registered to the remote Sip Server (this event is also triggered called for each registration renewal)
     */
    ACCOUNT_REGISTERED,
    /**
     * The sip user has been successfully unregistered
     */
    ACCOUNT_UNREGISTERED,
    /**
     * The User Account Registration process failed for some reason (e.g authentication failed)
     */
    ACCOUNT_REGISTRATION_FAILED,

    /**
     * The User Account Unregistration process failed for some reason (e.g the sip server is down)
     */
    ACCOUNT_UNREGISTRATION_FAILED,

    /**
     * a remote user is under subscrition process
     */
    BUDDY_SUBSCRIBING, // ex REMOTE_USER_SUBSCRIBING
    /**
     * The remote user subscription process failed for some reason
     */
    BUDDY_SUBSCRIPTION_FAILED,
    /**
     * The remote user has been successfully subscribed (it is now possible to get status notifications about it)
     */
    BUDDY_SUBSCRIBED, // ex REMOTE_USER_SUBSCRIBED
    /**
     * The remote user is connected (i.e is in ON LINE status)
     */
    BUDDY_CONNECTED,
    /**
     * The remote user is no longer connected (i.e is in OFF LINE status)
     */
    BUDDY_DISCONNECTED,
    /**
     * The remote user is still connected, but it is not available at the moment (it is in BUSY state)
     */
    BUDDY_HOLDING,  // ex CALL_REMOTE_HOLDING

    /**
     * an outcoming call is ringing
     */
    CALL_DIALING,
    /**
     * an incoming call is ringing
     */
    CALL_INCOMING,
    /**
     * a new call is ready to become active or rejected
     */
    CALL_READY,
    //CALL_INCOMING_REJECTED, // not triggered yet
    /**
     * The call is active
     */
    CALL_ACTIVE,
    /**
     * The local user puts on hold the call
     */
    CALL_HOLDING,
    /**
     * The local user unholds the call
     */
    CALL_UNHOLDING,
    /**
     * The local user hangs up
     */
    CALL_HANGUP,
    /**
     * The remote user hangs up
     */
    CALL_REMOTE_HANGUP,
    /**
     * The remote server has been disconnected so the call was interrupted.
     */
    CALL_REMOTE_DISCONNECTION_HANGUP

}
   

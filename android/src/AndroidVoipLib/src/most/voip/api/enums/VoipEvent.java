/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package most.voip.api.enums;

/**
 * Contains all events triggered by the library
 * 
 */
public enum VoipEvent {
	
  LIB_INITIALIZING,   
  LIB_INITIALIZED,
  LIB_INITIALIZATION_FAILED,
  LIB_DEINITIALIZING,
  LIB_DEINITIALIZED,
  LIB_DEINITIALIZATION_FAILED,
  LIB_CONNECTION_FAILED,  // TIMEOUT during account registration request on the remote Sip Server
  //CONNECTING,   // server connection is checked during each account registration renewal
  //CONNECTED,
  
  ACCOUNT_REGISTERING,    // called only for explicit registration requests (no called during automatic registration renewals)
  ACCOUNT_UNREGISTERING,
  ACCOUNT_REGISTERED,     // called also for each registration renewal
  ACCOUNT_UNREGISTERED ,
  ACCOUNT_REGISTRATION_FAILED,
  ACCOUNT_UNREGISTRATION_FAILED,
  
  BUDDY_SUBSCRIBING , // ex REMOTE_USER_SUBSCRIBING
  BUDDY_SUBSCRIPTION_FAILED,  //ex  REMOTE_USER_SUBSCRIBING_FAILED
  BUDDY_SUBSCRIBED, // ex REMOTE_USER_SUBSCRIBED
  BUDDY_CONNECTED , // ex REMOTE_USER_CONNECTED
  BUDDY_DISCONNECTED, // ex REMOTE_USER_DISCONNECTED
  BUDDY_HOLDING,  // ex CALL_REMOTE_HOLDING
  
  CALL_DIALING,   // outcoming call is ringing
  CALL_INCOMING,  // incoming call is ringing
  CALL_INCOMING_REJECTED, // not triggered yet
  CALL_ACTIVE,
  CALL_HOLDING, // local user puts on hold the call
  CALL_UNHOLDING,  // local user resumes the call on active state
  CALL_HANGUP,
  CALL_REMOTE_HANGUP, // not triggered yet
  CALL_REMOTE_DISCONNECTION_HANGUP  // not triggered yet
}
   

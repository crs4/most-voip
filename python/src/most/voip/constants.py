#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

"""
Most-Voip Constants
"""

class VoipEvent(object):
    """
    This class contains all events triggered by the library
    """

    Name                    = 'VOIP_EVENT'

    Null                           = '%s__NULL' % Name
    LIB_INITIALIZING                    = '%s__LIB_INITIALIZING' % Name        # VOIP NO YET INITIALIZED
    LIB_INITIALIZED                = '%s__LIB_INITIALIZED' % Name              # VOIP INITIALIZED
    LIB_INITIALIZATION_FAILED      = '%s__LIB_INITIALIZATION_FAILED' % Name    # VOIP INITIALIZATION FAILED
    #Connecting                    = '%s__CONNECTING' % Name                   # CONNECTING TO SIP SERVER
    #Connected                     = '%s__CONNECTED' % Name                    # SIP SERVER ON LINE
    LIB_CONNECTION_FAILED             = '%s__LIB_CONNECTION_FAILED' % Name     # SERVER CONNECTION TIMEOUT
    LIB_DEINITIALIZING             = '%s__LIB_DEINITIALIZING' % Name
    LIB_DEINITIALIZED              = '%s__LIB_DEINITIALIZED' % Name
    LIB_DEINITIALIZATION_FAILED    = '%s__LIB_DEINITIALIZATION_FAILED' % Name
    
    ACCOUNT_REGISTERING            = '%s__ACCOUNT_REGISTERING' % Name                   # REGISTERING LOCAL USER TO SIP SERVER
    ACCOUNT_UNREGISTERING                  = '%s__ACCOUNT_UNREGISTERING' % Name               # UNREGISTERING LOCAL USER FROM SIP SERVER
    ACCOUNT_REGISTERED                     = '%s__ACCOUNT_REGISTERED' % Name
    ACCOUNT_UNREGISTERED                    = '%s__ACCOUNT_UNREGISTERED' % Name
    ACCOUNT_REGISTRATION_FAILED           = '%s__ACCOUNT_REGISTRATION_FAILED' % Name
    ACCOUNT_UNREGISTRATION_FAILED         = '%s__ACCOUNT_UNREGISTRATION_FAILED' % Name
    BUDDY_SUBSCRIBING                   = '%s__BUDDY_SUBSCRIBING' % Name  # REGISTERING REMOTE USER AS BUDDY
    BUDDY_SUBSCRIPTION_FAILED  = '%s__BUDDY_SUBSCRIPTION_FAILED' % Name
    BUDDY_SUBSCRIBED        = '%s__BUDDY_SUBSCRIBED' % Name    # REMOTE BUDDY REGISTERED
    BUDDY_CONNECTED         = '%s__BUDDY_CONNECTED' % Name     # REMOTE BUDDY CONNECTED
    BUDDY_HOLDING            = '%s__BUDDY_HOLDING' % Name     # REMOTE BUDDY CONNECTED
    BUDDY_DISCONNECTED      = '%s__REMOTE_BUDDY_DISCONNECTED' % Name
    
    CALL_DIALING                  = '%s__CALL_DIALING' % Name          # OUTCOMING CALL
    CALL_INCOMING                 = '%s__CALL_INCOMING' % Name        # INCOMING CALL
    CALL_INCOMING_REJECTED        = '%s__CALL_INCOMING_REJECTED' % Name        # INCOMING CALL REJECTED 
    CALL_ACTIVE                       = '%s__CALL_ACTIVE' % Name
    CALL_HOLDING                       = '%s__CALL_HOLDING' % Name
    CALL_UNHOLDING                     = '%s__CALL_UNHOLDING' % Name
    CALL_HANGUP                        = '%s__CALL_HANGUP' % Name
    CALL_REMOTE_HANGUP                  = '%s__CALL_REMOTE_HANGUP' % Name
    CALL_REMOTE_DISCONNECTION_HANGUP     = '%s__CALL_REMOTE_DISCONNECTION_HANGUP' % Name
    


class CallState:
    """
    This class contains all allowed states of a call
    """
    
    Name                    = 'CALL_STATE'
    #: No call
    IDLE = '%s__IDLE' % Name
    #: Dialing an incoming call
    INCOMING = '%s__INCOMING' % Name
    #: Active call
    ACTIVE = '%s__ACTIVE' % Name
    #: Dialing an outcoming call
    DIALING = '%s__DIALING' % Name
    #: The local account put the active call on hold
    HOLDING = '%s__HOLDING' % Name
    
    
class BuddyState:
    """
    This class contains all allowed states of a buddy
    """
    
    Name      = 'BUDDY_STATE' 
    #: Not Found
    NOT_FOUND = "%s__NOT_FOUND" % Name
    #: Off line
    OFF_LINE= "%s__OFF_LINE" % Name
    #: On line
    ON_LINE= "%s__ON_LINE" % Name
    #: On hold
    ON_HOLD= "%s__ON_HOLD" % Name
    #: Unknown
    UNKNOWN= "%s__UNKNOWN" % Name
 

class ServerState:
    """
    This class contains all allowed states of a remote Sip Server
    """
    
    Name      = 'SIP_SERVER_STATE' 
    #: Not Found
    NOT_FOUND = "%s__NOT_FOUND" % Name
    #: Disconnected
    DISCONNECTED = "%s__DISCONNECTED" % Name
    #: Connected
    CONNECTED = "%s__CONNECTED" % Name
   

class AccountState:
    """
    This class contains all allowed states of the local account
    """
    
    Name      = 'SIP_ACCOUNT_STATE' 
    #: Unregistered
    UNREGISTERED = "%s__UNREGISTERED" % Name
    #: Registered
    REGISTERED = "%s__REGISTERED" % Name
      
class VoipEventType:
    """
    This class contains the list of different types of event triggerable by the library
    """
    
    Name      = 'EVENT_TYPE' 
    #: Library Event Type (Library (de)initialization, Sip server (dis)onnection)
    LIB_EVENT  = "%s__LIB_EVENT" % Name   # voip library general events: (de) init, server disconnection
    #: Account Event Type (account (un)registration)
    ACCOUNT_EVENT = "%s__ACCOUNT_EVENT" % Name  # account (un)registration
    #: Call Event Type (incoming, dialing, active, (un)holding, hanging up)
    CALL_EVENT = "%s__CALL_EVENT" % Name # incoming, dialing, active, (un)holding, hanging up)
    #: Buddy Event Type ((un)subsscribing, (dis)connection,  remote (un)holding)
    BUDDY_EVENT = "%s__BUDDY_EVENT" % Name  # buddy presence notification: (un)subsscribing, (dis)connection,  remote (un)holding


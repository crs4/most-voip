'''
Created on 06/mag/2014

@author: smonni
'''
class VoipEvent(object):

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
    BUDDY_DISCONNECTED      = '%s__REMOTE_BUDDY_DISCONNECTED' % Name
    
    CALL_DIALING                  = '%s__CALL_DIALING' % Name          # OUTCOMING CALL
    CALL_INCOMING                 = '%s__CALL_INCOMING' % Name        # INCOMING CALL
    CALL_INCOMING_REJECTED        = '%s__CALL_INCOMING_REJECTED' % Name        # INCOMING CALL REJECTED 
    CALL_ACTIVE                       = '%s__CALL_ACTIVE' % Name
    CALL_HOLDING                       = '%s__CALL_HOLDING' % Name
    RemoteHolding                 = '%s__CALL_REMOTE_HOLDING' % Name
    RemoteLocalHolding            = '%s__CALL_REMOTE_AND_LOCAL_HOLDING' % Name
    CALL_UNHOLDING                     = '%s__CALL_UNHOLDING' % Name
    CALL_HANGUP                        = '%s__CALL_HANGUP' % Name
    CALL_REMOTE_HANGUP                  = '%s__CALL_REMOTE_HANGUP' % Name
    CALL_REMOTE_DISCONNECTION_HANGUP     = '%s__CALL_REMOTE_DISCONNECTION_HANGUP' % Name
    


class CallState:
    Name                    = 'CALL_STATE'
    IDLE = '%s__IDLE' % Name
    INCOMING = '%s__INCOMING' % Name
    ACTIVE = '%s__ACTIVE' % Name
    DIALING = '%s__DIALING' % Name
    HOLDING = '%s__HOLDING' % Name
    REMOTE_HOLDING = '%s__REMOTE_HOLDING' % Name
    
    
class BuddyState:
    Name      = 'BUDDY_STATE' 
    NOT_FOUND = "%s__NOT_FOUND" % Name
    OFF_LINE= "%s__OFF_LINE" % Name
    ON_LINE= "%s__ON_LINE" % Name
    ON_HOLD= "%s__ON_HOLD" % Name
    UNKNOWN= "%s__UNKNOWN" % Name
 

class ServerState:
    Name      = 'SIP_SERVER_STATE' 
    NOT_FOUND = "%s__NOT_FOUND" % Name
    DISCONNECTED = "%s__DISCONNECTED" % Name
    CONNECTED = "%s__CONNECTED" % Name
    
    
class VoipEventType:
    
    Name      = 'EVENT_TYPE' 
    LIB_EVENT  = "%s__LIB_EVENT" % Name   # voip library general events: (de) init, server disconnection
    ACCOUNT_EVENT = "%s__ACCOUNT_EVENT" % Name  # account (un)registration
    CALL_EVENT = "%s__CALL_EVENT" % Name # incoming, dialing, active, (un)holding, hanging up CALL
    BUDDY_EVENT = "%s__buddy_EVENT" % Name  # buddy presence notification: (un)subsscribing, (dis)connection,  remote (un)holding


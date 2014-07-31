#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#



from most.voip.api import VoipLib
from most.voip.constants import VoipEvent
 

import time

"""
For locally testing this application, assuming that you are using asterisk as Sip Server, do the following:

1) Run this script
2) from the Asterisk CLI console type the following command:
   originate SIP/ste extension
   where 'ste' is the user you have just registered  by this application on the SIP server from this application
"""

if __name__ == '__main__':
    
    # implement a method that will capture all the events triggered by the Voip Library
    def notify_events(voip_event_type,voip_event, params):
        print "Received Event Type:%s  Event:%s -> Params: %s" % (voip_event_type, voip_event, params)
        
        # event triggered when the account registration has been confirmed by the remote Sip Server 
        if (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            print "Account %s registered: ready to accept call!" % myVoip.get_account().get_uri()
        
        # event triggered when a new call is incoming
        elif (voip_event==VoipEvent.CALL_INCOMING):
            print "INCOMING CALL From %s" % params["from"]
            time.sleep(2)
            print "Answering..."
            myVoip.answer_call()
        
        # event triggered when the call has been established    
        elif(voip_event==VoipEvent.CALL_ACTIVE):
            print "The call with %s has been established"  % myVoip.get_call().get_remote_uri()
            
            dur = 2
            print "Waiting %s seconds before hanging up..."  % dur
            time.sleep(dur)
            myVoip.hangup_call()
          
        
        # events triggered when the call ends for some reasons      
        elif (voip_event in [VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, VoipEvent.CALL_REMOTE_HANGUP, VoipEvent.CALL_HANGUP]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
        # event triggered when the library was destroyed   
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Call End. Exiting from the app."
            sys.exit(0)
        
        # just print informations about other events triggered by the library
        else:
            print "Received unhandled event type:%s --> %s" % (voip_event_type,voip_event)
        
        
    voip_params0 = {u'username': u'ste', 
                   u'sip_server_pwd': u'ste', 
                   u'sip_server_address': u'192.168.1.100' , 
                   u'sip_server_user': u'ste', 
                   u'sip_server_transport' :u'udp',
                   #u'turn_server_address': u'192.168.1.79', 
                   #u'turn_server_user': u'', 
                   #u'turn_server_pwd': u'',
                   u'log_level' : 1,
                   u'debug' : True }
    
    
    
    myVoip = VoipLib()
    print "Initializing the Voip Lib..."
    myVoip.init_lib(voip_params0, notify_events)
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    
   
    while True:
        time.sleep(2)
    
    
    
    
    
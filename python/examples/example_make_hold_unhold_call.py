#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#


from most.voip.api import VoipLib
from most.voip.constants import VoipEvent, VoipEventType, CallState
 

import time, sys

if __name__ == '__main__':
    
   
    def notify_events(voip_event_type, voip_event, params):
        print "Received event type:%s Event:%s -> Params: %s" % (voip_event_type, voip_event, params)
        print "Current Call State:%s" % myVoip.get_call().get_state()
        
        # the local account is registered and it is ready to make calls
        if (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            
            # add a buddy for presence notification 
            print "Adding a buddy for extension: %s" % extension
            myVoip.get_account().add_buddy(extension)
            
            # make a call to the specified extension
            print "Making a call dialing the extension: %s" % extension
            myVoip.make_call(extension)
        
        # events triggered when the call ends for some reasons          
        elif (voip_event in [VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, VoipEvent.CALL_REMOTE_HANGUP, VoipEvent.CALL_HANGUP]):
            print "End of call!"
        
        # events triggered when the status of a buddy is changed
        elif(voip_event_type==VoipEventType.BUDDY_EVENT):
            print "Remote Buddy %s Status Changed (%s):%s" % (params["buddy"].get_extension(),voip_event, params["buddy"].get_status_text())
        
        # event triggered when the library was destroyed    
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Lib Destroyed. Exiting from the app."
            sys.exit(0)
            
        # just print informations about other events triggered by the library
        else:
            print "Received unhandled event type:%s --> %s" % (voip_event_type,voip_event)
            
    
    # choose an extension to call
    extension = "steand"   
    
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
    myVoip.init_lib(voip_params, notify_events)
    
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    
    print "Server State:%s" % myVoip.get_server().get_state()
    print "Call State:%s" % myVoip.get_call().get_state()
    
    while True:

        if myVoip.get_call().get_state()==CallState.ACTIVE:
            # get info about the current state of the Sip Server
            print "Server State:%s" % myVoip.get_server().get_state()
            
            # get info about the current state of the active call
            print "Call State:%s" % myVoip.get_call().get_state()
            
            # get info about the current state of the called buddy
            print "Buddy State:%s" % myVoip.get_account().get_buddy(extension).get_state()
            
            
            cmd = raw_input("Enter 'h' to put on hold the call,  'e' to hangup:")
            if (cmd=='h' and myVoip.get_call().get_state()==CallState.ACTIVE):
                myVoip.hold_call()
            elif (cmd=='e'):
                myVoip.hangup_call()
                
        elif myVoip.get_call().get_state()==CallState.HOLDING:
            
            print "Server State:%s" % myVoip.get_server().get_state()
            print "Call State:%s" % myVoip.get_call().get_state()
            
            cmd = raw_input("Enter 'u' to put on unhold the call,'e' to hangup:")
            if (cmd=='u' and myVoip.get_call().get_state()==CallState.HOLDING):
                
                myVoip.unhold_call()
           
            elif (cmd=='e'): 
                
                 myVoip.hangup_call()
                 print "Server State:%s" % myVoip.get_server().get_state()
                 print "Call State:%s" % myVoip.get_call().get_state()
                 
        elif myVoip.get_call().get_state()==CallState.IDLE:
            cmd = raw_input("Enter 'q' to quit the app")
            if cmd=='q':
                myVoip.destroy_lib()
            
                
          
   
    
    
    
    
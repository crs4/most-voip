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
    
    def notify_events(voip_event_type,voip_event, params):
        print "Received Event Type:%s  Event:%s -> Params: %s" % (voip_event_type, voip_event, params)
        
        if (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            print "Ready to accept call!"
        
        elif (voip_event==VoipEvent.CALL_INCOMING):
            print "INCOMING CALL From %s" % params["from"]
            time.sleep(2)
            print "Answering!"
            myVoip.answer_call()
            
        elif(voip_event==VoipEvent.CALL_ACTIVE):
            dur = 2
            print "Waiting %s seconds before holding..."  % dur
            time.sleep(dur)
            myVoip.hold_call()
              
        elif(voip_event==VoipEvent.CALL_HOLDING):
            dur = 2
            print "Waiting %s seconds before unholding..."  % dur
            time.sleep(dur)
            myVoip.unhold_call()
        
        elif(voip_event==VoipEvent.CALL_UNHOLDING):
            dur = 2
            print "Waiting %s seconds before hanging up..."  % dur
            time.sleep(dur)
            myVoip.hangup_call()
             
            
        elif (voip_event in [VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, VoipEvent.CALL_REMOTE_HANGUP, VoipEvent.CALL_HANGUP]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
            
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Call End. Exiting from the app."
            sys.exit(0)
            
        else:
            print "Received unhandled event:%s" % voip_event
        
        
    voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'156.148.33.226' , #'u'192.168.1.79',  u'156.148.33.223' 
                   u'sip_user': u'ste', 
                   u'transport' :u'udp',
                   #u'turn_server': u'192.168.1.79', 
                   #u'turn_user': u'', 
                   #u'turn_pwd': u'',
                   u'log_level' : 1,
                   u'debug' : True }
    
    voip_params =    {u'username': u'specialista', 
                          u'turn_server': u'156.148.133.240', 
                          u'sip_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212', 
                          u'sip_server': u'156.148.133.240', 
                          u'sip_user': u'specialista', 
                          u'turn_user': u'specialista', 
                          u'turn_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212',
                          u'log_level' : 5,
                          u'debug' : True 
                          }
    
    
    
    
    myVoip = VoipLib()
    print "Initializing the Voip Lib..."
    myVoip.init_lib(voip_params0, notify_events)
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    
   
    while True:
        time.sleep(2)
    
    
    
    
    
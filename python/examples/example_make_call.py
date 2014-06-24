from most.voip.api import VoipLib
from most.voip.states import VoipEvent
 

import time, sys


if __name__ == '__main__':
    
    extension = "steand"
    
    def notify_events(voip_event_type,voip_event, params):
        print "Received event type:%s Event:%s -> Params: %s" % (voip_event_type, voip_event, params)
        
        if (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            #extension = "REMOTE0002"
            #extension = "1234"
            print "Making a call dialing the extension: %s" % extension
            myVoip.make_call(extension)
            
        elif(voip_event==VoipEvent.CALL_ACTIVE):
            dur = 2
            #print "Waiting %s seconds before holding..."  % dur
            #time.sleep(dur)
            #myVoip.hold_call()
              
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
            print "Lib Destroyed. Exiting from the app."
            sys.exit(0)
            
        else:
            print "Received unhandled event:%s" % voip_event
        
    voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'156.148.33.226' , #'u'192.168.1.79',  u'156.148.33.223' 
                   u'sip_user': u'ste', 
                   u'transport' :u'udp',
                   # u'turn_server': u'192.168.1.79', 
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
                          u'log_level' : 1,
                          u'debug' : True 
                          }
    
    
    myVoip = VoipLib()
    print "Initializing the Voip Lib..."
    myVoip.init_lib(voip_params0, notify_events)
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    
    while True:
        time.sleep(1)
  
   
    
    
    
    
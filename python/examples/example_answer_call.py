from most.voip.api import VoipLib
from most.voip.states import VoipState
 

import time

if __name__ == '__main__':
    
    def notify_events(voip_state, params):
        print "Received state:%s -> Params: %s" % (voip_state, params)
        if (voip_state==VoipState.Registered):
            print "Ready to accept call!"
        
        if (voip_state==VoipState.Incoming):
            print "INCOMiNG CALL From %s" % params["from"]
            time.sleep(2)
            print "Answering!"
            myVoip.answer_call()
            
        elif(voip_state==VoipState.Calling):
            dur = 4
            print "Waiting %s seconds before hanging up..."  % dur
            time.sleep(dur)
            myVoip.hangup_call()
            
        elif (voip_state in [VoipState.RemoteDisconnectionHangup, VoipState.RemoteHangup, VoipState.Hangup]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
            
        elif (voip_state==VoipState.DeinitializeDone):
            print "Call End. Exiting from the app."
            sys.exit(0)
        
        
    voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'156.148.33.223' , #'u'192.168.1.79',  u'156.148.33.223' 
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
        time.sleep(2)
    
    
    
    
    
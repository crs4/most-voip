from most.voip.api import VoipLib
from most.voip.states import VoipState
 

import time, sys
"""
For locally testing this application, assuming that you are using asterisk as Sip Server, do the following:

1) Run this application
2) from the Asterisk CLI console type the following command:
   originate SIP/ste extension
   where 'ste' is the user you have just registered  by this application on the SIP server from this application
"""

if __name__ == '__main__':
    
    extension = "steand"
    
    def notify_events(voip_state, params):
        print "Received state:%s -> Params: %s" % (voip_state, params)
        
        if (voip_state==VoipState.Registered):
            #extension = "REMOTE0002"
            #extension = "1234"
            print "Making a call dialing the extension: %s" % extension
            myVoip.make_call(extension)
            
        elif(voip_state==VoipState.Calling):
            dur = 2
            #print "Waiting %s seconds before holding..."  % dur
            #time.sleep(dur)
            #myVoip.hold_call()
              
        elif(voip_state==VoipState.Holding):
            dur = 2
            print "Waiting %s seconds before unholding..."  % dur
            
            
            time.sleep(dur)
            myVoip.unhold_call()
        
        elif(voip_state==VoipState.Unholding):
            dur = 2
            print "Waiting %s seconds before hanging up..."  % dur
            time.sleep(dur)
            myVoip.hangup_call()
            
        elif (voip_state in [VoipState.RemoteDisconnectionHangup, VoipState.RemoteHangup, VoipState.Hangup]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
        elif (voip_state==VoipState.DeinitializeDone):
            print "Lib Destroyed. Exiting from the app."
            sys.exit(0)
        
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
  
   
    
    
    
    
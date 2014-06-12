from most.voip.api import VoipLib
from most.voip.states import VoipState, CallState
 

import time, sys

if __name__ == '__main__':
    
   
    def notify_events(voip_state, params):
        print "Received state:%s -> Params: %s" % (voip_state, params)
        print "Current Call State:%s" % myVoip.get_call_state()
        if (voip_state==VoipState.Registered):
            print "Adding a buddy for extension: %s" % extension
            myVoip.add_buddy(extension)
            print "Making a call dialing the extension: %s" % extension
            myVoip.make_call(extension)
            
        elif (voip_state in [VoipState.RemoteDisconnectionHangup, VoipState.RemoteHangup, VoipState.Hangup]):
            print "End of call. Destroying lib..."
            myVoip.destroy_lib()
            
        elif (voip_state==VoipState.DeinitializeDone):
            print "Lib Destroyed. Exiting from the app."
            sys.exit(0)
    
    extension = "steand"   
    
    voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'192.168.1.80' , #'u'192.168.1.79',  u'156.148.33.223' 
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
        if myVoip.get_call_state()==CallState.ACTIVE:
            cmd = raw_input("Enter 'h' to put on hold the call, 'e' to hangup:")
            if (cmd=='h' and myVoip.get_call_state()==CallState.ACTIVE):
                myVoip.hold_call()
            elif (cmd=='e'):
                myVoip.hangup_call()
        elif myVoip.get_call_state()==CallState.HOLDING:
            cmd = raw_input("Enter 'u' to put on unhold the call, 'e' to hangup:")
            if (cmd=='u' and myVoip.get_call_state()==CallState.HOLDING):
                myVoip.unhold_call()
            elif (cmd=='e'):
                myVoip.hangup_call()
  
   
    
    
    
    
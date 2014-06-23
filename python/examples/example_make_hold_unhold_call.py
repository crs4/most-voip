from most.voip.api import VoipLib
from most.voip.states import VoipEvent, CallState
 

import time, sys

if __name__ == '__main__':
    
   
    def notify_events(voip_state, params):
        print "Received state:%s -> Params: %s" % (voip_state, params)
        print "Current Call State:%s" % myVoip.get_call_state()
        if (voip_state==VoipEvent.ACCOUNT_REGISTERED):
            print "Adding a buddy for extension: %s" % extension
            myVoip.add_buddy(extension)
            print "Making a call dialing the extension: %s" % extension
            myVoip.make_call(extension)
            
        elif (voip_state in [VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, VoipEvent.CALL_REMOTE_HANGUP, VoipEvent.CALL_HANGUP]):
            print "End of call!"
            #myVoip.destroy_lib()
            
        elif (voip_state==VoipEvent.LIB_DEINITIALIZED):
            print "Lib Destroyed. Exiting from the app."
            sys.exit(0)
    
    extension = "steand"   
    
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
    
    print "Server State:%s" % myVoip.get_server_state()
    print "Call State:%s" % myVoip.get_call_state()
    while True:

        if myVoip.get_call_state()==CallState.ACTIVE:
            print "Server State:%s" % myVoip.get_server_state()
            print "Call State:%s" % myVoip.get_call_state()
            print "Buddy State:%s" % myVoip.get_buddy_state(extension)
            cmd = raw_input("Enter 'h' to put on hold the call,  'e' to hangup:")
            if (cmd=='h' and myVoip.get_call_state()==CallState.ACTIVE):
                myVoip.hold_call()
            elif (cmd=='e'):
                myVoip.hangup_call()
                
        elif myVoip.get_call_state()==CallState.HOLDING:
            print "Server State:%s" % myVoip.get_server_state()
            print "Call State:%s" % myVoip.get_call_state()
            cmd = raw_input("Enter 'u' to put on unhold the call,'e' to hangup:")
            if (cmd=='u' and myVoip.get_call_state()==CallState.HOLDING):
                
                myVoip.unhold_call()
           
            elif (cmd=='e'): 
                
                 myVoip.hangup_call()
                 print "Server State:%s" % myVoip.get_server_state()
                 print "Call State:%s" % myVoip.get_call_state()
                 
        elif myVoip.get_call_state()==CallState.IDLE:
            cmd = raw_input("Enter 'q' to quit the app")
            if cmd=='q':
                myVoip.destroy_lib()
            
                
          
   
    
    
    
    
from most.voip.api import VoipLib
from most.voip.states import VoipState
 

def notify_events(voip_state, params):
    print "Received state:%s -> Params: %s" % (voip_state, params)
    if (voip_state==VoipState.Registered):
        print "Making a call dialing a specific extension..."
        extension = "REMOTE0002"
        myVoip.make_call(extension)
        

if __name__ == '__main__':
    voip_params0 = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'156.148.133.239' , #'u'192.168.1.79',  u'156.148.33.223' 
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
    myVoip.initialize(voip_params0, notify_events)
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    
    
    import time
    print "Sleeping for some seconds for calling..."
    time.sleep(10)
    print "Hunging up the call..."
    myVoip.hungup_call()
    #myVoip.unregister_account()
    time.sleep(2)
    print "Finalizing the lib..."
    myVoip.finalize()
    
    
    
    
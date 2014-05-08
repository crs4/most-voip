from most.voip.api import VoipLib

 

def notify_events(voip_state, params):
    print "Received state:%s -> Params: %s" % (voip_state, params)

if __name__ == '__main__':
    voip_params = {u'username': u'ste', 
                   u'sip_pwd': u'ste', 
                   u'sip_server': u'192.168.1.79', 
                   u'sip_user': u'ste', 
                   u'transport' :u'udp',
                   # u'turn_server': u'192.168.1.79', 
                   #u'turn_user': u'', 
                   #u'turn_pwd': u'',
                   u'log_level' : 1,
                   u'debug' : False }
    
    
    myVoip = VoipLib()
    print "Initializing the Voip Lib..."
    myVoip.initialize(voip_params, notify_events)
    print "Registering the account on the Sip Server..."
    myVoip.register_account()
    print "MAking a call dialing a specific extension..."
    extension = "1234"
    myVoip.make_call(extension)
    
    import time
    print "Sleeping for some seconds for calling..."
    time.sleep(7)
    print "Hunging up the call..."
    myVoip.hungup_call()
    #myVoip.unregister_account()
    time.sleep(2)
    print "Finalizing the lib..."
    myVoip.finalize()
    
    
    
    
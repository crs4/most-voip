'''
Created on 28/apr/2014

@author: smonni
'''
import logging
from most.voip.states import VoipEvent
import time

class MockVoipState:
    NOT_INITIALIZED = 0
    INITIALIZED = 1
    OK = 2
    
class MockVoipBackend:
  
    
    def __init__(self):
        self.rootLogger = logging.getLogger('MockVoip')
        
        handler = logging.StreamHandler()
        rootFormatter = logging.Formatter('%(name)s - %(levelname)s: %(msg)s')
        handler.setFormatter(rootFormatter)
        self.rootLogger.addHandler(handler)
        self.rootLogger.setLevel(logging.ERROR)
        self.state = MockVoipState.NOT_INITIALIZED
        
       
    
    def init_lib(self,params, notification_cb):
        """
        init_lib the voip library
        @return: true if the initialization successfully completes. raise an exception otherwise
        """
        print "INITIALIZE CALLED!"
        self.rootLogger.debug("Initialing with params:%s" % params)
        self.notification_cb = notification_cb
        self.params = params
        self.notification_cb(VoipEvent.Initializing, { 'State': VoipEvent.Initializing ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipEvent.Initialized, { 'State': VoipEvent.Initialized ,'Success' : True})
        return True
    
    def destroy_lib(self):
        self.notification_cb(VoipEvent.Deinitializing, { 'State': VoipEvent.Deinitializing ,'Success' : True})
        time.sleep(0.5);
        self.state = MockVoipState.NOT_INITIALIZED
        self.notification_cb(VoipEvent.DeinitializeDone, { 'State': VoipEvent.DeinitializeDone ,'Success' : True})
         
        return True
    
    
    def register_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.rootLogger.debug("Registering account...")
        self.notification_cb(VoipEvent.Registering, { 'State': VoipEvent.Registering ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.OK
        self.notification_cb(VoipEvent.Registered, { 'State': VoipEvent.Registered ,'Success' : True})
        return True
    
    def unregister_account(self):
        if self.state != MockVoipState.OK:
            self.notification_cb(VoipEvent.Unregistration_failed, { 'State': VoipEvent.Unregistration_failed ,'Success' : False, 'Reason': 'No account to unregister found'})
            return False
        self.notification_cb(VoipEvent.Unregistering, { 'State': VoipEvent.Unregistering ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipEvent.Unregistered, { 'State': VoipEvent.Unregistered ,'Success' : True})
        return True
    
    def make_call(self, extension):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.Dialing, { 'State': VoipEvent.Dialing ,'Success' : True})
        time.sleep(0.5)
        self.notification_cb(VoipEvent.Calling, { 'State': VoipEvent.Calling ,'Success' : True})
        return True
    
    def answer_call(self):
        self.notification_cb(VoipEvent.Dialing, { 'State': VoipEvent.Dialing ,'Success' : True})
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.Calling, { 'State': VoipEvent.Calling ,'Success' : True})
        return True
    
    def hold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.Holding, { 'State': VoipEvent.Holding ,'Success' : True})
        return True
    
    def unhold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.Unholding, { 'State': VoipEvent.Unholding ,'Success' : True})
        return True
    
    def hangup_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.Hangup, { 'State': VoipEvent.Hangup ,'Success' : True})
        return True
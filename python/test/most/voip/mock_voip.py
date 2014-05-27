'''
Created on 28/apr/2014

@author: smonni
'''
import logging
from most.voip.states import VoipState
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
        
       
    
    def initialize(self,params, notification_cb):
        """
        initialize the voip library
        @return: true if the initialization successfully completes. raise an exception otherwise
        """
        print "INITIALIZE CALLED!"
        self.rootLogger.debug("Initialing with params:%s" % params)
        self.notification_cb = notification_cb
        self.params = params
        self.notification_cb(VoipState.Initializing, { 'State': VoipState.Initializing ,'Success' : True})
        time.sleep(1)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipState.Initialized, { 'State': VoipState.Initialized ,'Success' : True})
        return True
    
    def destroy(self):
        self.notification_cb(VoipState.Deinitializing, { 'State': VoipState.Deinitializing ,'Success' : True})
        time.sleep(1);
        self.state = MockVoipState.NOT_INITIALIZED
        self.notification_cb(VoipState.DeinitializeDone, { 'State': VoipState.DeinitializeDone ,'Success' : True})
         
        return True
    
    
    def register_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.rootLogger.debug("Registering account...")
        self.notification_cb(VoipState.Registering, { 'State': VoipState.Registering ,'Success' : True})
        time.sleep(1)
        self.state = MockVoipState.OK
        self.notification_cb(VoipState.Registered, { 'State': VoipState.Registered ,'Success' : True})
        return True
    
    def unregister_account(self):
        if self.state != MockVoipState.OK:
            self.notification_cb(VoipState.Unregistration_failed, { 'State': VoipState.Unregistration_failed ,'Success' : False, 'Reason': 'No account to unregister found'})
            return False
        self.notification_cb(VoipState.Unregistering, { 'State': VoipState.Unregistering ,'Success' : True})
        time.sleep(1)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipState.Unregistered, { 'State': VoipState.Unregistered ,'Success' : True})
        return True
    
    def make_call(self, extension):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipState.Dialing, { 'State': VoipState.Dialing ,'Success' : True})
        time.sleep(2)
        return True
    
    def answer_call(self):
        self.notification_cb(VoipState.Dialing, { 'State': VoipState.Dialing ,'Success' : True})
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipState.Calling, { 'State': VoipState.Calling ,'Success' : True})
        return True
    
    def hold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipState.Holding, { 'State': VoipState.Holding ,'Success' : True})
        return True
    
    def unhold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipState.Unholding, { 'State': VoipState.Unholding ,'Success' : True})
        return True
    
    def hangup_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipState.Hangup, { 'State': VoipState.Hangup ,'Success' : True})
        return True
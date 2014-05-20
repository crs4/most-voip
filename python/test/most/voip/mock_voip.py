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
    REGISTERED = 2
    
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
        self.rootLogger.debug("Initialing with params:%s" % params)
        self.notification_cb = notification_cb
        self.params = params
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipState.Initialized, { 'State': VoipState.Initialized ,'Success' : True})
        return True
    
    def finalize(self):
        self.state = MockVoipState.NOT_INITIALIZED
        return True
    
    
    def register_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.state = MockVoipState.REGISTERED
        self.rootLogger.debug("Registering account...")
        self.notification_cb(VoipState.Registered, { 'State': VoipState.Registered ,'Success' : True})
        return True
    
    def unregister_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipState.Unregistered, { 'State': VoipState.Unregistered ,'Success' : True})
        return True
    
    def make_call(self, extension):
        if self.state != MockVoipState.REGISTERED:
            return False
        self.notification_cb(VoipState.Dialing, { 'State': VoipState.Dialing ,'Success' : True})
        time.sleep(2)
        return True
    
    def answer_call(self):
        self.notification_cb(VoipState.Dialing, { 'State': VoipState.Dialing ,'Success' : True})
        if self.state != MockVoipState.REGISTERED:
            return False
        self.notification_cb(VoipState.Calling, { 'State': VoipState.Calling ,'Success' : True})
        return True
    
    def hold_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        self.notification_cb(VoipState.Holding, { 'State': VoipState.Holding ,'Success' : True})
        return True
    
    def unhold_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        self.notification_cb(VoipState.Unholding, { 'State': VoipState.Unholding ,'Success' : True})
        return True
    
    def hungup_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        self.notification_cb(VoipState.Hungup, { 'State': VoipState.Hungup ,'Success' : True})
        return True
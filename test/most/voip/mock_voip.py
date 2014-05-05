'''
Created on 28/apr/2014

@author: smonni
'''
import logging

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
        
       
    
    def initialize(self,params):
        """
        initialize the voip library
        @return: true if the initialization successfully completes. raise an exception otherwise
        """
        self.rootLogger.debug("Initialing with params:%s" % params)
        self.params = params
        self.state = MockVoipState.INITIALIZED
        return True
    
    def finalize(self):
        self.state = MockVoipState.NOT_INITIALIZED
        return True
    
    
    def register_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.state = MockVoipState.REGISTERED
        self.rootLogger.debug("Registering account...")
        return True
    
    def unregister_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.state = MockVoipState.INITIALIZED
        return True
    
    def make_call(self, extension):
        if self.state != MockVoipState.REGISTERED:
            return False
        return True
    
    def answer_call(self):
        
        if self.state != MockVoipState.REGISTERED:
            return False
        return True
    
    def hold_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        return True
    
    def unhold_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        return True
    
    def hungup_call(self):
        if self.state != MockVoipState.REGISTERED:
            return False
        return True
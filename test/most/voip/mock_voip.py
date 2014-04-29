'''
Created on 28/apr/2014

@author: smonni
'''
class MockVoipBackend:
    
    def initialize(self):
        """
        initialize the voip library
        @return: true if the initialization successfully completes. raise an exception otherwise
        """
        return True
    
    def finalize(self):
        return True
    
    
    def register_account(self):
        return True
    
    def unregister_account(self):
        return True
    
    def make_call(self, extension):
        return True
    
    def answer_call(self):
        return True
    
    def hold_call(self):
        return True
    
    def unhold_call(self):
        return True
    
    def hungup_call(self):
        return True
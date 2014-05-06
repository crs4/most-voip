'''
Created on 28/apr/2014

@author: smonni
'''

class VoipLib:
    def __init__(self, backend):
        self.backend = backend
    
    def initialize(self,params, notification_cb):
        """
        @param params: a dictionary contatining all initialization parameters
        @param notification_cb: a method called for all voip notification (status changes, errors, events and so on)
        @return: True if the initialization successfully completes, False otherwise 
        """
        return self.backend.initialize(params, notification_cb)
    
   
    def register_account(self):
        return self.backend.register_account()
    
    def unregister_account(self):
        return self.backend.unregister_account()
    
    def make_call(self, extension):
        return self.backend.make_call(extension)
    
    def answer_call(self):
        return self.backend.answer_call()
    
    def hold_call(self):
        return self.backend.hold_call()
    
    def unhold_call(self):
        return self.backend.unhold_call()
    
    def hungup_call(self):
        return self.backend.hungup_call()
    
    def finalize(self):
        """
        Destroy the Voip Lib and free all allocated resources.
        """
        return self.backend.finalize()
    
        

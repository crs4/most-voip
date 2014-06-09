'''
Created on 28/apr/2014

@author: smonni
'''
from api_backend import VoipBackend

class VoipLib:
    def __init__(self, backend=VoipBackend()):
        self.backend = backend
        
    def init_lib(self,params, notification_cb):
        """
        @param params: a dictionary containing all initialization parameters 
        @param notification_cb: a method called for all voip notification (status changes, errors, events and so on)
        @return: True if the initialization successfully completes, False otherwise 
        """
        return self.backend.init_lib(params, notification_cb)
    
   
    def register_account(self):
        """
        Register the account specified in the params dictionary passed to the L{init_lib} method
        """
        return self.backend.register_account()
    
    def unregister_account(self):
        """
        Unregister the account specified in the params dictionary passed to the L{init_lib} method
        """
        return self.backend.unregister_account()
    
    def make_call(self, extension):
        """
        Make a call to the specified extension
        @param extension:the extension to dial
        """
        
        return self.backend.make_call(extension)
    
    def answer_call(self):
        """
        Answer the current incoming call.
        """
        return self.backend.answer_call()
    
    def hold_call(self):
        """
        Put the currently active call on hold status
        """
        return self.backend.hold_call()
    
    def unhold_call(self):
        """
         Put the currently active call on active status
        """
        return self.backend.unhold_call()
    
    def hangup_call(self):
        """
        Hangup the currently active call
        """
        return self.backend.hangup_call()
    
    def destroy_lib(self):
        """
        Destroy the Voip Lib and free all allocated resources.
        """
        return self.backend.destroy_lib()
    
        

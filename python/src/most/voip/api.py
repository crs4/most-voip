'''
Created on 28/apr/2014

@author: smonni
'''
from api_backend import VoipBackend
#from states import ServerState, BuddyState

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
   
    """
    def set_online_status(self, is_online):
        return self.backend.set_online_status(is_online)
    
    def is_online(self):
        return self.backend.is_online()
    """
    
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
    
    
    def get_call_state(self):
        """
        @return: the state of the current call (if any)
        """
        return self.backend.get_call_state()
    
    def hangup_call(self):
        """
        Hangup the currently active call
        """
        return self.backend.hangup_call()
    
    def add_buddy(self, extension):
        """
        Add the specified buddy to this account (so its current state can be notified)
        @param extension: the extension related to the buddy to add
        """
        self.backend.add_buddy(extension)
        
    def remove_buddy(self, extension):
        """
        Remove the specified buddy from this account
        @param extension: the extension related to the buddy to remove
        """
        self.backend.remove_buddy(extension)
        
    def get_buddy_state(self, extension):
        """
        Get the current state of the buddy with the given extension
        @param extension: the extension of the buddy
        @return: the current state of the buddy
        @rtype: BuddyState
        """
        return self.backend.get_buddy_state(extension)
   
    def get_server_state(self):
        """
        Get the current state of the sip server
        @return: the current remote sip server state
        @rtype: ServerState
        """
        return self.backend.get_server_state()
    
    def destroy_lib(self):
        """
        Destroy the Voip Lib and free all allocated resources.
        """
        return self.backend.destroy_lib()
    
        

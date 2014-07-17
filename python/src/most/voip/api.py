"""
Created on 28/apr/2014

:author: CRS4 -- http://www.crs4.it

"""

from api_backend import VoipBackend


class VoipLib:
    
    def __init__(self, backend=VoipBackend):
        self.backend = backend()
        
    def init_lib(self,params, notification_cb):
        """Initialize the voip library
        
        :param params: a dictionary containing all initialization parameters 
        :param notification_cb: a method called for all voip notification (status changes, errors, events and so on)
        :returns: True if the initialization successfully completes, False otherwise 
        
        """
        
        return self.backend.init_lib(params, notification_cb)
    
   
    def register_account(self):
        """Register the account specified in the params dictionary passed to the :func:`init_lib` method
        
        """
        return self.backend.register_account()
 
    
    def unregister_account(self):
        """Unregister the account specified in the params dictionary passed to the L{init_lib} method
        
        """
        return self.backend.unregister_account()
    
    def make_call(self, extension):
        """
        Make a call to the specified extension
        :param extension:the extension to dial
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
        """Put the currently active call on active status
        
        """
        return self.backend.unhold_call()
    
    
    def get_call(self):
        """
        Get the current ICall instance
        
        :returns: ICall -- ICall object containing informations about the current call (if any)
        """
        return self.backend.get_call()
    
    def hangup_call(self):
        """
        Hangup the currently active call
        """
        return self.backend.hangup_call()
    
    
    def get_server(self):
        """
        Get informations about the remote sip server
        
        :returns: IServer --  an IServer object containing informations about the remote sip server
        """
        return self.backend.get_server()
    
    def get_account(self):
        """
        Get informations about the local account
        
        :returns: IAccount -- an IAccount object containing informations about the local sip account
        """
        return self.backend.get_account()
    
    def destroy_lib(self):
        """
        Destroy the Voip Lib and free all allocated resources.
        """
        return self.backend.destroy_lib()
    
        

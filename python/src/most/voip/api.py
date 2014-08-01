#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

"""
Most-Voip API - VoipLib Class
"""

class VoipLib:
    """
    It is the core class of the Library, that allows you to:
    
    - initialize the Voip Library
    - create  an account and register it on a remote Sip Server
    - make a call
    - listen for incoming calls and answer  
    
    """
    
    def __init__(self, backend=None):
        """
        Create a new instance of the VoipLib
        
        :param backend: (optional) if specified, it is used as the default VoipLib implementation
        
        """
        if backend==None:
            from api_backend import VoipBackend
            self.backend = VoipBackend()
        else:
            self.backend = backend()
            
        
    def init_lib(self,params, notification_cb):
        """Initialize the voip library
        
        :param params: a dictionary containing all initialization parameters 
        :param notification_cb: a callback method called by the library for all event notificationa (status changes, errors, events and so on)
        :returns: True if the initialization request completes without errors, False otherwise 
        
        """
        
        return self.backend.init_lib(params, notification_cb)
    
   
    def register_account(self):
        """Register the account specified into the *params* dictionary passed to the :func:`init_lib` method
        
        """
        return self.backend.register_account()
 
    
    def unregister_account(self):
        """Unregister the account specified in the *params* dictionary passed to the :func:`init_lib` method
        
        """
        return self.backend.unregister_account()
    
    def make_call(self, extension):
        """Make a call to the specified extension
        
        :param extension: the extension to dial
        
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
        
        :returns: an :class:`most.voip.interfaces.ICall`  object containing informations about the current call
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
        
        :returns:  an :class:`most.voip.interfaces.IServer` object containing informations about the remote sip server
        """
        return self.backend.get_server()
    
    def get_account(self):
        """
        Get informations about the local account
        
        :returns: an :class:`most.voip.interfaces.IAccount` object containing informations about the local sip account
        """
        return self.backend.get_account()
    
    def destroy_lib(self):
        """
        Destroy the Voip Lib and free all allocated resources.
        """
        return self.backend.destroy_lib()
    
        

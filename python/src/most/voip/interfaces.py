#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

"""
Most-Voip Interfaces
"""


class ICall:
    """
    This class contains informations about a call between 2 sip accounts.
    """
    
    
    def get_local_uri(self):
        """
        :returns: the uri of the local sip account
        """
        raise NotImplementedError
    
    def get_remote_uri(self):
        """
        :returns: the uri of the remote sip account
        """
        raise NotImplementedError
    
    def get_state(self):
        """
        :returns: the current state of this call (see :class:`most.voip.constants.CallState`)
        """
        raise NotImplementedError
    
    
class IBuddy:
    """
    This class contains informations about a buddy. 
    A buddy is a Sip user that notify its presence status to sip accounts that are interested to get informations by them.
    """
    
    def get_state(self):
        """
        :returns: the current state of this buddy  (see :class:`most.voip.constants.BuddyState`)
        """
        raise NotImplementedError
       
    def get_uri(self):
        """
        :returns: the sip uri of this buddy
        """
        raise NotImplementedError
    
    def get_extension(self):
        """
        :returns: the sip extension of this buddy
        """
        raise NotImplementedError
   
    def get_status_text(self):
        """
        :returns: a textual description of the current status of this buddy
        """
        raise NotImplementedError
   
    def refresh_status(self):
        """
        Refreshes the current status of this buddy
        """
        raise NotImplementedError 
    
    
    
    
class IServer:
    """
    This class contains informations about the remote Sip Server (e.g Asterisk)
    """
    
    def get_state(self):
        """
        :returns: the current status of the sip server (see :class:`most.voip.constants.ServerState`)
        """
        raise NotImplementedError
    
    def get_ip(self):
        """
        :returns: the ip address of the remote sip server
        """
        raise NotImplementedError


class IAccount:
    """
    This class contains informations about the local sip account.
    """
    
    def get_uri(self):
        """
        :returns: the sip uri of this account
        """
        raise NotImplementedError
    
    def get_state(self):
        """
        :returns: the current state of this account (see :class:`most.voip.constants.AccountState`)
        """
        raise NotImplementedError
    
    def add_buddy(self, extension):
        """
        Add the specified buddy to this account (so its current state can be notified)
        
        :param extension: the extension related to the buddy to add
        """
        
        raise NotImplementedError
        
    def remove_buddy(self, extension):
        """
        Remove the specified buddy from this account
        
        :param extension: the extension related to the buddy to remove
        """
        raise NotImplementedError
        
    def get_buddy(self, extension):
        """
        Get the buddy with the given extension
        
        :param extension: the extension of the buddy
        :returns: the  :class:`most.voip.interfaces.IBuddy` with the specified extension
        """
        raise NotImplementedError
    
    def get_buddies(self):
        """
        Get the list of buddies of the current registered account
        
        :returns:  the list of :class:`most.voip.interfaces.IBuddy`  subscribed by the local account
        """
        raise NotImplementedError
   

 
    

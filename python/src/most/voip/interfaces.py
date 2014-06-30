'''
Created on 24/giu/2014

@author: crs4
'''


class ICall:
    def get_local_uri(self):
        raise NotImplementedError
    
    def get_remote_uri(self):
        raise NotImplementedError
    
    def get_state(self):
        raise NotImplementedError
    
    
class IBuddy:
    def get_state(self):
        raise NotImplementedError
       
    def get_uri(self):
        raise NotImplementedError
    
    def get_extension(self):
        raise NotImplementedError
   
    def get_status_text(self):
        raise NotImplementedError
   
    def refresh_status(self):
        raise NotImplementedError 
    
    
    
    
class IServer:
    def get_state(self):
        raise NotImplementedError
    
    def get_ip(self):
        raise NotImplementedError


class IAccount:
    def get_uri(self):
        raise NotImplementedError
    
    def get_state(self):
        raise NotImplementedError
    
    def add_buddy(self, extension):
        """
        Add the specified buddy to this account (so its current state can be notified)
        @param extension: the extension related to the buddy to add
        """
        raise NotImplementedError
        
    def remove_buddy(self, extension):
        """
        Remove the specified buddy from this account
        @param extension: the extension related to the buddy to remove
        """
        raise NotImplementedError
        
    def get_buddy(self, extension):
        """
        Get the buddy with the given extension
        @param extension: the extension of the buddy
        @return: the buddy with the specified extension
        @rtype: IBuddy
        """
        raise NotImplementedError
    
    def get_buddies(self):
        """
        Get the list of buddies of the current registered account
        @return:  the list of the buddies of the currently registered account
        """
        raise NotImplementedError
   

 
    

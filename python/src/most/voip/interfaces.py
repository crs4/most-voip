'''
Created on 24/giu/2014

@author: crs4
'''


class ICallInfo:
    def get_local_uri(self):
        raise NotImplementedError
    
    def get_remote_uri(self):
        raise NotImplementedError
    
    
class IBuddy:
    def get_state(self):
        raise NotImplementedError
       
    def get_uri(self):
        raise NotImplementedError
   
    def get_status_text(self):
        raise NotImplementedError
   
    def refresh_status(self):
        raise NotImplementedError 
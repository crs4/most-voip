#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

import logging
from most.voip.constants import VoipEvent
import time

class MockVoipState:
    NOT_INITIALIZED = 0
    INITIALIZED = 1
    OK = 2
    
class MockVoipBackend:
  
    
    def __init__(self):
        self.rootLogger = logging.getLogger('MockVoip')
        
        handler = logging.StreamHandler()
        rootFormatter = logging.Formatter('%(name)s - %(levelname)s: %(msg)s')
        handler.setFormatter(rootFormatter)
        self.rootLogger.addHandler(handler)
        self.rootLogger.setLevel(logging.ERROR)
        self.state = MockVoipState.NOT_INITIALIZED
        
       
    
    def init_lib(self,params, notification_cb):
        """
        init_lib the voip library
        @return: true if the initialization successfully completes. raise an exception otherwise
        """
        print "INITIALIZE CALLED!"
        self.rootLogger.debug("Initialing with params:%s" % params)
        self.notification_cb = notification_cb
        self.params = params
        self.notification_cb(VoipEvent.LIB_INITIALIZING, { 'State': VoipEvent.LIB_INITIALIZING ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipEvent.LIB_INITIALIZED, { 'State': VoipEvent.LIB_INITIALIZED ,'Success' : True})
        return True
    
    def destroy_lib(self):
        self.notification_cb(VoipEvent.LIB_DEINITIALIZING, { 'State': VoipEvent.LIB_DEINITIALIZING ,'Success' : True})
        time.sleep(0.5);
        self.state = MockVoipState.NOT_INITIALIZED
        self.notification_cb(VoipEvent.LIB_DEINITIALIZED, { 'State': VoipEvent.LIB_DEINITIALIZED ,'Success' : True})
         
        return True
    
    
    def register_account(self):
        if self.state == MockVoipState.NOT_INITIALIZED:
            return False
        self.rootLogger.debug("Registering account...")
        self.notification_cb(VoipEvent.ACCOUNT_REGISTERING, { 'State': VoipEvent.ACCOUNT_REGISTERING ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.OK
        self.notification_cb(VoipEvent.ACCOUNT_REGISTERED, { 'State': VoipEvent.ACCOUNT_REGISTERED ,'Success' : True})
        return True
    
    def unregister_account(self):
        if self.state != MockVoipState.OK:
            self.notification_cb(VoipEvent.ACCOUNT_UNREGISTRATION_FAILED, { 'State': VoipEvent.ACCOUNT_UNREGISTRATION_FAILED ,'Success' : False, 'Reason': 'No account to unregister found'})
            return False
        self.notification_cb(VoipEvent.ACCOUNT_UNREGISTERING, { 'State': VoipEvent.ACCOUNT_UNREGISTERING ,'Success' : True})
        time.sleep(0.5)
        self.state = MockVoipState.INITIALIZED
        self.notification_cb(VoipEvent.ACCOUNT_UNREGISTERED, { 'State': VoipEvent.ACCOUNT_UNREGISTERED ,'Success' : True})
        return True
    
    def make_call(self, extension):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.CALL_DIALING, { 'State': VoipEvent.CALL_DIALING ,'Success' : True})
        time.sleep(0.5)
        self.notification_cb(VoipEvent.CALL_ACTIVE, { 'State': VoipEvent.CALL_ACTIVE ,'Success' : True})
        return True
    
    def answer_call(self):
        self.notification_cb(VoipEvent.CALL_DIALING, { 'State': VoipEvent.CALL_DIALING ,'Success' : True})
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.CALL_ACTIVE, { 'State': VoipEvent.CALL_ACTIVE ,'Success' : True})
        return True
    
    def hold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.CALL_HOLDING, { 'State': VoipEvent.CALL_HOLDING ,'Success' : True})
        return True
    
    def unhold_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.CALL_UNHOLDING, { 'State': VoipEvent.CALL_UNHOLDING ,'Success' : True})
        return True
    
    def hangup_call(self):
        if self.state != MockVoipState.OK:
            return False
        self.notification_cb(VoipEvent.CALL_HANGUP, { 'State': VoipEvent.CALL_HANGUP ,'Success' : True})
        return True
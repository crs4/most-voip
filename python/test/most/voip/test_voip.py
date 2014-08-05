#
# Project MOST - Moving Outcomes to Standard Telemedicine Practice
# http://most.crs4.it/
#
# Copyright 2014, CRS4 srl. (http://www.crs4.it/)
# Dual licensed under the MIT or GPL Version 2 licenses.
# See license-GPLv2.txt or license-MIT.txt
#

import unittest

from most.voip.api import VoipLib
from most.voip.constants import VoipEvent
#from most.voip.api_backend import VoipBackend
from mock_voip import MockVoipBackend

import time

class VoipTestCase(unittest.TestCase):
    
    def __init__(self, test_method,backend):
        #unittest.TestCase(test_method)
        super(VoipTestCase,self).__init__(test_method)
     
        self.voip = VoipLib(backend)
        self.holding_event_already_triggered = False
        self.curEventIndex = 0
    
        self.extension = "1234";  
       
        
        self.params  =    {u'username': u'ste', 
                                u'sip_user': u'ste', 
                                u'sip_pwd': u'ste',    
                                u'sip_server': u'192.168.1.100', 
                                u'transport' : u'udp',
                                u'log_level' : 1}
        
      
        
    def account_reg_notification_cb(self, voip_event, params):
        print "Notification Event:%s - Params:%s" % (voip_event,params)
        self.voipEvent = voip_event
        
        self.assertEqual(voip_event,  self.expectedEvents[self.curEventIndex], "Wrong event: %s . Expected:%s"  % (self.voipEvent,  self.expectedEvents[self.curEventIndex]) )

        self.curEventIndex+=1
         
        if (voip_event==VoipEvent.LIB_INITIALIZED):
            self.assertTrue(self.voip.register_account());    
        elif (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            self.assertTrue(self.voip.unregister_account());    
        elif (voip_event==VoipEvent.ACCOUNT_UNREGISTERED):
            self.assertTrue(self.voip.destroy_lib());
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Ok."
   
    def make_call_notification_cb(self, voip_event, params):
        print "Notification Event:%s - Params:%s" % (voip_event,params)
        self.voipEvent = voip_event
        
        self.assertEqual(voip_event,  self.expectedEvents[self.curEventIndex], "Wrong event: %s . Expected:%s"  % (self.voipEvent,  self.expectedEvents[self.curEventIndex]) )

        self.curEventIndex+=1
         
        if (voip_event==VoipEvent.LIB_INITIALIZED):
            self.assertTrue(self.voip.register_account());    
        elif (voip_event==VoipEvent.ACCOUNT_REGISTERED):
            self.assertTrue(self.voip.make_call(self.extension)); 
        elif (voip_event==VoipEvent.CALL_DIALING):
            pass   
        elif (voip_event==VoipEvent.CALL_ACTIVE):
            time.sleep(2)
            self.assertTrue(self.voip.hold_call()); 
                
        elif (voip_event==VoipEvent.CALL_HOLDING):
            time.sleep(0.5)
            self.assertTrue(self.voip.unhold_call()); 
        elif (voip_event==VoipEvent.CALL_UNHOLDING):
            self.assertTrue(self.voip.hangup_call()); 
        elif (voip_event==VoipEvent.CALL_HANGUP):
            self.assertTrue(self.voip.unregister_account());  
        elif (voip_event==VoipEvent.ACCOUNT_UNREGISTERED):
            self.assertTrue(self.voip.destroy_lib());
        elif (voip_event==VoipEvent.LIB_DEINITIALIZED):
            print "Ok."
        
    def setUp(self):
        print "Running test:%s" % self._testMethodName
        self.curEventIndex = 0
        self.voipEvent = VoipEvent.Null
       
    def tearDown(self):
        print "Test:%s completed." % self._testMethodName
        print "-----------------------------------------------\n"
      
        
    def test_account_registration(self):
        
        self.expectedEvents = [
                        VoipEvent.LIB_INITIALIZING, 
                        VoipEvent.LIB_INITIALIZED , 
                        VoipEvent.ACCOUNT_REGISTERING, 
                        VoipEvent.ACCOUNT_REGISTERED, 
                        VoipEvent.ACCOUNT_UNREGISTERING,
                        VoipEvent.ACCOUNT_UNREGISTERED,
                        VoipEvent.LIB_DEINITIALIZING,
                        VoipEvent.LIB_DEINITIALIZED
                        ]
         
        result = self.voip.init_lib(self.params, self.account_reg_notification_cb)
      
        self.assertTrue(result , "Error initializing the lib!")
        
        while (self.curEventIndex<len(self.expectedEvents)):
            time.sleep(0.5)
            
            
    def test_make_call(self):
        
        self.expectedEvents = [
                        VoipEvent.LIB_INITIALIZING, 
                        VoipEvent.LIB_INITIALIZED , 
                        VoipEvent.ACCOUNT_REGISTERING, 
                        VoipEvent.ACCOUNT_REGISTERED, 
                        VoipEvent.CALL_DIALING,
                        VoipEvent.CALL_ACTIVE,
                        VoipEvent.CALL_HOLDING,
                        VoipEvent.CALL_UNHOLDING,
                        VoipEvent.CALL_HANGUP,
                        VoipEvent.ACCOUNT_UNREGISTERING,
                        VoipEvent.ACCOUNT_UNREGISTERED,
                        VoipEvent.LIB_DEINITIALIZING,
                        VoipEvent.LIB_DEINITIALIZED
                        ]
         
        result = self.voip.init_lib(self.params, self.make_call_notification_cb)
      
        self.assertTrue(result , "Error initializing the lib!")
        
        while (self.curEventIndex<len(self.expectedEvents)):
            time.sleep(0.5)

    
class DummyVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(DummyVoipTestCase,self).__init__(test_method, MockVoipBackend)
        
"""
class PjsipVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(PjsipVoipTestCase,self).__init__(test_method, VoipBackend)
"""
       
def getDummyVoipSuite():
    return  unittest.makeSuite(DummyVoipTestCase, "test")

"""
def getRealVoipSuite():
    return  unittest.makeSuite(PjsipVoipTestCase, "test")
"""

 
if __name__ == '__main__':
    myDummySuite = getDummyVoipSuite()
    #myRealSuite = getRealVoipSuite()
    runner = unittest.TextTestRunner()
    runner.run(myDummySuite)
    #runner.run(myRealSuite)
    
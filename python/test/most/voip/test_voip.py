import unittest

from most.voip.api import VoipLib
from most.voip.constants import VoipEvent
from most.voip.api_backend import VoipBackend
from mock_voip import MockVoipBackend

import time

class VoipTestCase(unittest.TestCase):
    
    def __init__(self, test_method,backend):
        #unittest.TestCase(test_method)
        super(VoipTestCase,self).__init__(test_method)
     
        self.voip = VoipLib(backend)
        self.holding_event_already_triggered = False
        self.curStateIndex = 0
    
        self.extension = "1234"; #"REMOTE0002"; # "1234"
        self.params_spec =    {u'username': u'specialista', 
                          u'turn_server': u'156.148.133.240', 
                          u'sip_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212', 
                          u'sip_server': u'156.148.133.240', 
                          u'sip_user': u'specialista', 
                          u'turn_user': u'specialista', 
                          u'turn_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212',
                                         #sha1$40fcf$4718177db1b6966f64d2d436f212 8da010a282b5
                          u'log_level' :1 }
        
        self.params_eco =    {u'username': u'ecografista', 
                              u'turn_server': u'156.148.133.240', 
                              u'sip_pwd': u'sha1$fdcad$659da6841c6d8538b7a10ca12aae', 
                                        #sha1$40fcf$4718177db1b6966f64d2d436f212
                                        #sha1$fdcad$659da6841c6d8538b7a10ca12aae 3303f9a5a88b
                                       
                          u'sip_server': u'156.148.133.240', 
                          u'sip_user': u'ecografista', 
                          u'turn_user': u'ecografista', 
                          u'turn_pwd': u'sha1$fdcad$659da6841c6d8538b7a10ca12aae',
                          u'log_level' : 5}
        
        self.params_local =    {u'username': u'ste', 
                                u'sip_user': u'ste', 
                                u'sip_pwd': u'ste',    
                                u'sip_server': u'156.148.33.223', 
                                u'transport' : u'udp',
                                u'log_level' : 1}
        
        self.params = self.params_local
        
        
    
    def account_reg_notification_cb(self, voip_state, params):
        print "Notification State:%s - Params:%s" % (voip_state,params)
        self.voipState = voip_state
        
        self.assertEqual(voip_state,  self.expectedStates[self.curStateIndex], "Wrong state: %s . Expected:%s"  % (self.voipState,  self.expectedStates[self.curStateIndex]) )

        self.curStateIndex+=1
         
        if (voip_state==VoipEvent.LIB_INITIALIZED):
            self.assertTrue(self.voip.register_account());    
        elif (voip_state==VoipEvent.ACCOUNT_REGISTERED):
            self.assertTrue(self.voip.unregister_account());    
        elif (voip_state==VoipEvent.ACCOUNT_UNREGISTERED):
            self.assertTrue(self.voip.destroy_lib());
        elif (voip_state==VoipEvent.LIB_DEINITIALIZED):
            print "Ok."
   
    def make_call_notification_cb(self, voip_state, params):
        print "Notification State:%s - Params:%s" % (voip_state,params)
        self.voipState = voip_state
        
        self.assertEqual(voip_state,  self.expectedStates[self.curStateIndex], "Wrong state: %s . Expected:%s"  % (self.voipState,  self.expectedStates[self.curStateIndex]) )

        self.curStateIndex+=1
         
        if (voip_state==VoipEvent.LIB_INITIALIZED):
            self.assertTrue(self.voip.register_account());    
        elif (voip_state==VoipEvent.ACCOUNT_REGISTERED):
            self.assertTrue(self.voip.make_call(self.extension)); 
        elif (voip_state==VoipEvent.CALL_DIALING):
            pass   
        elif (voip_state==VoipEvent.CALL_ACTIVE):
            time.sleep(2)
            self.assertTrue(self.voip.hold_call()); 
                
        elif (voip_state==VoipEvent.CALL_HOLDING):
            time.sleep(0.5)
            self.assertTrue(self.voip.unhold_call()); 
        elif (voip_state==VoipEvent.CALL_UNHOLDING):
            self.assertTrue(self.voip.hangup_call()); 
        elif (voip_state==VoipEvent.CALL_HANGUP):
            self.assertTrue(self.voip.unregister_account());  
        elif (voip_state==VoipEvent.ACCOUNT_UNREGISTERED):
            self.assertTrue(self.voip.destroy_lib());
        elif (voip_state==VoipEvent.LIB_DEINITIALIZED):
            print "Ok."
        
    def setUp(self):
        print "Running test:%s" % self._testMethodName
        self.curStateIndex = 0
        self.voipState = VoipEvent.Null
       
    def tearDown(self):
        print "Test:%s completed." % self._testMethodName
        print "-----------------------------------------------\n"
      
        
    def etest_account_registration(self):
        
        self.expectedStates = [
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
        
        while (self.curStateIndex<len(self.expectedStates)):
            time.sleep(0.5)
            
            
    def test_make_call(self):
        
        self.expectedStates = [
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
        
        while (self.curStateIndex<len(self.expectedStates)):
            time.sleep(0.5)

#      
#     def test_register_account(self):
#         self.assertTrue(self.voip.register_account(), "Error registering the account!")
#         self.assertEquals(self.voipState, VoipEvent.Registered,"Registration state failed") 
#         
#     def test_unregister_account(self):
#         self.assertTrue(self.voip.unregister_account(), "Error unregistering the account!")
#         self.assertEquals(self.voipState, VoipEvent.Unregistered,"Unregistration state failed") 
#     
#    
#     def test_make_call(self):
#         self.assertTrue(self.voip.make_call(self.extension), "Failed making a call to extension %s" % self.extension)
#         #self.assertEquals(self.voipState, VoipEvent.Dialing,"Dialing state failed") 
#     
#     
#     def test_answer_call(self):
#         self.assertTrue(self.voip.answer_call(), "Failed answering the call")
#          
#     def test_hold(self):
#         self.assertTrue(self.voip.hold_call(), "Failed holding the call")
#         self.assertEquals(self.voipState, VoipEvent.Holding,"Holding state failed") 
#         
#     def test_unhold(self):
#         self.assertTrue(self.voip.unhold_call(), "Failed unholding the call")
#         self.assertEquals(self.voipState, VoipEvent.Unholding,"Unholding state failed") 
#      
#     def test_hungup(self):
#         self.assertTrue(self.voip.hungup_call(), "Failed hunging up the call")
#         self.assertEquals(self.voipState, VoipEvent.Hungup,"Hungup state failed") 
#     
#      
#     def test_finalize(self):
#         self.assertTrue(self.voip.destroy(), "Error finalizing the lib!")
#      
     
    
class DummyVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(DummyVoipTestCase,self).__init__(test_method, MockVoipBackend)
        
class PjsipVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(PjsipVoipTestCase,self).__init__(test_method, VoipBackend)
        
def getDummyVoipSuite():
    return  unittest.makeSuite(DummyVoipTestCase, "test")

def getRealVoipSuite():
    return  unittest.makeSuite(PjsipVoipTestCase, "test")
    

 
if __name__ == '__main__':
    #pass
    myDummySuite = getDummyVoipSuite()
    #myRealSuite = getRealVoipSuite()
    runner = unittest.TextTestRunner()
    runner.run(myDummySuite)
    #runner.run(myRealSuite)
    
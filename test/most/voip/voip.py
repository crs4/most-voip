import unittest

from most.voip.api import VoipLib
from most.voip.states import VoipState
from most.voip.api_backend import VoipBackend
from mock_voip import MockVoipBackend

class VoipTestCase(unittest.TestCase):
    
    def __init__(self, test_method,backend):
        #unittest.TestCase(test_method)
        super(VoipTestCase,self).__init__(test_method)
        self.voip = VoipLib(backend)
        self.extension = "REMOTE_0002"
        self.params =    {u'username': u'specialista', 
                          u'turn_server': u'156.148.133.240', 
                          u'sip_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212', 
                          u'sip_server': u'156.148.133.240', 
                          u'sip_user': u'specialista', 
                          u'turn_user': u'specialista', 
                          u'turn_pwd': u'sha1$40fcf$4718177db1b6966f64d2d436f212',
                          u'log_level' : 5}
        
        self.voipState = VoipState.Null
        
    
    def notification_cb(self, voip_state, params):
        print "Notification State:%s - Params:%s" % (voip_state,params)
        self.voipState = voip_state
        
    def setUp(self):
        print "Running test:%s" % self._testMethodName
        if (self._testMethodName != "test_initialize"):
            self.voip.initialize(self.params, self.notification_cb)
            if (self._testMethodName != "test_register_account"):
                self.voip.register_account()
        
    def tearDown(self):
        print "Current State:%s" % str(self._resultForDoCleanups)
        print "-----------------------------------------------\n"
        self.voip.finalize()
        
        
    def test_initialize(self):
        print "Test initialize....."
        self.assertTrue(self.voip.initialize(self.params, self.notification_cb), "Error initializing the lib!")
        self.assertEquals(self.voipState, VoipState.Initialized,"Initialization state failed") 
     
    def test_register_account(self):
        self.assertTrue(self.voip.register_account(), "Error registering the account!")
        self.assertEquals(self.voipState, VoipState.Registered,"Registration state failed") 
        
    def test_unregister_account(self):
        self.assertTrue(self.voip.unregister_account(), "Error unregistering the account!")
        self.assertEquals(self.voipState, VoipState.Unregistered,"Unregistration state failed") 
         
    def test_call(self):
        self.assertTrue(self.voip.make_call(self.extension), "Failed making a call to extension %s" % self.extension)
        #self.assertEquals(self.voipState, VoipState.Dialing,"Dialing state failed") 
     
    def test_answer_call(self):
        self.assertTrue(self.voip.answer_call(), "Failed answering the call")
         
    def test_hold(self):
        self.assertTrue(self.voip.hold_call(), "Failed holding the call")
        self.assertEquals(self.voipState, VoipState.Holding,"Holding state failed") 
        
    def test_unhold(self):
        self.assertTrue(self.voip.unhold_call(), "Failed unholding the call")
        self.assertEquals(self.voipState, VoipState.Unholding,"Unholding state failed") 
     
    def test_hungup(self):
        self.assertTrue(self.voip.hungup_call(), "Failed hunging up the call")
        self.assertEquals(self.voipState, VoipState.Hungup,"Hungup state failed") 
         
    def test_finalize(self):
        self.assertTrue(self.voip.finalize(), "Error finalizing the lib!")
        
     
    
class DummyVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(DummyVoipTestCase,self).__init__(test_method, MockVoipBackend())
        
class PjsipVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(PjsipVoipTestCase,self).__init__(test_method, VoipBackend())
        
def getDummyVoipSuite():
    return  unittest.makeSuite(DummyVoipTestCase, "test")

def getRealVoipSuite():
    return  unittest.makeSuite(PjsipVoipTestCase, "test")
    

 
if __name__ == '__main__':
    
    myDummySuite = getDummyVoipSuite()
    myRealSuite = getRealVoipSuite()
    runner = unittest.TextTestRunner()
    #runner.run(myDummySuite)
    runner.run(myRealSuite)
    
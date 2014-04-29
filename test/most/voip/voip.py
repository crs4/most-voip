import unittest

from most.voip.api import VoipLib
from mock_voip import MockVoipBackend


class VoipTestCase(unittest.TestCase):
    
    def __init__(self, test_method,backend):
        #unittest.TestCase(test_method)
        super(VoipTestCase,self).__init__(test_method)
        self.voip = VoipLib(backend)
        self.extension = "REMOTE_0002"
        
   
    def setUp(self):
        print "Running test:%s" % self._testMethodName
        
    def tearDown(self):
        print "-----------------------------------------------\n"
        
    def test_initialize(self):
        self.assertTrue(self.voip.initialize(), "Error initializing the lib!")
    
    def test_register_account(self):
        self.assertTrue(self.voip.register_account(), "Error registering the account!")
        
    def test_unregister_account(self):
        self.assertTrue(self.voip.unregister_account(), "Error unregistering the account!")
        
    def test_call(self):
        self.assertTrue(self.voip.make_call(self.extension), "Failed making a call to extension %s" % self.extension)
    
    def test_answer_call(self):
        self.assertTrue(self.voip.answer_call(), "Failed answering the call")
        
    def test_hold(self):
        self.assertTrue(self.voip.hold_call(), "Failed holding the call")
    
    def test_unhold(self):
        self.assertTrue(self.voip.unhold_call(), "Failed unholding the call")
    
    def test_hungup(self):
        self.assertTrue(self.voip.hungup_call(), "Failed hunging up the call")
        
    def test_finalize(self):
        self.assertTrue(self.voip.finalize(), "Error finalizing the lib!")
       
    
    
class DummyVoipTestCase(VoipTestCase):
    def __init__(self, test_method):
        super(DummyVoipTestCase,self).__init__(test_method, MockVoipBackend())
        
        
def getDummyVoipSuite():
    return  unittest.makeSuite(DummyVoipTestCase, "test")
    

 
if __name__ == '__main__':
    
    mySuite = getDummyVoipSuite()
    runner = unittest.TextTestRunner()
    runner.run(mySuite)
    
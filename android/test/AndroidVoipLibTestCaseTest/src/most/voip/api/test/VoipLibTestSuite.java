package most.voip.api.test;

import java.util.Currency;

import junit.framework.TestCase;
import most.voip.api.*;
import android.os.*;
import android.util.Log;


public class VoipLibTestSuite extends TestCase implements Handler.Callback  {
	 
	abstract class HandlerTest {
		protected int curStateIndex = 0;
		protected VoipState [] expectedStates = {};
		
		public boolean isDone() {
			return curStateIndex>=expectedStates.length;
		}
		
		public abstract boolean handleMessage(Message voipMessage);

	}
	
	class AccountRegistrationHandlerTest extends HandlerTest {
		
		AccountRegistrationHandlerTest () {
			
			this.expectedStates = new VoipState[] {VoipState.INITIALIZED , 
					VoipState.REGISTERING, 
					VoipState.REGISTERED, 
					VoipState.UNREGISTERING,
					VoipState.UNREGISTERED,
					VoipState.DEINITIALIZING,
					VoipState.DEINITIALIZE_DONE};
		}
 
		
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
			String infoMsg = myState.getState() + ":" + myState.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myState.getState(), expectedStates[curStateIndex]);
			curStateIndex++;
			     if (myState.getState()==VoipState.INITIALIZED)   assertTrue(myVoip.registerAccount());	
			else if (myState.getState()==VoipState.REGISTERED)    assertTrue(myVoip.unregisterAccount());	
			else if (myState.getState()==VoipState.UNREGISTERED)  assertTrue(myVoip.destroyLib());

			return false;
		}

	}
	
	class MakeCallHandlerTest extends HandlerTest {
		
		 MakeCallHandlerTest () {
			
			this.expectedStates = new VoipState[] {VoipState.INITIALIZED , 
					VoipState.REGISTERING, 
					VoipState.REGISTERED, 
					VoipState.CALL_DIALING,
					VoipState.CALL_ACTIVE,
					VoipState.CALL_HANGUP,
					VoipState.UNREGISTERING,
					VoipState.UNREGISTERED,
					VoipState.DEINITIALIZING,
					VoipState.DEINITIALIZE_DONE};
		}
 
		
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			//Log.d(TAG, "Called handleMessage with info...");
			VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
			String infoMsg = myState.getState() + ":" + myState.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myState.getState(), expectedStates[curStateIndex]);
			curStateIndex++;
			     if (myState.getState()==VoipState.INITIALIZED)   assertTrue(myVoip.registerAccount());	
			else if (myState.getState()==VoipState.REGISTERED)    assertTrue(myVoip.makeCall("destination_test_extension"));	
			else if (myState.getState()==VoipState.CALL_ACTIVE)   assertTrue(myVoip.hangupCall());	
			else if (myState.getState()==VoipState.CALL_HANGUP)   assertTrue(myVoip.unregisterAccount());	
			else if (myState.getState()==VoipState.UNREGISTERED)  assertTrue(myVoip.destroyLib());

			return false;
		}

	}
	
	
	class AnswerCallHandlerTest extends HandlerTest {
		
		 AnswerCallHandlerTest () {
			
			this.expectedStates = new VoipState[] {VoipState.INITIALIZED , 
					VoipState.REGISTERING, 
					VoipState.REGISTERED, 
					VoipState.CALL_INCOMING,
					VoipState.CALL_ACTIVE,
					VoipState.CALL_HANGUP,
					VoipState.UNREGISTERING,
					VoipState.UNREGISTERED,
					VoipState.DEINITIALIZING,
					VoipState.DEINITIALIZE_DONE};
		}

		 private void notifyIncomingCall()
		    {
			    VoipStateBundle myStateBundle = new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_INCOMING, "Incoming call from:" + "test_caller", null);
			    Handler testHandler = new Handler(VoipLibTestSuite.this);
				Log.d(TAG, "Called notifyState for state:" + myStateBundle.getState().name());
				
		    	Message m = Message.obtain(testHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
				m.sendToTarget();
		    }
		 
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			//Log.d(TAG, "Called handleMessage with info...");
			VoipStateBundle myState = (VoipStateBundle) voipMessage.obj;
			String infoMsg = myState.getState() + ":" + myState.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myState.getState(), expectedStates[curStateIndex]);
			curStateIndex++;
			     if (myState.getState()==VoipState.INITIALIZED)   { assertEquals(CallState.NONE, myVoip.getCallState()); assertTrue(myVoip.registerAccount());	}
			else if (myState.getState()==VoipState.REGISTERED)    this.notifyIncomingCall();	
			else if (myState.getState()==VoipState.CALL_INCOMING) {  //assertEquals(CallState.INCOMING, myVoip.getCallState()); // non simulato...
				                                                     assertTrue(myVoip.answerCall());
																	}
			else if (myState.getState()==VoipState.CALL_ACTIVE)   {assertEquals(CallState.ACTIVE, myVoip.getCallState());
																   assertTrue(myVoip.hangupCall());}
			     
			else if (myState.getState()==VoipState.CALL_HANGUP)   {assertEquals(CallState.NONE, 
					                                               myVoip.getCallState());assertTrue(myVoip.unregisterAccount());}
			     
			else if (myState.getState()==VoipState.UNREGISTERED)  {assertEquals(CallState.NONE, myVoip.getCallState());
																   assertTrue(myVoip.destroyLib());
																		}

			return false;
		}

	}
    
	private static final String TAG = "VoipTestActivity";
	private Handler handler = new Handler(this);
	private HandlerTest handlerTest = null;
	private VoipLib myVoip =null;
 
	
	
	public VoipLibTestSuite(String name) {
		super(name);
	}
   
	protected void setUp() throws Exception {
		myVoip = new MockVoipLib();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 *  This test calls the initLib() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipState.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipState.
	 */
	public void testAccountRegistration()
	{
		Log.d(TAG, "Testing testAccountRegistration...");
		this._testHandler(new AccountRegistrationHandlerTest());
	}
	
	
	/**
	 *  This test calls the initLib() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipState.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), makeCall(), hangupCall(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipState.
	 */
	public void testMakeCall()
	{
		Log.d(TAG, "Testing testMakeCall");
		this._testHandler(new MakeCallHandlerTest());
	}
	
	/**
	 *  This test calls the initLib() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipState.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), answerCall() (after sending a simulated dialing notification), hangupCall(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipState.
	 */
	public void testAnswerCall()
	{
		Log.d(TAG, "Testing testAnswerCall");
		this._testHandler(new AnswerCallHandlerTest());
	}
	
	
	
	private void _testHandler(HandlerTest handlerTest) {
		this.handlerTest= handlerTest;
		boolean result = myVoip.initLib(null,this.handler);
		Log.d(TAG,"testVoip with HandlerTest");
		assertTrue(result);
 
		while (!handlerTest.isDone())
		{
		 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Log.d(TAG,"test running...please wait....");
		}
		Log.d(TAG,"Test End");
	}

	@Override
	public boolean handleMessage(Message msg) {
		//Log.d(TAG, "Called handleMessage with HandlerTest!!");
		return this.handlerTest.handleMessage(msg);
	 
	}

	
}

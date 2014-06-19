package most.voip.api.test;

import junit.framework.TestCase;
import most.voip.api.enums.CallState;
import most.voip.api.enums.VoipEvent;
import most.voip.api.enums.VoipEventType;
import most.voip.api.VoipEventBundle;
import most.voip.api.VoipLib;
import android.os.*;
import android.util.Log;


public class VoipLibTestSuite extends TestCase implements Handler.Callback  {
	 
	abstract class HandlerTest {
		protected int curStateIndex = 0;
		protected VoipEvent [] expectedEvents = {};
		
		public boolean isDone() {
			return curStateIndex>=expectedEvents.length;
		}
		
		public abstract boolean handleMessage(Message voipMessage);

	}
	
	class AccountRegistrationHandlerTest extends HandlerTest {
		
		AccountRegistrationHandlerTest () {
			
			this.expectedEvents = new VoipEvent[] {VoipEvent.LIB_INITIALIZED , 
					VoipEvent.ACCOUNT_REGISTERING, 
					VoipEvent.ACCOUNT_REGISTERED, 
					VoipEvent.ACCOUNT_UNREGISTERING,
					VoipEvent.ACCOUNT_UNREGISTERED,
					VoipEvent.LIB_DEINITIALIZING,
					VoipEvent.LIB_DEINITIALIZE_DONE};
		}
 
		
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			VoipEventBundle myEvent = (VoipEventBundle) voipMessage.obj;
			String infoMsg = myEvent.getEvent() + ":" + myEvent.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myEvent.getEvent(), expectedEvents[curStateIndex]);
			curStateIndex++;
			     if (myEvent.getEvent()==VoipEvent.LIB_INITIALIZED)   assertTrue(myVoip.registerAccount());	
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    assertTrue(myVoip.unregisterAccount());	
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_UNREGISTERED)  assertTrue(myVoip.destroyLib());

			return false;
		}

	}
	
	class MakeCallHandlerTest extends HandlerTest {
		
		 MakeCallHandlerTest () {
			
			this.expectedEvents = new VoipEvent[] {VoipEvent.LIB_INITIALIZED , 
					VoipEvent.ACCOUNT_REGISTERING, 
					VoipEvent.ACCOUNT_REGISTERED, 
					VoipEvent.CALL_DIALING,
					VoipEvent.CALL_ACTIVE,
					VoipEvent.CALL_HANGUP,
					VoipEvent.ACCOUNT_UNREGISTERING,
					VoipEvent.ACCOUNT_UNREGISTERED,
					VoipEvent.LIB_DEINITIALIZING,
					VoipEvent.LIB_DEINITIALIZE_DONE};
		}
 
		
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			//Log.d(TAG, "Called handleMessage with info...");
			VoipEventBundle myEvent = (VoipEventBundle) voipMessage.obj;
			String infoMsg = myEvent.getEvent() + ":" + myEvent.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myEvent.getEvent(), expectedEvents[curStateIndex]);
			curStateIndex++;
			     if (myEvent.getEvent()==VoipEvent.LIB_INITIALIZED)   assertTrue(myVoip.registerAccount());	
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    assertTrue(myVoip.makeCall("destination_test_extension"));	
			else if (myEvent.getEvent()==VoipEvent.CALL_ACTIVE)   assertTrue(myVoip.hangupCall());	
			else if (myEvent.getEvent()==VoipEvent.CALL_HANGUP)   assertTrue(myVoip.unregisterAccount());	
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_UNREGISTERED)  assertTrue(myVoip.destroyLib());

			return false;
		}

	}
	
	
	class AnswerCallHandlerTest extends HandlerTest {
		
		 AnswerCallHandlerTest () {
			
			this.expectedEvents = new VoipEvent[] {VoipEvent.LIB_INITIALIZED , 
					VoipEvent.ACCOUNT_REGISTERING, 
					VoipEvent.ACCOUNT_REGISTERED, 
					VoipEvent.CALL_INCOMING,
					VoipEvent.CALL_ACTIVE,
					VoipEvent.CALL_HANGUP,
					VoipEvent.ACCOUNT_UNREGISTERING,
					VoipEvent.ACCOUNT_UNREGISTERED,
					VoipEvent.LIB_DEINITIALIZING,
					VoipEvent.LIB_DEINITIALIZE_DONE};
		}

		 private void notifyIncomingCall()
		    {
			    VoipEventBundle myStateBundle = new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_INCOMING, "Incoming call from:" + "test_caller", null);
			    Handler testHandler = new Handler(VoipLibTestSuite.this);
				Log.d(TAG, "Called notifyState for state:" + myStateBundle.getEvent().name());
				
		    	Message m = Message.obtain(testHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
				m.sendToTarget();
		    }
		 
		@Override
		public boolean handleMessage(Message voipMessage) {
			//int msg_type = voipMessage.what;
			//Log.d(TAG, "Called handleMessage with info...");
			VoipEventBundle myEvent = (VoipEventBundle) voipMessage.obj;
			String infoMsg = myEvent.getEvent() + ":" + myEvent.getInfo();
			Log.d(TAG, "handleMessage: Current State:" + infoMsg);
			
			assertEquals( myEvent.getEvent(), expectedEvents[curStateIndex]);
			curStateIndex++;
			     if (myEvent.getEvent()==VoipEvent.LIB_INITIALIZED)   { assertEquals(CallState.IDLE, myVoip.getCallState()); assertTrue(myVoip.registerAccount());	}
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_REGISTERED)    this.notifyIncomingCall();	
			else if (myEvent.getEvent()==VoipEvent.CALL_INCOMING) {  //assertEquals(CallState.INCOMING, myVoip.getCallState()); // non simulato...
				                                                     assertTrue(myVoip.answerCall());
																	}
			else if (myEvent.getEvent()==VoipEvent.CALL_ACTIVE)   {assertEquals(CallState.ACTIVE, myVoip.getCallState());
																   assertTrue(myVoip.hangupCall());}
			     
			else if (myEvent.getEvent()==VoipEvent.CALL_HANGUP)   {assertEquals(CallState.IDLE, 
					                                               myVoip.getCallState());assertTrue(myVoip.unregisterAccount());}
			     
			else if (myEvent.getEvent()==VoipEvent.ACCOUNT_UNREGISTERED)  {assertEquals(CallState.IDLE, myVoip.getCallState());
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
	 *  the received Voip State matches with the expected state (VoipEvent.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipEvent.
	 */
	public void testAccountRegistration()
	{
		Log.d(TAG, "Testing testAccountRegistration...");
		this._testHandler(new AccountRegistrationHandlerTest());
	}
	
	
	/**
	 *  This test calls the initLib() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipEvent.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), makeCall(), hangupCall(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipEvent.
	 */
	public void testMakeCall()
	{
		Log.d(TAG, "Testing testMakeCall");
		this._testHandler(new MakeCallHandlerTest());
	}
	
	/**
	 *  This test calls the initLib() method of the Voip Library. The testing callback method receives the updated Voip State. The test checks if
	 *  the received Voip State matches with the expected state (VoipEvent.INITIALIZED). Then the test continues by calling the methods
	 *  registerAccount(), answerCall() (after sending a simulated dialing notification), hangupCall(), unregisterAccount(), destroyLib(), checking any time for the expected received VoipEvent.
	 */
	public void testAnswerCall()
	{
		Log.d(TAG, "Testing testAnswerCall");
		this._testHandler(new AnswerCallHandlerTest());
	}
	
	
	
	private void _testHandler(HandlerTest handlerTest) {
		this.handlerTest= handlerTest;
		boolean result = myVoip.initLib(null,null,this.handler);
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

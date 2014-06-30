package most.voip.api;



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
 
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import most.voip.api.enums.AccountState;
import most.voip.api.enums.BuddyState;
import most.voip.api.enums.CallState;
import most.voip.api.enums.RegistrationState;
import most.voip.api.enums.ServerState;
import most.voip.api.enums.VoipEvent;
import most.voip.api.enums.VoipEventType;
import most.voip.api.interfaces.IAccount;
import most.voip.api.interfaces.IBuddy;
import most.voip.api.interfaces.ICall;
import most.voip.api.interfaces.IServer;

import org.pjsip.pjsua2.*;
 

public class VoipLibBackend extends Application implements VoipLib   {
	
private CallState currentCallState = CallState.IDLE;
private AudioMediaPlayer playerOnHold = null;
private AudioMediaPlayer playerIncomingCall = null;
private AudioMediaPlayer playerOutcomingCall = null;

	static {
		System.out.println("LOADING LIB...");
		System.loadLibrary("pjsua2");
		System.out.println("Library loaded");
	}
	

public static Endpoint ep = null; //new Endpoint();
public ArrayList<MyAccount> accList = new ArrayList<MyAccount>();
//private ArrayList<MyAccountConfig> accCfgs = new ArrayList<MyAccountConfig>();
private EpConfig epConfig = new EpConfig();
private TransportConfig sipTpConfig = new TransportConfig();
private MyAccount acc = null;
private AccountConfig acfg = null;
private static MyCall currentCall = null;	
private String sipServerIp = null;
private ServerState serverState = ServerState.DISCONNECTED;

private Handler notificationHandler = null;

MediaPlayer mediaPlayer = null;
 
private HashMap<String,String> configParams = new HashMap<String,String>();

//private boolean onHoldSoundIsPlaying = false;
//private boolean onIncomingCallRingToneIsPlaying = false;
//private boolean onOutcomingCallRingToneIsPlaying = false;

private boolean localHangup = false;
private Context context;

private final static String TAG = "VoipLib"; 
    public VoipLibBackend()
    {
    	Log.d((TAG), "VoipLib");
    }
    
    private void notifyEvent(VoipEventBundle myStateBundle)
    {   
    	Log.d(TAG, "notify state:" + myStateBundle.getEvent());
    	this.updateCallStateByVoipEvent(myStateBundle.getEvent());
    	Log.d(TAG, "New Current State:" + this.getCall().getState());
    	Message m = Message.obtain(this.notificationHandler,myStateBundle.getEventType().ordinal(), myStateBundle);
		m.sendToTarget();
    }
    
    private void updateCallStateByVoipEvent(VoipEvent voipEvent)
    {
    	switch (voipEvent){
		case CALL_ACTIVE: this.currentCallState = CallState.ACTIVE; break;
		case CALL_DIALING: this.currentCallState = CallState.DIALING; break;
		case CALL_INCOMING: this.currentCallState = CallState.INCOMING; break;
		case CALL_HOLDING: this.currentCallState = CallState.HOLDING; break;
		case CALL_UNHOLDING: this.currentCallState = CallState.ACTIVE; break;
		case CALL_HANGUP: this.currentCallState = CallState.IDLE; break;
		case CALL_REMOTE_HANGUP: this.currentCallState = CallState.IDLE; break;
		case CALL_REMOTE_DISCONNECTION_HANGUP: this.currentCallState = CallState.IDLE; break;
		default:
			break;
    	}
    }
    
    @Override
    public boolean initLib(Context context, HashMap<String,String> configParams, Handler notificationHandler)
    {
    	Log.d((TAG), "initializing");
    	//Utils.copyAssets(context);
    	this.context = context;
    	this.configParams = configParams;
    	this.notificationHandler = notificationHandler;
    	
    	this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_INITIALIZING, "Voip Lib initialization", this.configParams));
    	
    	/* Create endpoint */
		try {
			Log.d(TAG,"Lib create...");
			if (ep!=null)
			{  
				Log.d(TAG, "EndPoint is not null...destroying .....");
				this.destroyLib();
			}
			Log.d(TAG,"Instancing EndPoint..");
			ep = new Endpoint();
			ep.libCreate();
			
			
			/* Load config */
			Log.d(TAG,"Load configuration...");
			loadConfig(configParams);
			
			
			// Init the pjsua lib with the given configuration
			Log.d(TAG,"Lib init......");
			ep.libInit( epConfig );
			
			// Create SIP transport. Error handling sample is shown
			Log.d(TAG,"transport create...");
			ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
			// Start the library
			Log.d(TAG,"Lib startig....");
			ep.libStart();
			
			// instance the player
			this.setupAudioPlayers();
			 
			// print the json config
			Log.d(TAG,"Lib initialized and started");
		    this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_INITIALIZED, "Inizialization Ok", this.configParams));
		    
			return true;
		} catch (Exception e) {
			Log.e(TAG,"Error Initializing the lib:" + e);
			System.out.println("ERROR IN INITIALIZATION:" + e);
			this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_INITIALIZATION_FAILED, "Inizialization Failed:"+ e.getMessage(), this.configParams));
			return false;
		}
    }
    
    
   
	@Override
	public boolean registerAccount() {
		/*
		AccountConfig acfg = new AccountConfig();
		acfg.setIdUri("sip:ste@156.148.33.223");
		acfg.getRegConfig().setRegistrarUri("sip:156.148.33.223");
		AuthCredInfo cred = new AuthCredInfo("digest", "*", "ste", 0, "ste");
		acfg.getSipConfig().getAuthCreds().add( cred );
		*/
		// Create the account
		
		this.acc = new MyAccount(acfg);
		try {
			acc.create(acfg);
			// TODO: create IAccount interface to be passed as final parameter
			this.notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_REGISTERING, "Account Registration request sent", this.acc));
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG,"Error Registering the account:" + e);
			this.notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_REGISTRATION_FAILED, "Account Registration request failed:"+e.getMessage(), this.acc));
			return false;
		}
		
		return true;
	}

	@Override
	public boolean unregisterAccount() {
		try {
			acc.setRegistration(false);
			this.notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_UNREGISTERING, "Account Unregistration request sent",this.acc));
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Failed Unregistering the account:" + e.getMessage());
			this.notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_UNREGISTRATION_FAILED, "Account Unregistration request failed:"+e.getMessage(), this.acc));
		}
		return false;
	}

	public String getSipUriFromExtension(String extension) {
		return "sip:" + extension + "@" + this.sipServerIp;
	}
	
	@Override
	public boolean makeCall(String extension) {
		Log.d(TAG, "Called makeCall for extension " + extension);
		
		/* 
		 * Only one call at anytime 
		if (currentCall != null) {
			Log.w(TAG, "There is already a call active, make call rejected");
			return false;
		}
		*/
		
		MyCall call = new MyCall(this.acc, -1);
		CallOpParam prm = new CallOpParam();
		CallSetting opt = prm.getOpt();
		opt.setAudioCount(1);
		opt.setVideoCount(0);

		try {
			call.makeCall(this.getSipUriFromExtension(extension), prm);
		} catch (Exception e) {
			VoipLibBackend.currentCall = null;
			Log.e(TAG, "Exception in makeCall: " + e.getMessage());
			return false;
		}
		// setting the new call as the current call
		VoipLibBackend.currentCall = call;
	    return true;
	}
	
	/**
	 * 
	 * Reject any incoming call if there is already any pending call, otherwise start ringing and set the new call as current call
	 * @param call
	 * @return true if the new call was not rejected, false otherwise
	 */
	private boolean handleIncomingCall(MyCall call, boolean allowMultipleCalls)
	{
		
		Log.d(TAG,"Handling incoming call from the Voip Lib");
		/* Incoming call */
		
		//final MyCall call = (MyCall) m.obj;
		CallOpParam prm = new CallOpParam();
		
		 
		if (currentCall != null && !allowMultipleCalls) {
			Log.d(TAG, "Incoming second call not allowed in configuration");
			CallInfo ci = null;
			try {
				ci = call.getInfo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"Exception in incoming call:" +e);
				return false;
			}
			//this.notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_INCOMING, "Incoming call during another call!", getICallInfo(ci)));
			
			/*
			// Only one call at anytime 
			this.notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_INCOMING_REJECTED, "Incoming call rejected beacuse there is already an other active call", null));
			
			try {
				
			    prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
				call.hangup(prm);
			} catch (Exception e) {
				
				Log.e(TAG,"Exception hanging up the call:" +e);
			}
			return false;
			*/
			return false;
		}
         
			
		/* Answer with ringing */
		prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
		try {
			Log.d(TAG,"Setting call in ringing state");
			call.answer(prm);
			Log.d(TAG,"Called answer in ringing state");
		} catch (Exception e) {
			
			Log.e(TAG,"Exception answering the call:" +e);
			return false;
		}
		
		currentCall = call;
		return true;
	}

	@Override
	public boolean answerCall() {
		if (VoipLibBackend.currentCall==null)
		{
			Log.d(TAG, "Answer Call ignored: no call found");
			return false;
		}
		CallInfo ci;
		try {
			ci = VoipLibBackend.currentCall.getInfo();
			if (ci.getState()!=pjsip_inv_state.PJSIP_INV_STATE_EARLY)
			{
				Log.d(TAG, "The current call is not in dialing state. Answering request ignored: State:" + ci.getState());
				return false;
			}
		} catch (Exception e1) {
			Log.d(TAG, "Error getting call info:" + e1);
			return false;
		}
		
		CallOpParam prm = new CallOpParam();
		prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		try {
			Log.d(TAG, "Try to answer the Call" );
			VoipLibBackend.currentCall.answer(prm);
			
		} catch (Exception e) {
			Log.e(TAG, "Exception in answerCall: " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean holdCall() {
		if (VoipLibBackend.currentCall == null) {
			Log.d(TAG,"There is no call to hold");
			return false;
		}
		CallOpParam prm = new CallOpParam(true);
		try {
		Log.d(TAG,"Call Hold request...");
		currentCall.setHold(prm);
		} catch (Exception e) {
		e.printStackTrace();
		Log.e(TAG, "Exception in holdCall: " + e.getMessage());
		return false;
		}
       return true;
	}

	@Override
	public boolean unholdCall() {
		if (VoipLibBackend.currentCall == null) {
			Log.d(TAG,"There is no call to unhold");
			return false;
		}
		CallOpParam prm = new CallOpParam(true);
		CallSetting cs;
		Log.d(TAG,"Retrieving current call settings.");
		try {
			cs = VoipLibBackend.currentCall.getInfo().getSetting();
			cs.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e(TAG,"Error retrieving call settings!");
			return false;
		}
 
	    prm.setOpt(cs);
	    //prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		//prm.setOptions(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());
		//prm.setOpt(CallSetting.this.
		try {
		Log.d(TAG,"Call unhold request with OK status code and CALL UNHOLD FLAG...");
		currentCall.reinvite(prm);
		return true;
		} catch (Exception e) {
		e.printStackTrace();
		Log.e(TAG, "Exception in unholdCall: " + e.getMessage());
		return false;
		}
	}

	@Override
	public boolean hangupCall() {
		// Stop the 'onhold sound, if any'
		stopOnHoldSound();
		if (VoipLibBackend.currentCall != null) {
			this.localHangup = true;
			CallOpParam prm = new CallOpParam();
			prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
			try {
				Log.d(TAG, "Try to hangup the Call" );
				VoipLibBackend.currentCall.hangup(prm);
				return true;
				} catch (Exception e) {
					Log.e(TAG, "Exception hanging up the call:" + e);
					this.localHangup = false;
					return false;
			}
			//VoipLibBackend.currentCall = null;
		}
		return false;
	}

	@Override
	public boolean destroyLib() {
		
		this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_DEINITIALIZING, "Voip Lib destroying", this.configParams));
		// Explicitly destroy and delete endpoint
		try {
			
			/* Explicitly delete the account.
			* This is to avoid GC to delete the endpoint first before deleting
			* the account.
			*/
			if (acc!=null)
			{
				Log.d(TAG,"Deleting account...");
				acc.delete();
				acc = null;
				Log.d(TAG,"Account deleted");
			}
			
			/* Try force GC to avoid late destroy of PJ objects as they should be
			 * deleted before lib is destroyed.
			 */
			Runtime.getRuntime().gc();
			
			Log.d(TAG,"Destroying lib...");
			/* Shutdown pjsua. Note that Endpoint destructor will also invoke
			 * libDestroy(), so this will be a test of double libDestroy().
			 */
			try {
				ep.libDestroy();
				Log.d(TAG, "LibDestroy successfully called");
			} catch (Exception e)
			{
				Log.e(TAG, "Exception calling ep.libDestroy():" + e.getMessage());
			}
			
			/* Force delete Endpoint here, to avoid deletion from a non-
			 * registered thread (by GC?). 
			 */
			ep.delete();
			ep = null;
			Log.d(TAG,"Lib destroyed");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Lib Destroy failed:" + e.getMessage());
			this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_DEINITIALIZATION_FAILED, "Voip Lib destroyed", this.configParams));
			return false;
		}
		 
		this.notifyEvent(new VoipEventBundle(VoipEventType.LIB_EVENT, VoipEvent.LIB_DEINITIALIZED, "Voip Lib destroyed", this.configParams));
		return true;
		
	}
	
	
 // ########## ACCESSORY METHODS AND CLASSES ###############################
	
/*
	private void buildAccConfigs() {
		// Sync accCfgs from accList 
		accCfgs.clear();
		for (int i = 0; i < accList.size(); i++) {
			MyAccount acc = accList.get(i);
			MyAccountConfig my_acc_cfg = new MyAccountConfig();
			my_acc_cfg.accCfg = acc.cfg;
			
			my_acc_cfg.buddyCfgs.clear();
			for (int j = 0; j < acc.buddyList.size(); j++) {
				MyBuddy bud = acc.buddyList.get(j);
				my_acc_cfg.buddyCfgs.add(bud.cfg);
			}
			
			accCfgs.add(my_acc_cfg);
		}
	}
*/	
	
	private ICall getICallInfo(final CallInfo ci) {
		
		return new ICall(){

			@Override
			public String getRemoteUri() {
				try {
					return ci.getRemoteUri().replaceAll("<", "").replaceAll(">", "");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public String getLocalUri() {
				try {
					return ci.getLocalUri().replaceAll("<", "").replaceAll(">", "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			@Override
			public CallState getState() {
				 
				return currentCallState;
			}
			
		
		};
	}
	
	
	class MyCall extends Call  {
		MyCall(MyAccount acc, int call_id) {
			super(acc, call_id);
		}

		
		@Override
		public void onCallState(OnCallStateParam prm) {
			//MyApp.observer.notifyCallState(this);
			Log.d(TAG, "On Call State Called:" + prm.getE().toString());
			CallInfo ci;
			try {
				ci = getInfo();
				//stopOnHoldSound();
				
				Log.d(TAG, "On Call State: CallInfo:" + ci.getStateText());
				if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
					//currentCallState = CallState.DIALING;
				    playOutcomingRingtoneSound();
					notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_DIALING, "Dialing call to:" + ci.getRemoteUri(), getICallInfo(ci)));
				}
				else if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
					//currentCallState = CallState.ACTIVE;
					notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_ACTIVE, "Call active with:" + ci.getRemoteUri(), getICallInfo(ci)));
				}
				else if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
					stopIncomingRingtoneSound();
					stopOutcomingRingtoneSound();
					stopOnHoldSound();
					//currentCallState = CallState.IDLE;
					//VoipLibBackend.currentCall = null;
					if (localHangup){
						notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_HANGUP, "Call hangup with:" + ci.getRemoteUri(), getICallInfo(ci)));
					}
					else 
					{
						if (getServer().getState()==ServerState.DISCONNECTED)
							notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_REMOTE_DISCONNECTION_HANGUP, "Call remote hangup with:" + ci.getRemoteUri(), getICallInfo(ci)));
						else
							notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_REMOTE_HANGUP, "Call remote hangup with:" + ci.getRemoteUri(), getICallInfo(ci)));
					}
					
				}
				else {
					Log.d(TAG, "UNHANDLED STATE in onCallState");
				}
				
			} catch (Exception e) {
				Log.e(TAG, "Exception in onCallState():" + e.getMessage());
				return;
			}
			
			
		}
		
		@Override
		public void onCallMediaState(OnCallMediaStateParam prm) {
		    Log.d(TAG, "On Call Media State:" + prm.toString());
			CallInfo ci;
			try {
				ci = getInfo();
				Log.d(TAG, "On Call Media State: CallInfo:" + ci.getStateText());
				
			} catch (Exception e) {
				return;
			}
			
			CallMediaInfoVector cmiv = ci.getMedia();
			
			for (int i = 0; i < cmiv.size(); i++) {
				CallMediaInfo cmi = cmiv.get(i);
				Log.d(TAG, "Received CALLMEDIAINFO: TYPE:" + cmi.getType() + " STATUS: " + cmi.getStatus());
				
				
				
				// IF THE CALL AUDIO STREAM IS ACTIVE ...
				if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
				    (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
				     cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
				{
					// a remote user put on hold the phone
					if (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD)
					{
						notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.BUDDY_HOLDING, "Call Remote holding (Call Event)", getICallInfo(ci)));
					}
					else {
						stopOnHoldSound();
						stopIncomingRingtoneSound();
						stopOutcomingRingtoneSound();
						
						//currentCallState = CallState.ACTIVE;
						if (getCall().getState()==CallState.HOLDING)
							notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_UNHOLDING, "Call Unholding", getICallInfo(ci)));
						else
							notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_ACTIVE, "Call Active", getICallInfo(ci)));
					}
					// unfortunately, on Java too, the returned Media cannot be downcasted to AudioMedia 
					Media m = getMedia(i);
					AudioMedia am = AudioMedia.typecastFromMedia(m);
					
					// connect ports
					try {
						//Cattura l'audio da remoto e lo trasmette al phone Android
						VoipLibBackend.ep.audDevManager().getCaptureDevMedia().startTransmit(am);
						//trasmette l'audio emesso dal phone android al client SIP remoto
						am.startTransmit(VoipLibBackend.ep.audDevManager().getPlaybackDevMedia());
					} catch (Exception e) {
						continue;
					}
				}
				
				// the local user put on hold the call
				else if(cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_LOCAL_HOLD)
				{
					notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_HOLDING, "Call holding", this));
					playOnHoldSound();
				}
			}  // end FOR
		}
	}
	
	
	private void playOnHoldSound()  
	{
		//if (this.playSoundFile("onHoldSound")) onHoldSoundIsPlaying = true;
		if (playerOnHold!=null)
		{
			AudioMedia am;
			try {
				am = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
				playerOnHold.setPos(0);
				playerOnHold.startTransmit(am);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void stopOnHoldSound()
	{
		 AudioMedia sink;
		try {
			sink = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
			playerOnHold.stopTransmit(sink);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void playOutcomingRingtoneSound()  
	{
		//if (this.playSoundFile("onHoldSound")) onHoldSoundIsPlaying = true;
				if (playerOutcomingCall!=null)
				{
					AudioMedia am;
					try {
						am = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
						playerOutcomingCall.setPos(0);
						playerOutcomingCall.startTransmit(am);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}
	
	private void stopOutcomingRingtoneSound()
	{
		//if (this.playSoundFile("onOutcomingCallSound")) onOutcomingCallRingToneIsPlaying = true;
		 AudioMedia sink;
			try {
				sink = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
				playerOutcomingCall.stopTransmit(sink);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void playIncomingRingtoneSound()  
	{
		if (playerIncomingCall!=null)
		{
			AudioMedia am;
			try {
				am = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
				playerIncomingCall.setPos(0);
				playerIncomingCall.startTransmit(am);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void stopIncomingRingtoneSound()
	{
		//if (this.playSoundFile("onOutcomingCallSound")) onOutcomingCallRingToneIsPlaying = true;
		 AudioMedia sink;
			try {
				sink = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
				playerIncomingCall.stopTransmit(sink);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	private void setupAudioPlayers()
	{
		//params.put("onHoldSound", onHoldSoundPath);
		//params.put("onIncomingCallSound",onIncomingCallRingTonePath ); // onIncomingCallRingTonePath
		//params.put("onOutcomingCallSound",onIncomingCallRingTonePath); // onOutcomingCallRingTonePath
	 
		try {
			AudioMedia am = VoipLibBackend.ep.audDevManager().getPlaybackDevMedia();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.configParams.containsKey("onHoldSound"))
			try {
				playerOnHold = new AudioMediaPlayer();
				playerOnHold.createPlayer(this.configParams.get("onHoldSound"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (this.configParams.containsKey("onIncomingCallSound"))
			try {
				playerIncomingCall = new AudioMediaPlayer();
				playerIncomingCall.createPlayer(this.configParams.get("onIncomingCallSound"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (this.configParams.containsKey("onOutcomingCallSound"))
			try {
				playerOutcomingCall = new AudioMediaPlayer();
				playerOutcomingCall.createPlayer(this.configParams.get("onOutcomingCallSound"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	// Subclass to extend the Account and get notifications etc.
	class MyAccount extends Account implements IAccount {
		public HashMap<String,MyBuddy> buddyList = new HashMap<String,MyBuddy>();
		public AccountConfig cfg;
		private AccountState accState = AccountState.UNREGISTERED;
		
		MyAccount(AccountConfig config) {
			super();
			cfg = config;
		}
		
		@Override
		public IBuddy[] getBuddies() {
			return this.buddyList.values().toArray(new IBuddy[0]);
		}
		
		public boolean hasBuddy(String uri)
		{
			return buddyList.containsKey(uri);
		}
		
		@Override
		public boolean addBuddy(String  buddyUri) {
			
				if (this.hasBuddy(buddyUri))
				{
					Log.d(TAG,"Buddy with extension:" + buddyUri + " already added" );
					return false;
				}
				BuddyConfig buddyConfig = new BuddyConfig();
				//dest_uri = "sip:%s@%s;transport=tcp" % (str(dest_extension), self.sip_server)
				
				buddyConfig.setUri(buddyUri);
				buddyConfig.setSubscribe(true);
				notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.BUDDY_SUBSCRIBING, "Subscribing buddy with uri:" + buddyUri, buddyUri)); 
				return (this.addBuddy(buddyConfig)!=null);
	
		}

		@Override
		public boolean removeBuddy(String  buddyUri) {
				return (this.delBuddy(buddyUri)!=null);
		}

		@Override
		public IBuddy getBuddy(String buddyUri) {
			
				IBuddy b =  this.buddyList.get(buddyUri);
				b.refreshStatus();
				return b;
		}

		
		/***
		 * add a buddy to this account , if not already added
		 * @param bud_cfg
		 * @return the added buddy, null idf the buddy was previuosly added or an error occurred
		 */
		public MyBuddy addBuddy(BuddyConfig bud_cfg)
		{
			if  (buddyList.containsKey(bud_cfg.getUri()))
			{
				Log.d(TAG,"Buddy with extension:" + bud_cfg.getUri() + " already added" );
				return null;
			}
			/* Create Buddy */
			MyBuddy bud = new MyBuddy(bud_cfg);
			try {
				bud.create(this, bud_cfg);
			} catch (Exception e) {
				bud = null;
			}
			
			if (bud != null) {
				buddyList.put(bud_cfg.getUri(), bud);
				if (bud_cfg.getSubscribe())
					try {
						bud.subscribePresence(true);
					} catch (Exception e) {
						Log.e(TAG, "Error subscribing the buddy:" + e);
					}
			}
			return bud;
		}
		
		/**
		 * delete the buddy with the given uri  from the account
		 * @param uri
		 * @return the removed buddy, or null if the buddy to remove was not found.
		 */
		public MyBuddy delBuddy(String uri) {
			 MyBuddy mb =  buddyList.remove(uri);
			 if (mb!=null)
			 {
				/*
				try {
					mb.subscribePresence(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				mb.delete();
			 }
			 return mb;
		}
		
		@Override
		public void onIncomingSubscribe(OnIncomingSubscribeParam prm) 
		{
			Log.d(TAG,"\n ****** \nonIncomingSubscribe from:" + prm.getFromUri() +  " " + prm.toString());	
		}
		
	
		@Override
		public void onRegState(OnRegStateParam prm) {
			//MyApp.observer.notifyRegState(prm.getCode(), prm.getReason(), prm.getExpiration());
			Log.d(TAG,"onRegState Code:" +  prm.getCode() + ": Reg Expire:" + prm.getExpiration() + " Reason:" + prm.getReason());
			try {
				Log.d(TAG,"onRegState ACCOUNT REG ACTIVE ? : " + acc.getInfo().getRegIsActive());
				int regStatus = prm.getCode().swigValue();
				if (regStatus==RegistrationState.REQUEST_TIMEOUT.intValue() || regStatus==RegistrationState.SERVICE_UNAVAILABLE.intValue())
				{
					serverState = ServerState.DISCONNECTED;
					accState = AccountState.UNREGISTERED;
					
					notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.LIB_CONNECTION_FAILED, "Connection Failed: Code:" +
	                        prm.getCode().swigValue() + " " + prm.getReason(), null)); 
				}
				// Registration Ok
				else if (regStatus == RegistrationState.OK.intValue())
				{
					serverState = ServerState.CONNECTED;
					// Account registered
					if (prm.getExpiration()>0  && acc.getInfo().getRegIsActive()){
						accState = AccountState.REGISTERED;
						notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_REGISTERED, "Registration Success:::" + prm.getReason(), regStatus));
					     
					}
					
					// Account unregistered
					else  if (prm.getExpiration()==0  && !acc.getInfo().getRegIsActive()) {
						accState = AccountState.UNREGISTERED;
						notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_UNREGISTERED, "Unregistration Success:::" + prm.getReason(), regStatus));
					}
					
				}
				// There was an error registering or unregistering the account
				else
				{
					// Account NOT registered
					if (!acc.getInfo().getRegIsActive()) 
					{
						notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_REGISTRATION_FAILED, "Registration Failed: Code:" +
		                        prm.getCode().swigValue() + " " + prm.getReason(), regStatus)); 
					}
					// Account NOT unregistered
					else {
						notifyEvent(new VoipEventBundle(VoipEventType.ACCOUNT_EVENT, VoipEvent.ACCOUNT_UNREGISTRATION_FAILED, "Unregistration Failed: Code:" +
		                        prm.getCode().swigValue() + " " + prm.getReason(),regStatus)); 
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"Error retrieving account info:" + e.getMessage());
			}
		}

		@Override
		public void onIncomingCall(OnIncomingCallParam prm) {
			Log.d(TAG, "INCOMING CALL:" + prm.toString());
			MyCall call = new MyCall(this, prm.getCallId());
			//MyApp.observer.notifyIncomingCall(call);
			 try {
				 boolean incoming_call_result = handleIncomingCall(call, true);	// allow multiple calls...
				 if (incoming_call_result)
				 {
					 playIncomingRingtoneSound();
					 notifyEvent(new VoipEventBundle(VoipEventType.CALL_EVENT, VoipEvent.CALL_INCOMING, "Incoming call from:" + call.getInfo().getRemoteUri(), call));
				 }
				 
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"Error reatrieving call info:" + e.getMessage());
			}
		}
		
		@Override
		public void onInstantMessage(OnInstantMessageParam prm) {
			System.out.println("======== Incoming pager ======== ");
			System.out.println("From 		: " + prm.getFromUri());
			System.out.println("To			: " + prm.getToUri());
			System.out.println("Contact		: " + prm.getContactUri());
			System.out.println("Mimetype	: " + prm.getContentType());
			System.out.println("Body		: " + prm.getMsgBody());
		}


		@Override
		public String getUri() {
			try {
				return this.getInfo().getUri();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "N.A";
			}
		}


		@Override
		public AccountState getState() {
			 
			return this.accState;
		}
	}
	
	class MyBuddy extends Buddy implements IBuddy {
		public BuddyConfig cfg;
		MyBuddy(BuddyConfig config) {
			super();
			cfg = config;
		}
		
		private BuddyState buddyState = BuddyState.NOT_FOUND;
		private String statusText = "?";
		
		// refresh the status of this buddy
		void updateBuddyStatus() {
			BuddyInfo bi=null;
			Log.d(TAG,"Called updateBuddyStatus---");
			try {
				bi = getInfo();
			} catch (Exception e) {
				this.buddyState = BuddyState.NOT_FOUND;
				this.statusText = "Not Found";
				Log.d(TAG,"Buddy not found!!!");
				return;
			}
			
		 
			
			if (bi.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACTIVE) {
				
				// BUDDY IS ON LINE
				if (bi.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE) {
					statusText = bi.getPresStatus().getStatusText();
					
					this.buddyState = BuddyState.ON_LINE;
					
					if (statusText == null || statusText.isEmpty()) {
						statusText = "Online";
					}
					else if(statusText!=null && statusText.equalsIgnoreCase("On hold")){
						this.buddyState = BuddyState.ON_HOLD;
					}
				} 
				
				// BUDDY IS OFFLINE
				else if (bi.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_OFFLINE) {
					statusText = "Offline";
					this.buddyState = BuddyState.OFF_LINE;
				} 
				// BUDDY STATUS UNKNOUN
				else {
					this.buddyState = BuddyState.UNKNOWN;
					statusText = "Unknown";
				}
			}
			Log.d(TAG,"(update) BuddyStatus: " + this.buddyState + " : Text:" + statusText);
		}

		@Override
		public void onBuddyState() {
	
			// update the status of this buddy because something is changed
			updateBuddyStatus();
			
			Log.d(TAG,"\n\nON BUDDY STATE ---> " + this.getState());
			if (this.buddyState==BuddyState.ON_LINE)
			{
				notifyEvent(new VoipEventBundle(VoipEventType.BUDDY_EVENT, VoipEvent.BUDDY_CONNECTED, "BuddyStateChanged:::" + this.statusText + " BuddyState:" + this.getState(), this));
			}
			
			else if (this.buddyState==BuddyState.ON_HOLD)
			{
				notifyEvent(new VoipEventBundle(VoipEventType.BUDDY_EVENT, VoipEvent.BUDDY_HOLDING, "BuddyStateChanged:::" + this.statusText + " BuddyState:" + this.getState(), this)); 
			}
			else if (this.buddyState==BuddyState.OFF_LINE || this.buddyState == BuddyState.UNKNOWN)
			{
				notifyEvent(new VoipEventBundle(VoipEventType.BUDDY_EVENT, VoipEvent.BUDDY_DISCONNECTED, "BuddyStateChanged:::" + this.statusText + " BuddyState:" + this.getState(), this)); 
			}
			else{
				Log.d(TAG,"BUDDY STATE NOT HANDLED::---> " + this.getState());
			}
			Log.d(TAG,"-------END BUDDY STATE HANDLING ---------------\n\n");
		}

		@Override
		public BuddyState getState() {
		
			return this.buddyState;
		}

		@Override
		public String getUri() {
			// TODO Auto-generated method stub
			return this.cfg.getUri();
		}

		@Override
		public String getStatusText() {	 
			return this.statusText;
		}

		@Override
		public void refreshStatus() {
			this.updateBuddyStatus();
		}

		@Override
		public String getExtension() {
			String uri = this.cfg.getUri();
			return uri.substring(uri.indexOf(':')+1, uri.indexOf('@'));
		}
		
	}
	private void loadConfig(HashMap<String, String> configParams) {
		 
		try {
			 
			Log.d(TAG, "Reading account config:::");

	        acfg = new AccountConfig();
	        
	        this.sipServerIp = configParams.get("sipServerIp"); //   "192.168.1.83"; //156.148.33.223";
	        String registrar_uri = "sip:" +this.sipServerIp;
	        String user_name = configParams.get("userName");
	        String user_pwd = configParams.get("userPwd");
	        String id_uri = "sip:" + user_name + "@" + this.sipServerIp;
	        
	        // Account Config
	        acfg.setIdUri(id_uri); //"sip:ste@192.168.1.83");
	        
			acfg.getRegConfig().setRegistrarUri(registrar_uri); // "sip:192.168.1.83"
			AuthCredInfo cred = new AuthCredInfo("digest", "*", user_name, 0, user_pwd);
			acfg.getSipConfig().getAuthCreds().add( cred );
			AccountPresConfig apc = new AccountPresConfig();
			
			apc.setPublishEnabled(true);
			
			acfg.getRegConfig().setTimeoutSec(60); // minimal auto-registration used to check server connection!
			acfg.setPresConfig(apc);
			// Transport Config
			if (configParams.containsKey("sipPort"))
			
				sipTpConfig.setPort(Integer.valueOf(configParams.get("sipPort")));
			else
				sipTpConfig.setPort(5060);
		 
		} catch (Exception e) {
			System.out.println(e);
			Log.e(TAG,"Error loading configuration:" + e.getMessage());
		}
	}

	/*
	 private void saveConfig(String filename) {
			JsonDocument json = new JsonDocument();
			
			try {
				// Write endpoint config  
				json.writeObject(epConfig);
				
				// Write transport config 
				ContainerNode tp_node = json.writeNewContainer("SipTransport");
				sipTpConfig.writeObject(tp_node);
				
				// Write account configs 
				buildAccConfigs();
				ContainerNode accs_node = json.writeNewArray("accounts");
				for (int i = 0; i < accCfgs.size(); i++) {
					accCfgs.get(i).writeObject(accs_node);
				}
				
				// Save file 
				//json.saveFile(filename);
				
				// Print the content!
				Log.d(TAG, "\n\n**** CONFIG JSON ****\n\n");
				Log.d(TAG, "Content Root:" + json.saveString());
				Log.d(TAG, "\n\n****END OF CONFIG JSON ****\n\n");
			} catch (Exception e) {
				
				Log.e(TAG, "Exception reading the json:" + e.toString());
			}

			//Force delete json now, as I found that Java somehow destroys it
			// after lib has been destroyed and from non-registered thread.
			 
			json.delete();
		}
*/
	
	class MyAccountConfig {
		public AccountConfig accCfg = new AccountConfig();
		public ArrayList<BuddyConfig> buddyCfgs = new ArrayList<BuddyConfig>();
		
		public void readObject(ContainerNode node) {
			try {
				ContainerNode acc_node = node.readContainer("Account");
				accCfg.readObject(acc_node);
				ContainerNode buddies_node = acc_node.readArray("buddies");
				buddyCfgs.clear();
				while (buddies_node.hasUnread()) {
					BuddyConfig bud_cfg = new BuddyConfig(); 
					bud_cfg.readObject(buddies_node);
					buddyCfgs.add(bud_cfg);
				}
			} catch (Exception e) {}
		}
		
		public void writeObject(ContainerNode node) {
			try {
				ContainerNode acc_node = node.writeNewContainer("Account");
				accCfg.writeObject(acc_node);
				ContainerNode buddies_node = acc_node.writeNewArray("buddies");
				for (int j = 0; j < buddyCfgs.size(); j++) {
					buddyCfgs.get(j).writeObject(buddies_node);
				}
			} catch (Exception e) {}
		}
	}
	@Override
	public ICall getCall() {
		return new ICall(){

			@Override
			public String getRemoteUri() {
				// TODO Not implemented yet
				return null;
			}

			@Override
			public String getLocalUri() {
				// TODO Not implemented yet
				return null;
			}

			@Override
			public CallState getState() {
			
				return currentCallState;
			}};
	}

	
	
	
	@Override
	public IServer getServer() {
		return new IServer(){

			@Override
			public ServerState getState() {
				return serverState;
			}

			@Override
			public String getIp() {
			   return sipServerIp;
			}};
	}

	

	@Override
	public IAccount getAccount() {
		return this.acc;
	}
	
}

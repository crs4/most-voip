package most.voip.api;


import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.pjsip.pjsua2.*;


public class VoipLibBackend implements VoipLib   {
	

	static {
		System.out.println("LOADING LIB...");
		System.loadLibrary("pjsua2");
		System.out.println("Library loaded");
	}
	

public static Endpoint ep = null; //new Endpoint();
public ArrayList<MyAccount> accList = new ArrayList<MyAccount>();
private ArrayList<MyAccountConfig> accCfgs = new ArrayList<MyAccountConfig>();
private EpConfig epConfig = new EpConfig();
private TransportConfig sipTpConfig = new TransportConfig();
private MyAccount acc = null;
private AccountConfig acfg = null;
private static MyCall currentCall = null;	
private String sipServerIp = null;

private final int SIP_PORT  = 5060;
private Handler notificationHandler = null;

private final static String TAG = "VoipLib"; 
    public VoipLibBackend()
    {
    	Log.d((TAG), "VoipLib");
    }
    
    private void notifyState(VoipStateBundle myStateBundle)
    {
    	Message m = Message.obtain(this.notificationHandler,myStateBundle.getMsgType().ordinal(), myStateBundle);
		m.sendToTarget();
    }
    
    @Override
    public boolean initLib(HashMap<String,String> configParams, Handler notificationHandler)
    {
    	Log.d((TAG), "initializing");
    	
    	this.notificationHandler = notificationHandler;
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
			// print the json config
			Log.d(TAG,"Lib initialized and started");
		    this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.INITIALIZED, "Inizialization Ok", null));
		    
			return true;
		} catch (Exception e) {
			Log.e(TAG,"Error Initializing the lib:" + e);
			System.out.println("ERROR IN INITIALIZATION:" + e);
			this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.INITIALIZE_FAILED, "Inizialization Failed:"+ e.getMessage(), null));
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
			this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERING, "Account Registration request sent", null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,"Error Registering the account:" + e);
			this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTRATION_FAILED, "Account Registration request failed:"+e.getMessage(), null));
			return false;
		}
		
		return true;
	}

	@Override
	public boolean unregisterAccount() {
		try {
			acc.setRegistration(false);
			this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERING, "Account Unregistration request sent", null));
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Failed Unregistering the account:" + e.getMessage());
			this.notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTRATION_FAILED, "Account Unregistration request failed:"+e.getMessage(), null));
		}
		return false;
	}

	public String getSipUriFromExtension(String extension) {
		return "sip:" + extension + "@" + this.sipServerIp;
	}
	
	@Override
	public void makeCall(String extension) {
		Log.d(TAG, "Called makeCall for extension " + extension);
		MyCall call = new MyCall(this.acc, -1);
		CallOpParam prm = new CallOpParam();
		CallSetting opt = prm.getOpt();
		opt.setAudioCount(1);
		opt.setVideoCount(0);

		try {
			call.makeCall(this.getSipUriFromExtension(extension), prm);
		} catch (Exception e) {
			currentCall = null;
			Log.d(TAG, "Exception makingCall: " + e.getMessage());
			return;
		}
	
	}

	@Override
	public void answerCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void holdCall() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unholdCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hangupCall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean destroyLib() {
		
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZING, "Voip Lib destroying", null));
		// Explicitly destroy and delete endpoint
		try {
			/* Explicitly delete the account.
			* This is to avoid GC to delete the endpoint first before deleting
			* the account.
			*/
			if (acc!=null)
			{
				acc.delete();
				acc = null;
			}
			
			/* Try force GC to avoid late destroy of PJ objects as they should be
			 * deleted before lib is destroyed.
			 */
			Runtime.getRuntime().gc();
			
			/* Shutdown pjsua. Note that Endpoint destructor will also invoke
			 * libDestroy(), so this will be a test of double libDestroy().
			 */
			try {
				ep.libDestroy();
			} catch (Exception e) {}
			
			/* Force delete Endpoint here, to avoid deletion from a non-
			 * registered thread (by GC?). 
			 */
			ep.delete();
			ep = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "Lib Destroy failed:" + e.getMessage());
			this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZE_FAILED, "Voip Lib destroyed", null));
			return false;
		}
		 
		this.notifyState(new VoipStateBundle(VoipMessageType.LIB_STATE, VoipState.DEINITIALIZE_DONE, "Voip Lib destroyed", null));
		return true;
		
	}
	
	
 // ########## ACCESSORY METHODS ###############################
	
	private void buildAccConfigs() {
		/* Sync accCfgs from accList */
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
	
	
	class MyCall extends Call {
		MyCall(MyAccount acc, int call_id) {
			super(acc, call_id);
		}

		@Override
		public void onCallState(OnCallStateParam prm) {
			//MyApp.observer.notifyCallState(this);
			Log.e(TAG, "On Call State Called:" + prm.getE().toString());
			CallInfo ci;
			try {
				ci = getInfo();
				
				
				Log.d(TAG, "On Call State: CallInfo:" + ci.getStateText());
				if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
					notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_DIALING, "Dialing call to:" + ci.getRemoteUri(), null));
				}
				else if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
					notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_ACTIVE, "Call active with:" + ci.getRemoteUri(), null));
				}
				else if (ci.getState()==pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
					notifyState(new VoipStateBundle(VoipMessageType.CALL_STATE, VoipState.CALL_HANGUP, "Call hangup with:" + ci.getRemoteUri(), null));
				}
				
			} catch (Exception e) {
				return;
			}
			
			
		}
		
		@Override
		public void onCallMediaState(OnCallMediaStateParam prm) {
		    Log.d(TAG, "On Media State:" + prm.toString());
			CallInfo ci;
			try {
				ci = getInfo();
				Log.d(TAG, "On Media State: CallInfo:" + ci.getStateText());
			} catch (Exception e) {
				return;
			}
			
			CallMediaInfoVector cmiv = ci.getMedia();
			
			for (int i = 0; i < cmiv.size(); i++) {
				CallMediaInfo cmi = cmiv.get(i);
				if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
				    (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
				     cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
				{
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
			}
		}
	}
	
	// Subclass to extend the Account and get notifications etc.
	class MyAccount extends Account {
		public ArrayList<MyBuddy> buddyList = new ArrayList<MyBuddy>();
		public AccountConfig cfg;
		
		
		MyAccount(AccountConfig config) {
			super();
			cfg = config;
		}
		
		public MyBuddy addBuddy(BuddyConfig bud_cfg)
		{
			/* Create Buddy */
			MyBuddy bud = new MyBuddy(bud_cfg);
			try {
				bud.create(this, bud_cfg);
			} catch (Exception e) {
				bud = null;
			}
			
			if (bud != null) {
				buddyList.add(bud);
				if (bud_cfg.getSubscribe())
					try {
						bud.subscribePresence(true);
					} catch (Exception e) {}
			}
			
			return bud;
		}
		
		public void delBuddy(MyBuddy buddy) {
			buddyList.remove(buddy);
		}
		
		public void delBuddy(int index) {
			buddyList.remove(index);
		}
		
		@Override
		public void onRegState(OnRegStateParam prm) {
			//MyApp.observer.notifyRegState(prm.getCode(), prm.getReason(), prm.getExpiration());
			Log.d(TAG,"onRegState Code:" +  prm.getCode() + ": Reg Expire:" + prm.getExpiration() + " Reason:" + prm.getReason());
			try {
				Log.d(TAG,"onRegState ACCOUNT REG ACTIVE ? : " + acc.getInfo().getRegIsActive());
				
				// Registration Ok
				if (prm.getCode().swigValue() == RegistrationState.OK.intValue())
				{
					if (prm.getExpiration()>0  && acc.getInfo().getRegIsActive()){
						notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERED, "Registration Success:::" + prm.getReason(), null));
					}
					
					else  if (prm.getExpiration()==0  && !acc.getInfo().getRegIsActive()) {
						notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTERED, "Unregistration Success:::" + prm.getReason(), null));
					}
					
				}
				// There was an error registering or unsregistering the account
				else
				{
					if (!acc.getInfo().getRegIsActive()) 
					{
						notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTRATION_FAILED, "Registration Failed: Code:" +
		                        prm.getCode().swigValue() + " " + prm.getReason(), null)); 
					}
					else {
						notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.UNREGISTRATION_FAILED, "Unregistration Failed: Code:" +
		                        prm.getCode().swigValue() + " " + prm.getReason(), null)); 
					}
					
				
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"Error reatrieving account info:" + e.getMessage());
			}
			
			
			
			
				
		}

		@Override
		public void onIncomingCall(OnIncomingCallParam prm) {
			System.out.println("======== Incoming call ======== ");
			//MyCall call = new MyCall(this, prm.getCallId());
			//MyApp.observer.notifyIncomingCall(call);
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
	}
	
	class MyBuddy extends Buddy {
		public BuddyConfig cfg;
		
		MyBuddy(BuddyConfig config) {
			super();
			cfg = config;
		}
		
		String getStatusText() {
			BuddyInfo bi;
			
			try {
				bi = getInfo();
			} catch (Exception e) {
				return "?";
			}
			
			String status = "";
			if (bi.getSubState() == pjsip_evsub_state.PJSIP_EVSUB_STATE_ACTIVE) {
				if (bi.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_ONLINE) {
					status = bi.getPresStatus().getStatusText();
					if (status == null || status.isEmpty()) {
						status = "Online";
					}
				} else if (bi.getPresStatus().getStatus() == pjsua_buddy_status.PJSUA_BUDDY_STATUS_OFFLINE) {
					status = "Offline";
				} else {
					status = "Unknown";
				}
			}
			return status;
		}

		@Override
		public void onBuddyState() {
			//MyApp.observer.notifyBuddyState(this);
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
	
	 private void saveConfig(String filename) {
			JsonDocument json = new JsonDocument();
			
			try {
				/* Write endpoint config */
				json.writeObject(epConfig);
				
				/* Write transport config */
				ContainerNode tp_node = json.writeNewContainer("SipTransport");
				sipTpConfig.writeObject(tp_node);
				
				/* Write account configs */
				buildAccConfigs();
				ContainerNode accs_node = json.writeNewArray("accounts");
				for (int i = 0; i < accCfgs.size(); i++) {
					accCfgs.get(i).writeObject(accs_node);
				}
				
				/* Save file */
				//json.saveFile(filename);
				
				// Print the content!
				Log.d(TAG, "\n\n**** CONFIG JSON ****\n\n");
				Log.d(TAG, "Content Root:" + json.saveString());
				Log.d(TAG, "\n\n****END OF CONFIG JSON ****\n\n");
			} catch (Exception e) {
				
				Log.e(TAG, "Exception reading the json:" + e.toString());
			}

			/* Force delete json now, as I found that Java somehow destroys it
			 * after lib has been destroyed and from non-registered thread.
			 */
			json.delete();
		}

	
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
	
}

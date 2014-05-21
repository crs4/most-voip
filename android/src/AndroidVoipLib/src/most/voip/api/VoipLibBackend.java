package most.voip.api;


import java.util.ArrayList;

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
	
	
public static Endpoint ep = new Endpoint();
public ArrayList<MyAccount> accList = new ArrayList<MyAccount>();
private ArrayList<MyAccountConfig> accCfgs = new ArrayList<MyAccountConfig>();
private EpConfig epConfig = new EpConfig();
private TransportConfig sipTpConfig = new TransportConfig();
private MyAccount acc = null;

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
    public boolean initialize(String configParams, Handler notificationHandler)
    {
    	Log.d((TAG), "initializing");
    	
    	this.notificationHandler = notificationHandler;
    	/* Create endpoint */
		try {
			ep.libCreate();
			Log.d(TAG,"Lib initiazed");
			
			/* Load config */
			
			
			if (configParams!=null) {
				loadConfig(configParams);
			} else {
				/* Set 'default' values */
				sipTpConfig.setPort(SIP_PORT);
			}
			
			ep.libInit( epConfig );
			
			// Create SIP transport. Error handling sample is shown
			ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
			// Start the library
			ep.libStart();
			// print the json config
		    
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
		
		AccountConfig acfg = new AccountConfig();
		acfg.setIdUri("sip:ste@156.148.33.223");
		acfg.getRegConfig().setRegistrarUri("sip:156.148.33.223");
		AuthCredInfo cred = new AuthCredInfo("digest", "*", "ste", 0, "ste");
		acfg.getSipConfig().getAuthCreds().add( cred );
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void makeCall(String extension) {
		// TODO Auto-generated method stub
		
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
	public boolean destroy() {
		/* Explicitly delete the account.
		* This is to avoid GC to delete the endpoint first before deleting
		* the account.
		*/
		acc.delete();
		// Explicitly destroy and delete endpoint
		try {
			ep.libDestroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		ep.delete();
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
			Log.d(TAG,"onRegState Code:" +  prm.getCode() + ":" + prm.getReason());
			
			if (prm.getCode().swigValue() == RegistrationState.REGISTERED.intValue())
				notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTERED, "Registration Success:" + prm.getReason(), null));
			else
				notifyState(new VoipStateBundle(VoipMessageType.ACCOUNT_STATE, VoipState.REGISTRATION_FAILED, "Registration Failed: Code:" +
                        prm.getCode().swigValue() + " " + prm.getReason(), null)); 
				
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
	private void loadConfig(String jsonContent) {
		JsonDocument json = new JsonDocument();
		
		try {
			/* Load json content */
			 
			json.loadString(jsonContent);
			ContainerNode root = json.getRootContainer();
			
			/* Read endpoint config */
			epConfig.readObject(root);
			
			/* Read transport config */
			ContainerNode tp_node = root.readContainer("SipTransport");
			sipTpConfig.readObject(tp_node);
			
			/* Read account configs */
			accCfgs.clear();
			ContainerNode accs_node = root.readArray("accounts");
			while (accs_node.hasUnread()) {
				MyAccountConfig acc_cfg = new MyAccountConfig();
				acc_cfg.readObject(accs_node);
				accCfgs.add(acc_cfg);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		/* Force delete json now, as I found that Java somehow destroys it
		 * after lib has been destroyed and from non-registered thread.
		 */
		json.delete();
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

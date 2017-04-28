/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */

package it.crs4.most.voip.examples;

import java.util.ArrayList;
import java.util.HashMap;

import it.crs4.most.voip.Utils;
import it.crs4.most.voip.VoipLib;
import it.crs4.most.voip.VoipLibBackend;
import it.crs4.most.voip.VoipEventBundle;
import it.crs4.most.voip.enums.AccountState;
import it.crs4.most.voip.enums.BuddyState;
import it.crs4.most.voip.enums.CallState;
import it.crs4.most.voip.enums.VoipEventType;
import it.crs4.most.voip.enums.VoipEvent;
import it.crs4.most.voip.interfaces.IAccount;
import it.crs4.most.voip.interfaces.IBuddy;
import it.crs4.most.voip.interfaces.ICall;

import android.app.Activity;
import android.app.Service;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server (by specifying its IP address) </li>
 * <li>subscribe new buddies for this account </li>
 * <li>make a call to a buddy </li>
 * <li>answer a call incoming from a remote user</li>
 * <li>hold/unhold the call </li>
 * <li>monitor the current status of the account </li>
 * <li>monitor the current status of the subscribed buddies </li>
 * <li>monitor the current status of the call </li>
 * <li>unregister the previously registered account from the Sip Server </li>
 * <li>deinitialize the Voip Lib </li>
 * </ul>
 *
 * @author crs4
 */
public class MainActivity extends Activity {
    private static final String TAG = "VoipTestActivity";


    private ArrayList<String> infoArray = null;
    private ArrayList<IBuddy> buddiesArray = null;
    private VoipLib myVoip = null;
    private ArrayAdapter<String> arrayAdapter = null;
    private ArrayAdapter<IBuddy> buddyArrayAdapter = null;
    private String serverIp = null;
    private String sipServerPort = "5060";

    private Button butMakeCall = null;
    private Button butAnswerCall = null;
    private Button butHoldCall = null;
    private Button butHangupCall = null;
    private Button butInit = null;


    private CallHandler voipHandler = null;

    private static class AbstractAppHandler extends Handler {
        protected MainActivity app = null;
        protected VoipLib myVoip = null;
        @SuppressWarnings("unused")
        protected int curStateIndex = 0;

        public AbstractAppHandler(MainActivity app, VoipLib myVoip) {
            super();
            this.app = app;
            this.myVoip = myVoip;
        }

        protected VoipEventBundle getEventBundle(Message voipMessage) {
            //int msg_type = voipMessage.what;
            VoipEventBundle myState = (VoipEventBundle) voipMessage.obj;
            String infoMsg = "Event:" + myState.getEvent() + ": Type:" + myState.getEventType() + " : " + myState.getInfo();
            Log.d(TAG, "Called handleMessage with event info:" + infoMsg);
            this.app.addInfoLine(infoMsg);
            return myState;
        }
    }


    private class CallHandler extends AbstractAppHandler {

        boolean reinitRequest = false;

        public CallHandler(MainActivity app, VoipLib myVoip) {
            super(app, myVoip);
        }


        @Override
        public void handleMessage(Message voipMessage) {

            if (voipMessage.what == -1) {
                Log.d(TAG, "App exiting request");
                myVoip.destroyLib();
                finish();
                Runtime.getRuntime().gc();
                android.os.Process.killProcess(android.os.Process.myPid());
                return;
            }

            VoipEventBundle myEventBundle = getEventBundle(voipMessage);
            Log.d(TAG, "HANDLE EVENT TYPE:" + myEventBundle.getEventType() + " EVENT:" + myEventBundle.getEvent());

            updateCallStateInfo();
            updateServerStateInfo();
            updateAccountStateInfo();
            updateButtonsByVoipState();


            if (myEventBundle.getEventType() == VoipEventType.BUDDY_EVENT) {
                Log.d(TAG, "In handle Message for BUDDY STATE");
                IBuddy myBuddy = (IBuddy) myEventBundle.getData();
                this.app.addInfoLine("Buddy (" + myBuddy.getUri() + ") ->" + myBuddy.getStatusText());
                updateBuddyStateInfo(myBuddy);
            }
            // Register the account after the Lib Initialization
            if (myEventBundle.getEvent() == VoipEvent.LIB_INITIALIZED) {
                myVoip.registerAccount();
            } else if (myEventBundle.getEvent() == VoipEvent.ACCOUNT_REGISTERED) {
                this.app.addInfoLine("Ready to accept calls (adding buddy...)");
                //subscribe buddies so that we can receive presence notifications from them
                subscribeBuddies();
            } else if (myEventBundle.getEvent() == VoipEvent.CALL_INCOMING) handleIncomingCall();
                //else if  (myState.getState()==VoipState.CALL_ACTIVE)    {}

                // Unregister the account
            else if (myEventBundle.getEvent() == VoipEvent.CALL_HANGUP) {
                ICall callInfo = (ICall) myEventBundle.getData();
                Log.d(TAG, "Hangup from uri:" + callInfo.getRemoteUri());
                IBuddy myBuddy = myVoip.getAccount().getBuddy(callInfo.getRemoteUri());
                if (myBuddy != null) {
                    Log.d(TAG, "Current Buddy Status Text:" + myBuddy.getStatusText());
                    updateBuddyStateInfo(myBuddy);
                }

                // myVoip.unregisterAccount();
            }
            // Deinitialize the Voip Lib and release all allocated resources
            else if (myEventBundle.getEvent() == VoipEvent.LIB_DEINITIALIZED || myEventBundle.getEvent() == VoipEvent.LIB_DEINITIALIZATION_FAILED) {
                Log.d(TAG, "Setting to null MyVoipLib");
                this.app.myVoip = null;

                if (this.reinitRequest) {
                    this.reinitRequest = false;
                    this.app.runExample();
                }
            }

        } // end of handleMessage()

    }

    private void subscribeBuddies() {
        String buddyExtension = "vitto";
        String buddyExtension2 = "vitto2";
        Log.d(TAG, "adding buddies...");
        myVoip.getAccount().addBuddy(getBuddyUri(buddyExtension));
        myVoip.getAccount().addBuddy(getBuddyUri(buddyExtension2));
    }

    private void handleIncomingCall() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.waitForInitialization();
        this.initializeGUI();

        //this.runExample();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_quit:
                if (this.voipHandler != null) {
                    Message m = Message.obtain(this.voipHandler, -1);
                    m.sendToTarget();
                    break;
                } else {
                    Log.d(TAG, "Exiting app");
                    finish();
                    Runtime.getRuntime().gc();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            default:
                break;
        }

        return true;
    }

    private String getBuddyUri(String extension) {
        return "sip:" + extension + "@" + this.serverIp + ":" + this.sipServerPort;
    }

    private boolean isAtleastOneBuddyOnPhone() {

        IBuddy[] buddies = this.myVoip.getAccount().getBuddies();
        for (IBuddy buddy : buddies) {
            if (buddy.getState() == BuddyState.ON_LINE)
                return true;
        }

        return false;
    }

    public void doVoipTest(View view) {
        EditText txtView = (EditText) this.findViewById(R.id.txtServerIp);
        this.serverIp = txtView.getText().toString();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtView.getWindowToken(), 0);

        this.runExample();
    }

    public void makeCall(View view) {
        EditText txtExtension = (EditText) this.findViewById(R.id.txtExtension);
        String extension = txtExtension.getText().toString();
        this.myVoip.makeCall(extension);
    }

    public void answerCall(View view) {
        this.myVoip.answerCall();
    }

    public void hangupCall(View view) {
        this.myVoip.hangupCall();
    }


    public void updateButtonsByVoipState() {
        Log.d(TAG, "updateButtonsByVoipState...");

        //ServerState myServerState = this.myVoip.getServer().getState();
        IAccount myAccount = this.myVoip.getAccount();
        AccountState myAccountState = (myAccount == null ? AccountState.UNREGISTERED : this.myVoip.getAccount().getState());

        if (myAccountState == AccountState.UNREGISTERED) {
            this.butMakeCall.setEnabled(false);
            this.butAnswerCall.setEnabled(false);
            this.butHoldCall.setEnabled(false);
            this.butHangupCall.setEnabled(false);
            this.butInit.setEnabled(true);
        } else {
            CallState myCallState = this.myVoip.getCall().getState();

            if (myCallState == CallState.IDLE) {
                this.butMakeCall.setEnabled(true);
                this.butAnswerCall.setEnabled(false);
                this.butHangupCall.setEnabled(false);
                this.butHoldCall.setEnabled(false);
            } else if (myCallState == CallState.INCOMING) {
                this.butMakeCall.setEnabled(false);
                this.butAnswerCall.setEnabled(true);
                this.butHangupCall.setEnabled(true);
                this.butHoldCall.setEnabled(false);
            } else if (myCallState == CallState.DIALING) {
                this.butMakeCall.setEnabled(false);
                this.butAnswerCall.setEnabled(false);
                this.butHangupCall.setEnabled(true);
                this.butHoldCall.setEnabled(false);
            } else if (myCallState == CallState.HOLDING) {
                this.butMakeCall.setEnabled(false);
                this.butAnswerCall.setEnabled(false);
                this.butHangupCall.setEnabled(true);
                this.butHoldCall.setEnabled(true);
                this.butHoldCall.setText("Unhold");
            } else if (myCallState == CallState.ACTIVE) {
                this.butMakeCall.setEnabled(false);
                this.butAnswerCall.setEnabled(false);
                this.butHangupCall.setEnabled(true);
                this.butHoldCall.setEnabled(true);
                this.butHoldCall.setText("Hold");
            }
        }
    }

    public void toggleHoldCall(View view) {
        if (myVoip == null || myVoip.getCall().getState() == CallState.IDLE)
            return;
        if (myVoip.getCall().getState() == CallState.ACTIVE) {
            Log.d(TAG, "trying to hold the call...");
            this.myVoip.holdCall();
        } else if (myVoip.getCall().getState() == CallState.HOLDING) {
            Log.d(TAG, "trying to unhold the call...");
            this.myVoip.unholdCall();
        }
    }


    private void initializeGUI() {
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listOperations);
        ListView buddiesView = (ListView) findViewById(R.id.listBuddies);

        this.infoArray = new ArrayList<String>();
        this.arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(this.arrayAdapter);


        buddiesView.setAdapter(arrayAdapter);
        buddiesView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EditText txtExtension = (EditText) findViewById(R.id.txtExtension);
                txtExtension.setText(buddiesArray.get(position).getExtension());

            }
        });

        this.buddiesArray = new ArrayList<IBuddy>();

        this.buddyArrayAdapter = new BuddyArrayAdapter(this, R.layout.buddy_row, this.buddiesArray);
        buddiesView.setAdapter(this.buddyArrayAdapter);

        this.setupButtons();
        //this.addInfoLine("Most Voip Lib Test Example 1");
    }

    private void waitForInitialization() {
        /* Wait for GDB to init */
        Log.d(TAG, "Waiting some second before initializing the lib...");
        if ((this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            this.waitForSeconds(3);
        }
    }

    void setupButtons() {
        this.butInit = (Button) findViewById(R.id.butGo);
        this.butInit.setEnabled(true);
        this.butAnswerCall = (Button) findViewById(R.id.butAccept);
        this.butAnswerCall.setEnabled(false);
        this.butMakeCall = (Button) findViewById(R.id.butMakeCall);
        this.butMakeCall.setEnabled(false);
        this.butHoldCall = (Button) findViewById(R.id.butToggleHold);
        this.butHoldCall.setEnabled(false);
        this.butHangupCall = (Button) findViewById(R.id.butHangup);
        this.butHangupCall.setEnabled(false);
    }


    private HashMap<String, String> buildParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sipServerIp", serverIp);
        params.put("sipServerPort", sipServerPort); // default 5060
        params.put("userName", "vitto");
        params.put("userPwd", "vitto");
        params.put("turnServerIp", serverIp);
        params.put("turnServerUser", "mauro");
        params.put("turnServerPwd", "mauro");
        params.put("sipServerTransport", "udp");

        String onHoldSoundPath = Utils.getResourcePathByAssetCopy(this.getApplicationContext(), "", "test_hold.wav");
        String onIncomingCallRingTonePath = Utils.getResourcePathByAssetCopy(this.getApplicationContext(), "", "ring_in_call.wav");
        String onOutcomingCallRingTonePath = Utils.getResourcePathByAssetCopy(this.getApplicationContext(), "", "ring_out_call.wav");
        params.put("onHoldSound", onHoldSoundPath);
        params.put("onIncomingCallSound", onIncomingCallRingTonePath); // onIncomingCallRingTonePath
        params.put("onOutcomingCallSound", onOutcomingCallRingTonePath); // onOutcomingCallRingTonePath

        Log.d(TAG, "OnHoldSoundPath:" + onHoldSoundPath);

        return params;

    }

    private void runExample() {
        this.clearInfoLines();
        this.addInfoLine("Local IP Address: " + Utils.getIPAddress(true));

        // Voip Lib Initialization Params
        HashMap<String, String> params = buildParams();

        Log.d(TAG, "Initializing the lib...");
        if (myVoip == null) {
            Log.d(TAG, "Voip null... Initialization.....");
            myVoip = new VoipLibBackend();
            this.voipHandler = new CallHandler(this, myVoip);

            // Initialize the library providing custom initialization params and an handler where
            // to receive event notifications. Following Voip methods are called from the handleMassage() callback method
            //boolean result = myVoip.initLib(params, new RegistrationHandler(this, myVoip));
            myVoip.initLib(this.getApplicationContext(), params, this.voipHandler);
        } else {
            Log.d(TAG, "Voip is not null... Destroying the lib before reinitializing.....");
            // Reinitialization will be done after deinitialization event callback
            this.voipHandler.reinitRequest = true;
            myVoip.destroyLib();
        }
    }

    public void waitForSeconds(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void clearInfoLines() {
        this.infoArray.clear();
        this.buddiesArray.clear();
        if (this.buddyArrayAdapter != null)
            this.buddyArrayAdapter.notifyDataSetChanged();
        if (arrayAdapter != null)
            arrayAdapter.notifyDataSetChanged();
    }

    private void updateCallStateInfo() {
        String callState = "Not available";

        if (this.myVoip != null) {
            callState = this.myVoip.getCall().getState().name();
        }

        TextView labState = (TextView) findViewById(R.id.labCallState);
        labState.setText(callState);
    }

    private void updateServerStateInfo() {
        TextView labServerState = (TextView) findViewById(R.id.labServerState);
        labServerState.setText(myVoip.getServer().getState().toString());
    }

    private void updateAccountStateInfo() {
        TextView labAccountState = (TextView) findViewById(R.id.labAccountState);
        IAccount myAccount = myVoip.getAccount();
        if (myAccount == null)
            labAccountState.setText("N.A");
        else
            labAccountState.setText(myAccount.getState().toString());

    }


    private void updateBuddyStateInfo(IBuddy buddy) {
        Log.d(TAG, "Called updateBuddyStateInfo on buddy");
        if (buddy == null) {
            Log.e(TAG, "Called updateBuddyStateInfo on NULL buddy");
            return;
        }

        Log.d(TAG, "Called updateBuddyStateInfo on buddy:" + buddy.getUri());

        int buddyPosition = this.buddyArrayAdapter.getPosition(buddy);
        if (buddyPosition < 0) {
            Log.d(TAG, "Adding buddy to listView!");
            this.buddiesArray.add(buddy);

        } else {
            Log.d(TAG, "Replacing buddy into the listView!");
            this.buddiesArray.set(buddyPosition, buddy);
        }
        this.buddyArrayAdapter.notifyDataSetChanged();
    }

    public void addInfoLine(String info) {
        String callStatus = "N.A";
        if (this.myVoip != null) {
            Log.d(TAG, "Voip Lib is not NULL: Test with multiple calls!");
            callStatus = myVoip.getCall().getState().name();
        }

        String msg = "CallState:(" + callStatus + "):" + info;
        this.infoArray.add(msg);
        if (arrayAdapter != null)
            arrayAdapter.notifyDataSetChanged();
    }
}
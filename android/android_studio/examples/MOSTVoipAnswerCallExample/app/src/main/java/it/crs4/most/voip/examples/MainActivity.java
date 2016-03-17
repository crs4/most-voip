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
import it.crs4.most.voip.enums.CallState;
import it.crs4.most.voip.enums.VoipEvent;

import android.app.Activity;
import android.app.Service;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This example application shows how to:
 * <ul>
 * <li>initialize the Voip Lib </li>
 * <li>register an account to a remote Sip Server (by specifying its IP address) </li>
 * <li>answer a call incoming from a remote user </li>
 * <li>unregister the previously registered account from the Sip Server </li>
 * <li>deinitialize the Voip Lib </li>
 * </ul>
 *
 * @author crs4
 */
public class MainActivity extends Activity {

    private static final String TAG = "VoipTestActivity";

    private String sipServerPort = "5060";
    private String sipServerTransport = "udp";
    private String sipServerUser = "mauro";
    private String sipServerPwd = "mauro";

    private ArrayList<String> infoArray = null;
    private VoipLib myVoip = null;
    private ArrayAdapter<String> arrayAdapter = null;

    private static class AbstractAppHandler extends Handler {
        protected MainActivity app = null;
        protected VoipLib myVoip = null;
        protected int curStateIndex = 0;

        public AbstractAppHandler(MainActivity app, VoipLib myVoip) {
            super();
            this.app = app;
            this.myVoip = myVoip;
        }

        protected VoipEventBundle getStateBundle(Message voipMessage) {
            //int msg_type = voipMessage.what;
            VoipEventBundle myState = (VoipEventBundle) voipMessage.obj;
            String infoMsg = "State:" + myState.getEvent() + ":" + myState.getInfo();
            Log.d(TAG, "Called handleMessage with state info:" + infoMsg);
            this.app.addInfoLine(infoMsg);
            return myState;
        }
    }

    private class AnswerCallHandler extends AbstractAppHandler {

        private VoipEvent[] expectedStates = {VoipEvent.LIB_INITIALIZED,
                VoipEvent.ACCOUNT_REGISTERING,
                VoipEvent.ACCOUNT_REGISTERED,
                VoipEvent.CALL_INCOMING,
                VoipEvent.CALL_ACTIVE,
                VoipEvent.CALL_HANGUP,
                VoipEvent.ACCOUNT_UNREGISTERING,
                VoipEvent.ACCOUNT_UNREGISTERED,
                VoipEvent.LIB_DEINITIALIZING,
                VoipEvent.LIB_DEINITIALIZED};

        public AnswerCallHandler(MainActivity app, VoipLib myVoip) {
            super(app, myVoip);
        }

        @Override
        public void handleMessage(Message voipMessage) {
            VoipEventBundle myState = getStateBundle(voipMessage);

            assert (myState.getEvent() == expectedStates[curStateIndex]);
            curStateIndex++;
            // Register the account after the Lib Initialization
            if (myState.getEvent() == VoipEvent.LIB_INITIALIZED) {
                myVoip.registerAccount();
            }
            else if (myState.getEvent() == VoipEvent.ACCOUNT_REGISTERED) {
                this.app.addInfoLine("Ready to accept calls");
            }
            else if (myState.getEvent() == VoipEvent.CALL_INCOMING) {
                handleIncomingCall();
            }
            else if (myState.getEvent() == VoipEvent.CALL_ACTIVE) {
                //this.app.waitForSeconds(20);
                //myVoip.hangupCall();
            }
            // Unregister the account
            else if (myState.getEvent() == VoipEvent.CALL_HANGUP) {
                setupButtons(false);
                myVoip.unregisterAccount();
            }
            // Deinitialize the Voip Lib and release all allocated resources
            else if (myState.getEvent() == VoipEvent.ACCOUNT_UNREGISTERED) {
                myVoip.destroyLib();
            }
        }
    }

    private void handleIncomingCall() {
        setupButtons(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.waitForInitialization();
        this.initializeGUI();
    }

    /**
     * Invoked when the 'Go' button is clicked
     */
    public void doVoipTest(View view) {
        EditText txtView = (EditText) this.findViewById(R.id.txtServerIp);
        String serverIp = txtView.getText().toString();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtView.getWindowToken(), 0);
        this.runExample(serverIp);
    }

    public void answerCall(View view) {
        this.myVoip.answerCall();
    }

    public void hangupCall(View view) {
        this.myVoip.hangupCall();
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
        this.infoArray = new ArrayList<String>();
        arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, this.infoArray);
        listView.setAdapter(arrayAdapter);
        this.setupButtons(false);
        //this.addInfoLine("Most Voip Lib Test Example 1");
    }

    private void waitForInitialization() {
        /* Wait for GDB to init */
        Log.d(TAG, "Waiting some second before initializing the lib...");
        if ((this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            this.waitForSeconds(3);
        }
    }

    void setupButtons(boolean active) {
        Button butAccept = (Button) findViewById(R.id.butAccept);
        butAccept.setEnabled(active);

        Button butToogleHold = (Button) findViewById(R.id.butToggleHold);
        butToogleHold.setEnabled(active);

        Button butHangup = (Button) findViewById(R.id.butHangup);
        butHangup.setEnabled(active);
    }

    public void runExample(String serverIp) {
        this.clearInfoLines();
        this.addInfoLine("Local IP Address:" + Utils.getIPAddress(true));

        // Voip Lib Initialization Params

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sipServerIp", serverIp);
        params.put("sipServerPort", sipServerPort); // optional: default 5060
        params.put("sipServerTransport", sipServerTransport); // optional: default 5060
        params.put("userName", sipServerUser);
        params.put("userPwd", sipServerPwd);

        Log.d(TAG, "Initializing the lib...");
        if (myVoip == null) {
            Log.d(TAG, "Voip null... Initialization.....");
            myVoip = new VoipLibBackend();
        }

        // Initialize the library providing custom initialization params and an handler where
        // to receive event notifications. Following Voip methods are called form the handleMassage() callback method
        //boolean result = myVoip.initLib(params, new RegistrationHandler(this, myVoip));
        boolean result = myVoip.initLib(this.getApplicationContext(), params, new AnswerCallHandler(this, myVoip));
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
        if (arrayAdapter != null)
            arrayAdapter.notifyDataSetChanged();
    }

    public void addInfoLine(String info) {
        String callStatus = "N.A";
        if (this.myVoip != null) {
            Log.d(TAG, "Voip Lib is not null");
            callStatus = myVoip.getCall().getState().name();
        }

        String msg = "CallState:(" + callStatus + "):" + info;
        this.infoArray.add(msg);
        if (arrayAdapter != null)
            arrayAdapter.notifyDataSetChanged();
    }
}
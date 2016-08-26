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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

 
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	
	
	private static String TAG = "VoipConfigDemo";
	private ConfigServer configServer = null;
	private JSONArray accounts = null;
	private String accountDetailsData = "";
	private String buddiesDetailsData = "";
	
	private static ArrayList<String> accountsArray = null;
	private static ArrayAdapter<String> accountsArrayAdapter = null;
	private static int selectedAccountIndex = -1;
	private String serverIp = "";
	private int serverPort = 8000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}
	
	 
	public void loadConfig(View view) {
		Log.d(TAG, "Called loadConfig");
		TextView txtAccount = (TextView) findViewById(R.id.txt_webserver_ip);
		serverIp = txtAccount.getText().toString();
		serverPort = 8000;
		
		Intent intent = getIntent();
		String ipAddress = intent.getStringExtra("ip_address");
		Log.d(TAG, "IpAddress: " + ipAddress);
		String accessToken = intent.getStringExtra("access_token");
		
		this.configServer = new ConfigServer(this, serverIp, serverPort, accessToken);
		 
		Listener<JSONObject> listener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				Log.d(TAG, "Query Response::" + response);
		    	try {
					Log.d(TAG, "Accounts" + response.getJSONObject("data").getJSONArray("accounts"));
					accounts =  response.getJSONObject("data").getJSONArray("accounts");
					updateAccountsArray();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "error:" + e);
				}
				
			}
		};
		ErrorListener errorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		this.configServer.getAccounts(listener , errorListener );
	} 
	
	
	public void loadAccount(View view) {
	
		Log.d(TAG, "Called loadAccount");
		if (selectedAccountIndex<0)
		{
			Log.d(TAG, "No selected account to load");
			return;
		}
		
		try {
			JSONObject selAccount = (JSONObject) this.accounts.get(selectedAccountIndex);
			int accountId = selAccount.getInt("uid");
			
			ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					Log.e(TAG,arg0.getMessage());
					
				}
			};
			
			Listener<JSONObject> listener = new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject accountDetails) {
					TextView txtAccount = (TextView) findViewById(R.id.txtAccount);
					try {
						accountDetailsData =accountDetails.getJSONObject("data").getJSONObject("account").toString();
						txtAccount.setText(accountDetailsData);
						loadAccountBuddies();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			this.configServer.getAccount(accountId, listener, errorListener);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,e.getMessage());
		}
	} 
	
	public void loadAccountBuddies() {
		
		Log.d(TAG, "Called loadAccountBuddies");
		if (selectedAccountIndex<0)
		{
			Log.d(TAG, "No selected account to load");
			return;
		}
		
		try {
			JSONObject selAccount = (JSONObject) this.accounts.get(selectedAccountIndex);
			int accountId = selAccount.getInt("uid");
			
			ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					Log.e(TAG,arg0.getMessage());
					
				}
			};
			
			Listener<JSONObject> listener = new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject accountBuddies) {
					TextView txtBuddies= (TextView) findViewById(R.id.txtBuddies);
					try {
						 buddiesDetailsData = accountBuddies.getJSONObject("data").getJSONArray("buddies").toString();
						 txtBuddies.setText(buddiesDetailsData);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			this.configServer.getBuddies(accountId, listener, errorListener);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,e.getMessage());
		}
	} 
	

	public void acceptConfig(View view)
	{
		Intent resultIntent = new Intent();
		Bundle b = new Bundle();
		b.putString("account_data", accountDetailsData);
		b.putString("buddies_data", buddiesDetailsData);
		
		resultIntent.putExtras(b);
		// TODO Add extras or a data URI to this intent as appropriate.
		Log.d(TAG,"Configuration accepted");
		setResult(Activity.RESULT_OK, resultIntent);
		
		finish();
	}
	
	public void ignoreConfig(View view)
	{
		Intent resultIntent = new Intent();
		Log.d(TAG,"Configuration refused");
		setResult(Activity.RESULT_CANCELED, resultIntent);
		finish();
	}
	
	
	private void updateAccountsArray()
	{
		accountsArray.clear();
		for (int i=0; i< this.accounts.length();i++)
		{
			JSONObject account;
			try {
				account = (JSONObject) this.accounts.get(i);
				accountsArray.add(account.getString("name"));
				accountsArrayAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		Activity activity;
		
	    @Override
	    public void onAttach(Activity activity)
	    {
	        super.onAttach(activity);
	        this.activity = activity;
	    }		
		
		public PlaceholderFragment() {
		}
		
		private void initializeGUI(){
			
			Intent intent = this.activity.getIntent();
			String ipAddress = intent.getStringExtra("ip_address");
			Log.d(TAG, "IpAddress: " + ipAddress);
			String accessToken = intent.getStringExtra("access_token");

			TextView txtAccount = (EditText) getActivity().findViewById(R.id.txt_webserver_ip);
			txtAccount.setText(ipAddress);
			
			 accountsArray= new ArrayList<String>();
		     accountsArrayAdapter =
		                new ArrayAdapter<String>(getActivity(), R.layout.accounts_row, R.id.textViewList, accountsArray);
		        
		    ListView listView = (ListView)getActivity().findViewById(R.id.listAccounts);
		    listView.setAdapter(accountsArrayAdapter);
		    listView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView labSelAccount = (TextView) getActivity().findViewById(R.id.labSelectedAccount);
					labSelAccount.setText(accountsArray.get(position));
					selectedAccountIndex = position;
					TextView txtAccount = (TextView) getActivity().findViewById(R.id.txtAccount);
					txtAccount.setText("");
					TextView txtBuddies= (TextView) getActivity().findViewById(R.id.txtBuddies);
					txtBuddies.setText("");
					// load the selected account
				 	 ((MainActivity) getActivity()).loadAccount(null);
				}});
	        
		    Bundle extras = getActivity().getIntent().getExtras();
		    if (extras != null) {
		        String serverIp = extras.getString("serverIp");
		        if (serverIp!=null)
		        {
		        	EditText txtWedServerIp = (EditText) getActivity().findViewById(R.id.txt_webserver_ip);
		        	txtWedServerIp.setText(serverIp);
		        }
		    }
		    
		}
		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			initializeGUI();
		}
	}  

}

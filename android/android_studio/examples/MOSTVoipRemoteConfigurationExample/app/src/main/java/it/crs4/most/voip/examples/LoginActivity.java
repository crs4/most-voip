/*
 * Project MOST - Moving Outcomes to Standard Telemedicine Practice
 * http://most.crs4.it/
 *
 * Copyright 2014, CRS4 srl. (http://www.crs4.it/)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * See license-GPLv2.txt or license-MIT.txt
 */



package it.crs4.most.voip.examples;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {

	private RequestQueue rq = null;
	private static final int REMOTE_ACCOUNT_CONFIG_REQUEST = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		this.rq = Volley.newRequestQueue(this);
		
		Button btnLogin = (Button)findViewById(R.id.buttonLogin);
		btnLogin.setOnClickListener(onClickListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	
	public class ProgressBarAnimation extends Animation{
	    private ProgressBar progressBar;
	    private float from;
	    private float  to;

	    public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
	        super();
	        this.progressBar = progressBar;
	        this.from = from;
	        this.to = to;
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        super.applyTransformation(interpolatedTime, t);
	        float value = from + (to - from) * interpolatedTime;
	        progressBar.setProgress((int) value);
	        Log.d("VoipConfigDemo", "Time: " + interpolatedTime);
	        if(interpolatedTime%this.to==0){
	        	 progressBar.setProgress(0);
	        }
	        
	    }

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REMOTE_ACCOUNT_CONFIG_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	    		Intent resultIntent = new Intent();
	    		Bundle b = new Bundle();
	    		b.putString("account_data", data.getExtras().getString("account_data"));
	    		b.putString("buddies_data", data.getExtras().getString("buddies_data"));
	    		
	    		resultIntent.putExtras(b);
	    		// TODO Add extras or a data URI to this intent as appropriate.
	    		Log.d("VoipConfigDemo","Configuration accepted");
	    		setResult(Activity.RESULT_OK, resultIntent);
	    		
	    		finish();
	        }
	        else{
	        	 Log.d("VoipConfigDemo", "Account data NOT received from the activity");
	        }
	    }
	    else {
	    	Log.d("VoipConfigDemo", "Received unknown requestCode:" + String.valueOf(requestCode));
	    }
	}
	
	private OnClickListener onClickListener = new OnClickListener() {

	     @Override
	     public void onClick(final View v) {
	         switch(v.getId()){
	             case R.id.buttonLogin:
	            	 
	            	 v.setEnabled(false);
	            	 ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar);
	            	 ProgressBarAnimation anim = new ProgressBarAnimation(progress, 0, 1000);	
//	            	 anim.setDuration(1000);
//	            	 anim.set
//	            	 progress.startAnimation(anim);
	            	 progress.setIndeterminate(true);
	                  //DO something
	            	 EditText username   = (EditText)findViewById(R.id.editText1);
	            	 EditText password   = (EditText)findViewById(R.id.editText2);
	            	 final String strUsername = username.getText().toString();
	            	 final String strPassword = password.getText().toString();
	            	 final String ipAddress = ((EditText)findViewById(R.id.editText3)).getText().toString();
	            			 
	            	 Log.d("VoipConfigDemo", "In OnClick with user: " + username.getText() + " - and password: " + password.getText());

	         		String url = "http://" + ipAddress + ":8000/oauth2/access_token/";
	         		StringRequest postRequest = new StringRequest(Request.Method.POST, url, 
	         				new Response.Listener<String>() {
	         			@Override
	                    public void onResponse(String response) {
	         				Log.d("VoipConfigDemo", "Response: " + response);
	         				JSONObject jObject;
	         				String accessToken=null;
	         				try {
								jObject = new JSONObject(response);
								Log.d("VoipConfigDemo", "access_token: " + jObject.get("access_token"));
								accessToken = jObject.getString("access_token");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	         				
	         				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
	         				intent.putExtra("ip_address", ipAddress);
	         				intent.putExtra("access_token", accessToken);
	         				startActivityForResult(intent, REMOTE_ACCOUNT_CONFIG_REQUEST);
	                    }
	                }, new Response.ErrorListener() {



	                        @Override
	                        public void onErrorResponse(VolleyError error) {


	                        }
	                    })



	                {

	         			@Override
	         		    protected Map<String, String> getParams() 
	         		    {  
	         		            Map<String, String>  params = new HashMap<String, String>();  
	        	                params.put("client_id", "065eb628607f75cc4642");
	        	                params.put("client_secret", "c8d3e22618ad29f0ef4ca4aa0fac14d95d491497");
	        	                params.put("grant_type", "password");
	        	                params.put("username", strUsername);
	        	                params.put("password", strPassword);
	         		             
	         		            return params;  
	         		    }



	                };
	         		LoginActivity.this.rq.add(postRequest);	         		

	            	 
	            	 
	             break;
//	             case R.id.button2:
//	                  //DO something
//	             break;
//	             case R.id.button3:
//	                  //DO something
//	             break;
	         }

	   }
	 
	};
}

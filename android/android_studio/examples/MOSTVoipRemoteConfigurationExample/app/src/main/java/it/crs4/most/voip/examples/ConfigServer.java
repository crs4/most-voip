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



import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ConfigServer {
	private RequestQueue rq = null;
	
	String serverIp = null;
	int serverPort = -1;
	Context ctx = null;
	String accessToken = null;
	private String urlPrefix = "";
	
	public ConfigServer(Context ctx, String serverIp, int serverPort, String accessToken)
	{
		this.ctx = ctx;
		this.accessToken = accessToken;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.urlPrefix = "http://" + this.serverIp + ":" + String.valueOf(this.serverPort) + "/voip/";
		this.rq = Volley.newRequestQueue(this.ctx);
	}
	
	
	public void doTestRequest() 
	{
		
		StringRequest postReq = new StringRequest(Request.Method.GET, this.urlPrefix + "accounts/", new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		    	Log.d("most_example", "Query Response:" + response);
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		    	Log.d("most_example", "Error ["+error+"]");
	
		    }
		});

	this.rq.add(postReq);
	
	Log.d("most_example", "Click button 2");
	}
	
	public void doTestJsonRequest() 
	{
		//RequestQueue rq = Volley.newRequestQueue(this.ctx);
		
		JsonObjectRequest postReq = new JsonObjectRequest( this.urlPrefix + "accounts/", null, new Response.Listener<JSONObject>(){
		    @Override
		    public void onResponse(JSONObject response) {
		    	Log.d("most_example", "Query Response::" + response);
		    	try {
					Log.d("most_example", "First Account data:" + response.getJSONObject("data").getJSONArray("accounts").get(0));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("most_example", "error:" + e);
				}
		    	
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		    	Log.d("most_example", "Error ["+error+"]");
	
		    }
		});

	this.rq.add(postReq);
	
	Log.d("most_example", "Click button 2");
	}
	

	public void  getAccounts(Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		JsonObjectRequest postReq = new JsonObjectRequest( this.urlPrefix + "accounts/?access_token=" + this.accessToken, null, listener, errorListener);
		this.rq.add(postReq);
		Log.d("most_example", "getAccountsRequest Sent");
	}


	public void getAccount(int accountId , Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		JsonObjectRequest postReq = new JsonObjectRequest( this.urlPrefix + "accounts/" + String.valueOf(accountId)+"/?access_token=" + this.accessToken, null, listener, errorListener);
		this.rq.add(postReq);
	}

	
	public void  getBuddies(int accountId , Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
		JsonObjectRequest postReq = new JsonObjectRequest( this.urlPrefix + "buddies/" + String.valueOf(accountId)+"/?access_token=" + this.accessToken, null, listener, errorListener);
		this.rq.add(postReq);
	}

}

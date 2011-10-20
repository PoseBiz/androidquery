package com.androidquery.auth;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.WebDialog;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.facebook.android.FbDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class FacebookHandle extends AccountHandle{

	private String appId;
	private Activity act;
	private WebDialog dialog;

	private static final String OAUTH_ENDPOINT = "https://graph.facebook.com/oauth/authorize";
	
	//private static final String REDIRECT_URI = "fbconnect://success";
	private static final String REDIRECT_URI = "https://www.facebook.com/connect/login_success.html";
	
	private static final String CANCEL_URI = "fbconnect:cancel";
	//private static final String TOKEN = "access_token";
	//private static final String EXPIRES = "expires_in";
	
	private static final String DISPLAY_STRING = "touch";

	public FacebookHandle(String appId, Activity act) {
		this.appId = appId;
		this.act = act;
		setToken(fetchToken());
	}


	private void dismiss(){
		if(dialog != null){
			try{
				dialog.dismiss();
			}catch(Exception e){				
			}
			dialog = null;
		}
	}
	
	private void show(){
		if(dialog != null){
			try{
				//dialog.show();
				new AQuery(act).show(dialog);
			}catch(Exception e){				
			}
		}
	}
	
	
	/*
	private void error(int code, String error){
		
		dismiss();
		
		AjaxStatus status = new AjaxStatus(code, error);		
		cb.callback(cb.getUrl(), null, status);
		
	}
	*/
	
	protected void auth() {

		Bundle parameters = new Bundle();
		parameters.putString("client_id", appId);
		parameters.putString("type", "user_agent");
		parameters.putString("redirect_uri", REDIRECT_URI);
		String url = OAUTH_ENDPOINT + "?" + encodeUrl(parameters);
		AQUtility.debug("url: " + url);

		dialog = new WebDialog(act, url, new FbWebViewClient());		

		show();
		dialog.hide();
		
	}
	
	private static final String FB_TOKEN = "aq.fb.token";
	
	private String fetchToken(){
		return PreferenceManager.getDefaultSharedPreferences(act).getString(FB_TOKEN, null);	
	}
	
	private void storeToken(String token){
		PreferenceManager.getDefaultSharedPreferences(act).edit().putString(FB_TOKEN, token).commit();	
	}
	

	private class FbWebViewClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			AQUtility.debug("return url: " + url);
			
			if(url.startsWith(REDIRECT_URI)) {
				
				Bundle values = parseUrl(url);
				
				String error = values.getString("error_reason");
				
				AQUtility.debug("error", error);
				
				String token = null;
				
				if(error == null) {
					token = extractToken(url);					
				}
				
				setToken(token);
				
				if(token != null){
					dismiss();
					storeToken(token);
					
					AQUtility.debug("success!");
					
					success(act);
				}else{
					dismiss();
					failure(act);
					//error(401, error);
				}
				
				return true;
			} else if (url.startsWith(CANCEL_URI)) {
				AQUtility.debug("cancelled");
				//error(401, "cancelled");
				dismiss();
				failure(act);
				return true;
			} else if (url.contains(DISPLAY_STRING)) {
				AQUtility.debug("display string");
				return false;
			}
			AQUtility.debug("nothing");
			return false;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			

			AQUtility.debug("started", url);
			
			super.onPageStarted(view, url, favicon);
			
			
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			AQUtility.debug("finished", url);
			super.onPageFinished(view, url);
			
			show();
		}
		
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			dismiss();
			failure(act);
		}
		
		@Override
		public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
			dismiss();
			failure(act);
		}
		
	}

	
	private String extractToken(String url) {

		Uri uri = Uri.parse(url.replace('#', '?'));

		String token = uri.getQueryParameter("access_token");

		AQUtility.debug("token", token);

		return token;
		
		
	}

	private static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(key + "=" + parameters.getString(key));
		}
		return sb.toString();
	}

	private static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(v[0], v[1]);
			}
		}
		return params;
	}

	
	private static Bundle parseUrl(String url) {
		
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	@Override
	public boolean expired(int code) {
		return code == 400 || code == 401;
	}

	@Override
	public boolean reauth(final AbstractAjaxCallback<?, ?> cb) {
		
		setToken(null);
		storeToken(null);
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				auth(cb);
			}
		});
		
		return false;
	}

	@Override
	public void applyToken(AbstractAjaxCallback<?, ?> cb) {
		
	}

	@Override
	public String applyToken(String url){

		if(url.indexOf('?') == -1){
			url = url + "?";
		}else{
			url = url + "&";
		}
		
		url = url + "access_token=" + getToken();
		return url;
	}
}

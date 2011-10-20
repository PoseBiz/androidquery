package com.androidquery.auth;

import java.util.LinkedHashSet;

import android.content.Context;
import android.preference.PreferenceManager;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;

public abstract class AccountHandle {

	
	private LinkedHashSet<AbstractAjaxCallback<?, ?>> callbacks;
	
	private String token;
	
	public synchronized void auth(AbstractAjaxCallback<?, ?> cb){		
		
		if(callbacks == null){
			callbacks = new LinkedHashSet<AbstractAjaxCallback<?,?>>();
			callbacks.add(cb);
			auth();
		}else{
			callbacks.add(cb);
		}
				
	}
	
	
	protected void setToken(String token) {
		this.token = token;
	}


	protected String getToken(){
		return token;
	}
	
	public boolean authenticated(){
		return token != null;
	}
	
	protected void success(Context context){
		failed = false;
		callback(context);
	}
	
	private synchronized void callback(Context context){
		
		if(callbacks != null){
			
			for(AbstractAjaxCallback<?, ?> cb: callbacks){
				cb.async(context);
			}
			
			callbacks = null;
		}
		
	}
	
	private boolean failed;
	protected void failure(Context context){		
		failed = true;
		callback(context);
	}
	
	public boolean failed(){
		return failed;
	}
	
	
	protected abstract void auth();
	
	public abstract boolean expired(int code);
	
	public abstract boolean reauth(AbstractAjaxCallback<?, ?> cb);
	
	public abstract void applyToken(AbstractAjaxCallback<?, ?> cb);
	
	public String applyToken(String url){
		return url;
	}
	
	
}

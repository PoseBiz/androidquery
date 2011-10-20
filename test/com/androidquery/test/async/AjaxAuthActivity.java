package com.androidquery.test.async;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.preference.PreferenceManager;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.GoogleHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class AjaxAuthActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	private static final String FB_TOKEN = "aq.fb.token";
	
	private String getToken(String key){
		return PreferenceManager.getDefaultSharedPreferences(this).getString(key, null);	
	}
	
	private void storeToken(String key, String token){
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, token).commit();	
	}
	
	public void auth_facebook(){
		
		storeToken(FB_TOKEN, null);
		
		String appId = "251003261612555";
		FacebookHandle fbh = new FacebookHandle(appId, this);
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url("https://graph.facebook.com/me/feed").type(JSONObject.class).weakHandler(this, "fbcb");
		
		//fbh.async(cb);
		
		cb.auth(fbh);
		
		aq.ajax(cb);
		
	}
	
	public void fbcb(String url, JSONObject jo, AjaxStatus status){
		
		showResult(jo, status);
		
	}
	
	public void auth_pick_account(){
	    
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, null);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void auth_last_account(){
		
		
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void auth_specific_account(){
		
		
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void readerCb(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml != null){
		
			List<XmlDom> entries = xml.tags("entry");			
			List<String> titles = new ArrayList<String>();
			
			for(XmlDom entry: entries){
				titles.add(entry.text("title"));
			}
			
			showTextResult(titles);			
		}
		
		AQUtility.debug("status:" + status);
		
		showResult(xml, status);
	}
	
	
	public void auth_picasa(){
		
		String url = "https://picasaweb.google.com/data/feed/api/user/default?alt=json";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
  
		cb.url(url).type(JSONObject.class).weakHandler(this, "picasaCb");  
		cb.auth(this, AQuery.AUTH_PICASA, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void picasaCb(String url, JSONObject jo, AjaxStatus status) {
	
		showResult(jo);
		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			AQUtility.debug(entries.toString());
						
			for(int i = 0; i < entries.length(); i++){
				JSONObject entry = entries.optJSONObject(i);
				JSONObject co = entry.optJSONObject("gphoto$numphotos");
				int count = co.optInt("$t", 0);
				if(count > 0){
					String tb = entry.optJSONObject("media$group").optJSONArray("media$content").optJSONObject(0).optString("url");
					AQUtility.debug("tb", tb);
					
					aq.id(R.id.image).image(tb);
					break;
				}
			}
			
			
		}else{
			showError(status);
		}
	
	}
	
	public void auth_youtube(){
		
		String url = "https://gdata.youtube.com/feeds/api/users/default/subscriptions?v=2&alt=json";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(); 
		cb.url(url).type(JSONObject.class).weakHandler(this, "youtubeCb").fileCache(true).expire(15 * 60 * 1000);  
		
		cb.auth(this, AQuery.AUTH_YOUTUBE, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void youtubeCb(String url, JSONObject jo, AjaxStatus status) {
		
		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			if(entries.length() > 0){	
				
				String src = entries.optJSONObject(0).optJSONObject("content").optString("src");			
				auth_youtube2(src + "&alt=json");
			}else{
				showResult(jo);				
			}
			
			
		}else{
			showError(status);
		}
	
	}
	
	private void auth_youtube2(String src){
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(); 
		cb.url(src).type(JSONObject.class).weakHandler(this, "youtubeCb2");  
		
		cb.auth(this, AQuery.AUTH_YOUTUBE, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void youtubeCb2(String url, JSONObject jo, AjaxStatus status) {
		
		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			if(entries.length() > 0){	
				showResult(entries, status);
				JSONArray tbs = entries.optJSONObject(0).optJSONObject("media$group").optJSONArray("media$thumbnail");
				
				for(int i = 0; i < tbs.length(); i++){
					JSONObject tbo = tbs.optJSONObject(i);
					if("hqdefault".equals(tbo.optString("yt$name"))){
						String tb = tbo.optString("url");							
						aq.id(R.id.image).image(tb);
						break;
					}
				}
				
				
				
			}else{			
				showResult(jo);				
			}
		}else{
			showError(status);
		}
	
	}
	
	public void auth_contacts(){
		
		
		String url = "https://www.google.com/m8/feeds/contacts/default/full";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();  
		cb.url(url).type(XmlDom.class).weakHandler(this, "contactsCb");  
		cb.auth(this, AQuery.AUTH_CONTACTS, AQuery.ACTIVE_ACCOUNT);
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void auth_parallel(){
		
		String url1 = "https://picasaweb.google.com/data/feed/api/user/default";
		String url2 = "https://picasaweb.google.com/data/feed/api/user/default?alt=json";
		
		GoogleHandle gh = new GoogleHandle(this, AQuery.AUTH_PICASA, null);
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url1).type(XmlDom.class).weakHandler(this, "pcb1");  
		cb.auth(gh);
		
		AjaxCallback<JSONObject> cb2 = new AjaxCallback<JSONObject>();
		  
		cb2.url(url2).type(JSONObject.class).weakHandler(this, "pcb2");  
		cb2.auth(gh);
		
		cb.async(this);
		cb2.async(this);
		
		//aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void pcb1(String url, XmlDom xml, AjaxStatus status){
		
		String result = "Result 1:\n";
		
		if(xml != null){			
			result += xml.toString();
			result = result.substring(0, Math.min(100, result.length())) + " ... ";
		}
		
		this.showTextResult(result);
		
	}
	
	public void pcb2(String url, JSONObject jo, AjaxStatus status){
		
		this.showResult("Result 2:\n" + jo, null);
		
	}
	
	public void contactsCb(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml != null){
		
			List<XmlDom> entries = xml.tags("entry");
			
			List<String> friends = new ArrayList<String>();
			
			for(XmlDom entry: entries){
				friends.add(entry.text("title"));
			}
			
			showTextResult(friends);
			
		}
		
		showResult(xml, status);
	}
	
	
	private void showError(AjaxStatus status){
		showResult(status.getCode(), "This account might not exist for this service.");
	}
	
	public void stringCb(String url, String str, AjaxStatus status) {
		
		if(status.getCode() == 401){
			showResult("Authenticate Error with Http Response 401", status);
		}else{		
			showResult(str, status);
		}
		
	}

	@Override
	public void onDestroy(){
		
		aq.dismiss();
		
		super.onDestroy();
	}
}

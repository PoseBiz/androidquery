package com.androidquery.test;

import java.util.Locale;

import javax.net.ssl.HandshakeCompletedListener;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Constants;
import com.androidquery.util.RatioDrawable;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.sugree.twitter.*;
import com.sugree.twitter.Twitter.DialogListener;

public class AdhocActivity2 extends RunSourceActivity implements DialogListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();
	}
	
	
	private void work(){
		
		//runSource();
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity2;
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("ad hoc2");
		
		facebook();
		//twitter();
	}
	
	private void facebook(){
		String appId = "251003261612555";
		FacebookHandle fbh = new FacebookHandle(appId, this);
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url("https://graph.facebook.com/me/feed").type(JSONObject.class).weakHandler(this, "fbcb");
		
		//fbh.async(cb);
		
		//fbh.auth();
	}
	
	
	public void fbcb(String url, JSONObject jo, AjaxStatus status){
		
		AQUtility.debug("fb status", status.getCode());
		
		AQUtility.debug("fbcb", jo);
		
	}
	
	private void twitter(){
		String key = "7yuczMXlS6dEpWZlJiBA";
		String secret = "eexhGXc4XCnJVqHy99160wWwzKSZXEtp9jC6FRgQc";
		
		Twitter twitter = new Twitter(R.drawable.icon);
		
		twitter.authorize(this, new Handler(), key, secret, this);
		
	}


	@Override
	public void onComplete(Bundle values) {
		// TODO Auto-generated method stub
		AQUtility.debug("onComplete");
	}


	@Override
	public void onTwitterError(TwitterError e) {
		// TODO Auto-generated method stub
		AQUtility.debug("onTwitterError");
	}


	@Override
	public void onError(com.sugree.twitter.DialogError e) {
		// TODO Auto-generated method stub
		AQUtility.debug("onError");
	}


	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		AQUtility.debug("onCancel");
	}
}

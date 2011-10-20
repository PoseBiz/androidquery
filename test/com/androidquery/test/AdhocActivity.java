package com.androidquery.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import android.app.Activity;  
 import android.graphics.drawable.Drawable;  
import android.net.Uri;
 import android.os.Bundle;  
 import android.util.Log;  
 import android.view.ViewGroup;  
 import android.webkit.WebView;  
 import android.webkit.WebViewClient;  
 import android.widget.FrameLayout;  
import android.widget.TextView;  
 
 public class AdhocActivity extends Activity {  
      private WebView webView;  
      private TextView title;  
      protected static String OAUTH_ENDPOINT = "https://graph.facebook.com/oauth/authorize";  
      public static final String REDIRECT_URI = "fbconnect://success";  
   public static final String CANCEL_URI = "fbconnect:cancel";  
   public static final String TOKEN = "access_token";  
   public static final String EXPIRES = "expires_in";  
      public static final String APP_ID = "146108052096538";  
     // static final int FB_BLUE = 0xFF6D84B4;  
   //static final float[] DIMENSIONS_LANDSCAPE = {460, 260};  
   //static final float[] DIMENSIONS_PORTRAIT = {280, 420};  
   //static final FrameLayout.LayoutParams FILL =   
  //   new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,   
   //           ViewGroup.LayoutParams.FILL_PARENT);  
   //static final int MARGIN = 4;  
   //static final int PADDING = 2;  
   static final String DISPLAY_STRING = "touch";  
   //static final String FB_ICON = "icon.png";  
   
   private AQuery aq;
   
   /** Called when the activity is first created. */  
   @Override  
   public void onCreate(Bundle savedInstanceState) {  
     super.onCreate(savedInstanceState);  
     //setContentView(R.layout.facebook); 
     
     aq = new AQuery(this);
    
     setContentView(R.layout.adhoc_activity); 
     
     Bundle parameters = new Bundle();  
     parameters.putString("client_id", APP_ID);        
     parameters.putString("type", "user_agent");  
     parameters.putString("redirect_uri", REDIRECT_URI);  
     String url = OAUTH_ENDPOINT + "?" + encodeUrl(parameters);  
     AQUtility.debug(this.getClass().getName(), "url: " + url);  
    
     webView = (WebView) findViewById(R.id.web);  
     webView.getSettings().setJavaScriptEnabled(true);  
     webView.setVerticalScrollBarEnabled(false);  
     webView.setHorizontalScrollBarEnabled(false);  
     webView.setWebViewClient(new FbWebViewClient());  
     webView.loadUrl(url);  
   }  
   private class FbWebViewClient extends WebViewClient {  
         @Override  
      public boolean shouldOverrideUrlLoading(WebView view, String url) {  
              AQUtility.debug(this.getClass().getName(), "return url: " + url);  
              if (url.startsWith(REDIRECT_URI)) {  
          Bundle values = parseUrl(url);  
          //AQUtility.debug(this.getClass().getName(), "values: " + Util.printBundle(values));  
          String error = values.getString("error_reason");  
          if (error == null) {  
               AQUtility.debug(this.getClass().getName(), "error was null");   
               extractToken(url);
          } else {  
               AQUtility.debug(this.getClass().getName(), "error was not null");  
          }  
          return true;  
        } else if (url.startsWith(CANCEL_URI)) {  
             AQUtility.debug(this.getClass().getName(), "cancelled");  
          return true;  
        } else if (url.contains(DISPLAY_STRING)) {  
             AQUtility.debug(this.getClass().getName(), "display string");  
          return false;  
        }   
              AQUtility.debug(this.getClass().getName(), "nothing");  
              return false;  
         }  
   }  
   
   private void extractToken(String url){
	   
	   Uri uri = Uri.parse(url.replace('#', '?'));
	   
	   String token = uri.getQueryParameter("access_token");
	   
	   AQUtility.debug("token", token);
	   
	   String u = "https://graph.facebook.com/me/feed?access_token=" + token;
	   
	   aq.ajax(u, JSONObject.class, new AjaxCallback<JSONObject>(){
		   
		   @Override
			public void callback(String url, JSONObject object, AjaxStatus status) {
				
			   AQUtility.debug("fbcb", object);
			   
			}
		   
	   });
	   
   }
   
   
   public static String encodeUrl(Bundle parameters) {  
	     if (parameters == null) {  
	          return "";  
	     }  
	     StringBuilder sb = new StringBuilder();  
	     boolean first = true;  
	     for (String key : parameters.keySet()) {  
	       if (first) first = false; else sb.append("&");  
	       sb.append(key + "=" + parameters.getString(key));  
	     }  
	     return sb.toString();  
	   }  
	   public static Bundle decodeUrl(String s) {  
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
   public static Bundle parseUrl(String url) {  
	     // hack to prevent MalformedURLException  
	     url = url.replace("fbconnect", "http");   
	     try {  
	       URL u = new URL(url);  
	       Bundle b = decodeUrl(u.getQuery());  
	       b.putAll(decodeUrl(u.getRef()));  
	       return b;  
	     } catch (MalformedURLException e) {  
	       return new Bundle();  
	     }  
	   }  
   
 }  

package com.androidquery;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sugree.twitter.Twitter.DialogListener;

public class WebDialog extends Dialog {

	private String url;
	private WebViewClient client;
    
    public WebDialog(Context context, String url, WebViewClient client) {
        super(context, R.style.Theme);
        this.url = url;
        this.client = client;
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
   
        WebView wv = setUpWebView();
        
        FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addContentView(wv, FILL);

    }

    
    private WebView setUpWebView() {
    	
    	WebView mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        
        WebViewClient wvc = client;
        if(wvc == null) wvc = new WebViewClient();
        
        mWebView.setWebViewClient(wvc);
        
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        
        return mWebView;
    }

}

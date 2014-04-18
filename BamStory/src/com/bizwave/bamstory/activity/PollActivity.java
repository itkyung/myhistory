package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.util.MyProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PollActivity extends Activity {
	private WebView wv;
	private MyProgressDialog progressDialog;
	private Context mContext;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pollview);
		
	
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		mContext = this;
		wv = (WebView) findViewById(R.id.pollWebView);
		wv.setVerticalScrollBarEnabled(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				if(progressDialog != null)
					progressDialog.dismiss();	
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				progressDialog = MyProgressDialog.show(mContext,"","",true,true,null);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				if(progressDialog != null)
					progressDialog.dismiss();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
				
			}
			
		});
		
		wv.loadUrl(Constants.HOST_NAME + "/bamStory/mobile/bam/poll/viewCurrentPollBySession.do?sessionKey=" + settings.getString(Constants.SESSION_KEY, null));		
	}
	
	
}

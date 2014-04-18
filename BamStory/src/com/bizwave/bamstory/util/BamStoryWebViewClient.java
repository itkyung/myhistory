package com.bizwave.bamstory.util;

import java.net.URLDecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bizwave.bamstory.activity.BasicActivity;

public class BamStoryWebViewClient extends WebViewClient {
	private static final String PhoneURL = "phone:";
	private static final String MapURL = "map:";
	private Context mContext;

	
	public BamStoryWebViewClient(){
		super();
	}
	
	public BamStoryWebViewClient(Context context){
		super();
		mContext = context;
	}
	
	
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		((BasicActivity)mContext).showProgress();
		super.onPageStarted(view, url, favicon);

	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		((BasicActivity)mContext).stopProgress();
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		
		super.onReceivedError(view, errorCode, description, failingUrl);
		((BasicActivity)mContext).stopProgress();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(url.startsWith(PhoneURL)){
			callPhone(url.substring(PhoneURL.length()));
			return true;
		}else if(url.startsWith(MapURL)){
			viewMap(url.substring(MapURL.length()));
			return true;
		}else{
			
			view.loadUrl(url);
			return true;
		}
	}

	private void callPhone(String param){
		String[] params = param.split(",");
		String phone = params[0];
		String id = params[1];
		
		String name = URLDecoder.decode(params[2]);
		String partner = URLDecoder.decode(params[3]);
		
		if(mContext instanceof BasicActivity){
			((BasicActivity)mContext).callPhone(phone, id, "[" + partner + "]" + name);
		}		
		
	}
	
	private void viewMap(String param){
		String[] params = param.split(",");
		String name = null;
		try{
			name = URLDecoder.decode(params[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(mContext instanceof BasicActivity){
			((BasicActivity)mContext).viewMap(name,params[1],params[2],params[3]);
		}		
	}
	
}

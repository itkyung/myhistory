package com.bizwave.bamstory;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bizwave.bamstory.activity.BamStoryTabActivity;
import com.bizwave.bamstory.activity.LoginActivity;
import com.bizwave.bamstory.db.Category;
import com.bizwave.bamstory.db.CategoryContactMapping;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.db.Event;
import com.bizwave.bamstory.db.EventContactMapping;
import com.bizwave.bamstory.db.Partner;
import com.bizwave.bamstory.db.RevisionInfo;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.NetworkCheckUtil;
import com.bizwave.bamstory.util.SHA1;

public class IntroActivity extends Activity {
	//private AlertDialog ad;
	private Context context;
	private MyProgressDialog progressDialog;
	private final String version="2.6";
	private String deviceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		
		context = this.getApplicationContext();
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		this.deviceId = manager.getDeviceId();		
		
		final ImageView image = (ImageView)findViewById(R.id.intro);
		image.setImageResource(R.drawable.intro);
		
	    if( !NetworkCheckUtil.IsWifiAvailable(context) && !NetworkCheckUtil.Is3GAvailable(context)){
	        buildAlertMessage("3G데이타망과 WIFI망이 둘다 사용불가 상태입니다. 둘중에 한가지를 사용가능하게 하고 다시 실행하세요");
	        return;	    	
	    }
		
		new AutoSwitcher().execute("");
	}

	private void buildAlertMessage(String msg) {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage(msg)
		           .setCancelable(false)
		           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
		               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		                   finish();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
	}
			
	public void goLogin(){
		startActivity(new Intent(this,LoginActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_left, R.anim.hold);
	}
	
	private class AutoSwitcher extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			try{

				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("deviceId", deviceId));
				ps.add(new BasicNameValuePair("phoneType", "ANDROID"));
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/launchApplication.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				httpClient.execute(httpPost, new BasicResponseHandler());
				goLogin();
			}catch(Exception e){
				e.printStackTrace();
				//buildAlertMessage(e.getMessage());
			}
			return null;
		}
	}	
}

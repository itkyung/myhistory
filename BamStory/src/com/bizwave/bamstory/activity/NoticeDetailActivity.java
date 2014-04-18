package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.NoticeInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.util.MyProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class NoticeDetailActivity extends Activity {
	private MyProgressDialog progressDialog;
	
	private String noticeId;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticedetail);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		Bundle param = getIntent().getExtras();
		this.noticeId = param.getString("id");
		
		
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new NoticeDetailDataHandler().execute("");
		
	}
	
	public void setNotice(NoticeInfo info){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		TextView title = (TextView)findViewById(R.id.noticeDetailTitleText);
		title.setText(info.getTitle());
		
		TextView description = (TextView)findViewById(R.id.noticeDetailDescription);
		description.setText(info.getDescription());
		
	}
	
	private class NoticeDetailDataHandler extends AsyncTask<String,String,NoticeInfo>{

		@Override
		protected NoticeInfo doInBackground(String... params) {
			NoticeInfo notice = new NoticeInfo();
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("id",noticeId));
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/viewNotice.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						notice.setId(jsonObject.getString("id"));
						notice.setTitle(jsonObject.getString("title"));
						notice.setCreated(jsonObject.getString("created"));
						notice.setDescription(jsonObject.getString("content"));
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return notice;	

		}

		@Override
		protected void onPostExecute(NoticeInfo result) {
			super.onPostExecute(result);
			setNotice(result);
		}
		
		
		
	}

}

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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ListView;
import android.widget.TextView;

public class NoticeActivity extends ListActivity {
	private MyProgressDialog progressDialog;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		
		ListView list = getListView();
		
		LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.contactlistheader,list,false);
		list.addHeaderView(header);
		TextView title = (TextView)header.findViewById(R.id.titleBar);
		title.setText("밤스 공지사항");
		setListAdapter(new NoticeListAdapter(this));		
		
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new NoticeDataHandler().execute("");		
	}

	public void setNotices(ArrayList<NoticeInfo> results){
		NoticeListAdapter ad = (NoticeListAdapter)this.getListAdapter();
		ad.setNotices(results);
		ad.notifyDataSetChanged();
		this.getListView().forceLayout();		
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		NoticeListAdapter adapter = (NoticeListAdapter)this.getListAdapter();
		ArrayList<NoticeInfo> notices = adapter.getNotices();
		position = position - 1;
		if(notices != null){
			NoticeInfo notice = notices.get(position);
			Bundle param = new Bundle();
			param.putString("id", notice.getId());
			Intent intent = new Intent(this,NoticeDetailActivity.class);
			intent.putExtras(param);
			startActivity(intent);
		}
		
	}
	
	private class NoticeListAdapter extends BaseAdapter{
		private Context mContext;
		ArrayList<NoticeInfo> notices = null;
		
		public NoticeListAdapter(Context c){
			mContext = c;
		}
		
		public void setNotices(ArrayList<NoticeInfo> results){
			this.notices = results;
		}
		
		public ArrayList<NoticeInfo> getNotices(){
			return notices;
		}
		
		@Override
		public int getCount() {
			return notices == null ? 0 : notices.size();
		}

		@Override
		public Object getItem(int position) {
			if(notices == null) return null;
			return notices.get(position);
			
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NoticeInfo notice = notices.get(position);
			View v=null;
			try{
				if(convertView == null){
					v = LayoutInflater.from(mContext).inflate(
							com.bizwave.bamstory.R.layout.noticelistview,parent,false);
				}else{
					v = convertView;
				}
				TextView title = (TextView)v.findViewById(R.id.noticeTitleText);
				title.setText(notice.getTitle());
				
				TextView created = (TextView)v.findViewById(R.id.noticeCreatedText);
				created.setText(notice.getCreated());
				
			}catch(Exception e){
				e.printStackTrace();
			}	
			
			return v;
		}
		
	}
	
	private class NoticeDataHandler extends AsyncTask<String,String,ArrayList<NoticeInfo>>{

		@Override
		protected ArrayList<NoticeInfo> doInBackground(String... params) {
			ArrayList<NoticeInfo> notices = new ArrayList<NoticeInfo>();
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/listNotices.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						JSONArray results = (JSONArray)jsonObject.get("results");
						for(int i=0; i < results.length(); i++){
							JSONObject noticeObj = (JSONObject)results.getJSONObject(i);
							NoticeInfo notice = new NoticeInfo();
							notice.setId(noticeObj.getString("id"));
							notice.setTitle(noticeObj.getString("title"));
							notice.setCreated(noticeObj.getString("created"));
							notices.add(notice);
						}
						
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return notices;	

		}

		@Override
		protected void onPostExecute(ArrayList<NoticeInfo> result) {
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
			super.onPostExecute(result);
			setNotices(result);
			
		}
		
	}
}

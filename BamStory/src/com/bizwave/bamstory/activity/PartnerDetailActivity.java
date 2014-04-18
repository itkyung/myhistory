package com.bizwave.bamstory.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;

import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.SimpleBag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PartnerDetailActivity extends Activity implements OnItemClickListener{
	private String partnerId;

	private Context context;
	private PartnerInfo pInfo;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.partnerdetail);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
			
		
		this.context = this.getApplicationContext();
		
		Bundle param = getIntent().getExtras();
		partnerId = param.getString("partnerId");
		
		ListView contactList = (ListView)findViewById(R.id.pDetailContacts);
		contactList.setAdapter(new ContactAdapter(this));
		contactList.setOnItemClickListener(this);
		
		new PartnerDetailDataHandler().execute("");
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		if(this.pInfo == null) return;
		SimpleBag contact = pInfo.getContacts().get(position);
		
		Bundle param = new Bundle();
		param.putString("contactId", contact.getKey());
		param.putString("contactName", contact.getValue());
		param.putString("partnerName", pInfo.getName());
		Intent intent = new Intent(this,ContactDetailActivity.class);
		intent.putExtras(param);
		startActivity(intent);
		
	}


	public void setPartnerInfo(PartnerInfo info){
		this.pInfo = info;
		TextView pv = (TextView)findViewById(R.id.pDetailPartnerName);
		pv.setText(info.getName());
		
		ImageView partnerImg = (ImageView)findViewById(R.id.pDetailPartnerImg);
		if(info.getImgUrl() == null || "null".equals(info.getImgUrl())){
			partnerImg.setImageResource(R.drawable.defaultpartner);		
		}else{
			setImg(partnerImg,info.getImgUrl());
		}	
		
		AssetManager assetManager = context.getAssets();
		try{
			ImageView typeImg = (ImageView)findViewById(R.id.pDetailPartnerTypeImg);
			AssetInputStream evalBuf = (AssetInputStream)assetManager.open("icon/" + info.getTypeImgName() + ".png");
			Bitmap eBitmap = BitmapFactory.decodeStream(evalBuf);		
			typeImg.setImageBitmap(eBitmap);
			evalBuf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		ListView contactView = (ListView)findViewById(R.id.pDetailContacts);
		ContactAdapter ca = (ContactAdapter)(contactView).getAdapter();
		ca.setContacts(info.getContacts());
		ca.notifyDataSetChanged();
		
	}
	
	private void setImg(ImageView iv,String url){
		try{
			URL imageURL = new URL(Constants.HOST_NAME + url);
			HttpURLConnection conn = (HttpURLConnection)imageURL.openConnection();             
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 10240);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			iv.setImageBitmap(bm);
			bis.close();   
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	

	private class ContactAdapter extends BaseAdapter{
		private Context pContext;
		private ArrayList<SimpleBag> contacts;
		private LayoutInflater mInflater;
		
		public ContactAdapter(Context context){
			this.pContext = context;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setContacts(ArrayList<SimpleBag> contacts){
			this.contacts = contacts;
		}
		
		@Override
		public int getCount() {
			if(this.contacts != null){
				return this.contacts.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if(this.contacts != null){
				return this.contacts.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup parent) {
			TextView tv;
			if(arg1 == null){

				tv = (TextView)mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			}else{
				tv = (TextView)arg1;
			}
			SimpleBag b = this.contacts.get(position);

			tv.setText(b.getValue());
			return tv;
		}
		
	}
	
	private class PartnerDetailDataHandler extends AsyncTask<String,String,PartnerInfo>{

		@Override
		protected PartnerInfo doInBackground(String... params) {
			PartnerInfo info = new PartnerInfo();
			try{
				
				
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
				ps.add(new BasicNameValuePair("partnerId",partnerId));
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/GetPartnerInfo.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);
					if(jsonObject != null) {
						info.setId(jsonObject.getString("id"));
						info.setName(jsonObject.getString("name"));
						info.setTypeImgName(jsonObject.getString("typeName").toLowerCase());
						info.setImgUrl(jsonObject.getString("partnerImgUrl"));
						
						ArrayList<SimpleBag> contacts = new ArrayList<SimpleBag>();
						JSONArray contactsObject = jsonObject.getJSONArray("contacts");
						for(int i = 0; i < contactsObject.length(); i++){
							JSONObject contact = contactsObject.getJSONObject(i);
							SimpleBag b = new SimpleBag();
							b.setKey(contact.getString("id"));
							b.setValue(contact.getString("name"));
							contacts.add(b);
						}
						info.setContacts(contacts);
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				
			}catch(Exception e){
				
			}
			return info;
		}
		@Override
		protected void onPostExecute(PartnerInfo result) {
			super.onPostExecute(result);
			setPartnerInfo(result);
		}
	}
}

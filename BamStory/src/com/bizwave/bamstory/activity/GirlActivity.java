package com.bizwave.bamstory.activity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.GirlInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.StoryInfo;

import com.bizwave.bamstory.db.Partner;
import com.bizwave.bamstory.util.ImageThreadLoader;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.ImageThreadLoader.ImageLoadedListener;
import com.bizwave.bamstory.view.CustomImageButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GirlActivity extends BamStoryBaseListActivity {
	private String contactId;
	private String contactName;
	private Context context;
	private MyProgressDialog progressDialog;
	private static final int maxPageSize = 15;
	private int start = 0;
	
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Intent i = getIntent();
		if(i.hasExtra("contactId")){
			contactId = i.getStringExtra("contactId");
			contactName = i.getStringExtra("contactName");
		}
		context = this;
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		ListView list = getListView();
		if(contactId != null){
			LayoutInflater infalter = getLayoutInflater();
	        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.storylistheader,list,false);
	        final TextView title = (TextView)header.findViewById(R.id.syListTitleBar);
	        title.setText(contactName + "의 언니들");
			list.addHeaderView(header);	
		}
		
		list.setCacheColorHint(getResources().getColor(android.R.color.black));
		
		setListAdapter(new GirlListAdapter(this));
		
		getGirlData();
	}
	
	public void setGirls(ArrayList<GirlInfo> girls){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		GirlListAdapter ad = (GirlListAdapter)this.getListAdapter();
		if(start == 0){
			if(ad.getGirls() != null){
				ad.clearData();
			}
			ad.setGirls(girls);
		}else{
			ad.addGirls(girls);
		}
		
		ad.notifyDataSetChanged();
		this.getListView().forceLayout();
	}
	
	public void getGirlData(){
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new GirlDataHandler().execute(""+start);		
	}
	
	private void buildAlertMessage(String msg,final boolean finish) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(msg)
	           .setCancelable(false)
	           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   if(finish)
	            		   finish();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(contactId != null)
			position = position-1;
		GirlListAdapter adapter = (GirlListAdapter)this.getListAdapter();
		ArrayList<GirlInfo> girls = adapter.getGirls();
		if(girls != null){
			if(girls.size() > position){
				GirlInfo girl = girls.get(position);
				Bundle param = new Bundle();
				param.putSerializable("girlInfo", girl);
				Intent intent = new Intent(this,GirlDetailActivity.class);
				intent.putExtras(param);
				startActivity(intent);
			}else{
				start = start + maxPageSize;
				getGirlData();
			}
		}
	}
	

	private class GirlListAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<GirlInfo> girls = null;
		private HashMap<String,String> girlMap = new HashMap<String,String>();
		private ImageThreadLoader imageLoader = new ImageThreadLoader();
		
		
		public void clearData(){
			if(this.girls != null)
				girls.clear();
			
		}
		public GirlListAdapter(Context context){
			mContext = context;
		}
		
		public void setGirls(ArrayList<GirlInfo> b){
			this.girls = b;
			for(GirlInfo s : b){
				this.girlMap.put(s.getId(), s.getId());
			}
		}
		
		public ArrayList<GirlInfo> getGirls(){
			return girls;
		}
		
		public void addGirls(ArrayList<GirlInfo> newGirls){
			for(GirlInfo s : newGirls){
				if(!this.girlMap.containsKey(s.getId())){
					girls.add(s);
				}
			}
		}
		@Override
		public int getCount() {
			return girls == null ? 0 : girls.size()+1;
		}

		@Override
		public Object getItem(int position) {
			if(girls == null) return null;
			if(girls.size() <= position){
				return null;
			}else{
				return girls.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AssetManager assetManager = mContext.getAssets();
			if(girls.size() <= position){
				View v=null;
				try{
					//if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.girllistview,parent,false);
					//}else{
					//	v = convertView;
					//}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				TextView nickNameText = (TextView)v.findViewById(R.id.girllist_nickname);
				nickNameText.setText("언니정보 더 보기");
				
				return v;
			}else{
				GirlInfo b = girls.get(position);
				View v=null;
				try{
					if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.girllistview,parent,false);
					}else{
						v = convertView;
					}
					TextView nickNameText = (TextView)v.findViewById(R.id.girllist_nickname);
					nickNameText.setText(b.getNickName());
					
					TextView likeCountText = (TextView)v.findViewById(R.id.girllist_liketext);
					likeCountText.setText(b.getLikeCount() + "명이 좋아함");
					
					TextView contactText = (TextView)v.findViewById(R.id.gillist_contact);
					contactText.setText("(" + b.getPartnerName() + ")" + b.getContactName());
					
					if(b.getProfileThumbnail() == null){
						ImageView profileImg = (ImageView)v.findViewById(R.id.girllist_profile);
						profileImg.setImageResource(R.drawable.default_girl);
					}else{
						Bitmap cachedImage = null;
						final ImageView profileImg = (ImageView)v.findViewById(R.id.girllist_profile);
						try {
						     cachedImage = imageLoader.loadImage(Constants.HOST_NAME + "/bamStory" + b.getProfileThumbnail(), new ImageLoadedListener() {
						      public void imageLoaded(Bitmap imageBitmap) {
						    	  profileImg.setImageBitmap(imageBitmap);
						    	  notifyDataSetChanged();               
						    	  }
						      });
						 } catch (MalformedURLException e) {
						    	e.printStackTrace();
						 }
						 if( cachedImage != null ) {
							 profileImg.setImageBitmap(cachedImage);
						 }else{	
							 profileImg.setImageResource(R.drawable.placeholder);		 
						 }	
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}	
				
				return v;
			}
			
		}
	}
	
	private class GirlDataHandler extends AsyncTask<String,String,ArrayList<GirlInfo>>{

		@Override
		protected ArrayList<GirlInfo> doInBackground(String... params) {
			ArrayList<GirlInfo> girlDatas = new ArrayList<GirlInfo>();
			String startStr = params[0];

			
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("sessionKey",settings.getString(Constants.SESSION_KEY, null)));
			if(contactId != null){
				ps.add(new BasicNameValuePair("contactId",contactId));
			}else{
				ps.add(new BasicNameValuePair("start",startStr));
				ps.add(new BasicNameValuePair("limits","" + (maxPageSize + Integer.parseInt(startStr))));
			}			
			
			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				String url = null;
				if(contactId != null){
					url = "/bamStory/mobile/bam/listGirlByContact.do";
				}else{
					url = "/bamStory/mobile/bam/searchGirls.do";
				}
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + url);
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						JSONArray results = (JSONArray)jsonObject.get("results");
						for(int i=0; i < results.length(); i++){
							JSONObject girlObj = (JSONObject)results.getJSONObject(i);
							GirlInfo girl = new GirlInfo();
							girl.setId(girlObj.getString("id"));
							girl.setNickName(girlObj.getString("nickName"));
							girl.setContactId(girlObj.getString("contactId"));
							girl.setContactName(girlObj.getString("contactName"));
							girl.setPartnerName(girlObj.getString("partnerName"));
							girl.setProfile(girlObj.getString("profile"));
							girl.setLikeCount(girlObj.getInt("likeCount"));
							if(!girlObj.isNull("profileThumbnailUrl")){
								girl.setProfileThumbnail(girlObj.getString("profileThumbnailUrl"));
							}
							if(!girlObj.isNull("profileImage1Url")){
								girl.setProfileImage1(girlObj.getString("profileImage1Url"));
							}
							if(!girlObj.isNull("profileImage2Url")){
								girl.setProfileImage2(girlObj.getString("profileImage2Url"));
							}
							
							girlDatas.add(girl);
						}
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return girlDatas;
		}

		@Override
		protected void onPostExecute(ArrayList<GirlInfo> results) {
			super.onPostExecute(results);
			setGirls(results);
		}
		
	}
	
}

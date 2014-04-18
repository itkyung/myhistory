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
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.StoryInfo;
import com.bizwave.bamstory.util.ImageThreadLoader;
import com.bizwave.bamstory.util.ImageThreadLoader.ImageLoadedListener;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.view.CustomImageButton;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StoryActivity extends BamStoryBaseListActivity implements OnClickListener{
	private Context context;
	private MyProgressDialog progressDialog;
	private static final int maxPageSize = 15;
	private int start = 0;
	protected boolean myStoryFlag=false;
	//  #4f4f4f  

	
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		ListView list = getListView();
		list.setCacheColorHint(getResources().getColor(android.R.color.black));
		
		setListAdapter(new StoryListAdapter(this));
		
		refreshNewData();
	}
	
	public void setStories(ArrayList<StoryInfo> b){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		StoryListAdapter ad = (StoryListAdapter)this.getListAdapter();
		if(start == 0){
			if(ad.getStories() != null){
				ad.clearData();
			}
			ad.setStories(b);
		}else{
			ad.addStories(b);
		}
		
		ad.notifyDataSetChanged();
		this.getListView().forceLayout();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		
		MenuItem item = menu.add(groupId,Constants.REFRESH_MENU,3,"새로고침");
		item.setOnMenuItemClickListener(this);
		MenuItem item2 = menu.add(groupId,Constants.NEW_STORY_MENU,4,"글쓰기");
		item2.setOnMenuItemClickListener(this);
		if(!myStoryFlag){
			MenuItem item3 = menu.add(groupId,Constants.MY_STORY_MENU,5,"내가쓴수다");
			item3.setOnMenuItemClickListener(this);
		}
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
	
		switch (itemId){
		case Constants.REFRESH_MENU :
			start = 0;
			refreshNewData();
			return true;
		case Constants.NEW_STORY_MENU : 
			registStory();
			return true;
		case Constants.MY_STORY_MENU :
			goMyStory();
			return true;
		}
		
		return super.onMenuItemClick(item);
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
	
	private void goMyStory(){
		Intent intent = new Intent(StoryActivity.this,MyStoryActivity.class);
		startActivity(intent);
	}
	
	private void registStory(){
		
		String nickName = settings.getString(Constants.NICK_NAME, null);
		if(nickName == null|| "".equals(nickName)){
			buildAlertMessage("환경설정에서 닉네임을 먼저 설정하세요.",false);
			return;
		}
		Intent intent = new Intent(StoryActivity.this,RegistStoryActivity.class);
		Bundle param = new Bundle();
		param.putString("nickName", nickName);
		param.putBoolean("myStoryFlag", myStoryFlag);
		intent.putExtras(param);
		startActivityForResult(intent, Constants.NEW_STORY_MENU);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==Constants.NEW_STORY_MENU){
			if(resultCode == RESULT_OK){
				
				refreshNewData();
			}
		}
	}

	private void refreshNewData(){
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new StoryDataHandler().execute(""+start);
	
	}
	
	@Override
	public void onClick(View v) {
		if(v instanceof CustomImageButton){
			String contactId = ((CustomImageButton)v).getObjId();
			Bundle param = new Bundle();
			param.putString("contactId", contactId);
			param.putString("contactName", null);
			param.putString("partnerName", null);
			Intent intent = new Intent(this,ContactDetailActivity.class);
			intent.putExtras(param);
			startActivity(intent);
			
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(myStoryFlag){
			//list에 header view가 존재할경우에는 position 값을 하나 줄여야한다.
			position = position -1;
		}
		StoryListAdapter adapter = (StoryListAdapter)this.getListAdapter();
		ArrayList<StoryInfo> stories = adapter.getStories();
		if(stories != null){
			if(stories.size() > position){
				StoryInfo story = stories.get(position);
				Bundle param = new Bundle();
				param.putString("originalStoryId", story.getId());
				Intent intent = new Intent(this,ReplyActivity.class);
				intent.putExtras(param);
				startActivity(intent);
			}else{
				start = start + maxPageSize;
				refreshNewData();
			}

		}
		
	}
	
	private class StoryListAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<StoryInfo> stories = null;
		private HashMap<String,String> storyMap = new HashMap<String,String>();
		
		private ImageThreadLoader imageLoader = new ImageThreadLoader();
		
		
		public void clearData(){
			if(this.stories != null)
				stories.clear();
			storyMap.clear();
			
		}
		public StoryListAdapter(Context context){
			mContext = context;
		}
		
		public void setStories(ArrayList<StoryInfo> b){
			this.stories = b;
			for(StoryInfo s : b){
				this.storyMap.put(s.getId(), s.getId());
			}
		}
		
		public ArrayList<StoryInfo> getStories(){
			return stories;
		}
		
		public void addStories(ArrayList<StoryInfo> newStories){
			for(StoryInfo s : newStories){
				if(!this.storyMap.containsKey(s.getId())){
					stories.add(s);
					this.storyMap.put(s.getId(), s.getId());
				}
			}
		}
		@Override
		public int getCount() {
			return stories == null ? 0 : stories.size()+1;
		}

		@Override
		public Object getItem(int position) {
			if(stories == null) return null;
			if(stories.size() <= position){
				return null;
			}else{
				return stories.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AssetManager assetManager = mContext.getAssets();
			if(stories.size() <= position){
				View v=null;
				try{
					//if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.storylistview,parent,false);
					//}else{
					//	v = convertView;
					//}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				TextView nickNameText = (TextView)v.findViewById(R.id.storyNickNameText);
				nickNameText.setText("이전게시물보기");
				
				return v;
			}else{
				StoryInfo b = stories.get(position);
				View v=null;
				try{
					if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.storylistview,parent,false);
					}else{
						v = convertView;
					}
					TextView nickNameText = (TextView)v.findViewById(R.id.storyNickNameText);
					nickNameText.setText(b.getNickName());
					TextView replyCountText = (TextView)v.findViewById(R.id.storyReplyCountText);
					if(b.getReplyCount() > 0){
						replyCountText.setText("댓글 " + b.getReplyCount() + "개");
					}else{
						replyCountText.setText("");
					}
					TextView createdText = (TextView)v.findViewById(R.id.storyCreatedText);
					createdText.setText(b.getCreated());
					
					TextView contentsText = (TextView)v.findViewById(R.id.storyContents);
					contentsText.setText(b.getMessage());
					
					if(b.isBamstoryFlag()){
						Double d = Math.random();
						int rValue = d.intValue() % 2 +1;
						String profileImageName = "admin_" + rValue + ".png";
						ImageView profileImg = (ImageView)v.findViewById(R.id.storyProfileImage);
						
						AssetInputStream statusBuf = (AssetInputStream)assetManager.open("icon/" + profileImageName);
						Bitmap statusBitmap = BitmapFactory.decodeStream(statusBuf);
						profileImg.setImageBitmap(statusBitmap);
						statusBuf.close();					
						
					}else{
						if(b.getProfileImage().startsWith("default")){
							ImageView profileImg = (ImageView)v.findViewById(R.id.storyProfileImage);
							
							AssetInputStream statusBuf = (AssetInputStream)assetManager.open("icon/" + b.getProfileImage());
							Bitmap statusBitmap = BitmapFactory.decodeStream(statusBuf);
							profileImg.setImageBitmap(statusBitmap);
							statusBuf.close();
						}else{
							Bitmap cachedImage = null;
							final ImageView profileImg = (ImageView)v.findViewById(R.id.storyProfileImage);
							try {
							     cachedImage = imageLoader.loadImage(Constants.HOST_NAME + b.getProfileImage(), new ImageLoadedListener() {
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
					}
					
					if(b.isTopFlag()){
						//Background색을 변경시켜야한다. 
						v.setBackgroundResource(R.drawable.storyitem_selector);
						//v.setBackgroundColor(R.color.topRowColor);
					}else{
						v.setBackgroundColor(android.R.color.black);
					}
					
					CustomImageButton m = (CustomImageButton)v.findViewById(R.id.story_more);
					if(b.getUserType().equals("Contact")){
						m.setVisibility(View.VISIBLE);
						m.setObjId(b.getUserId());
						m.setOnClickListener((StoryActivity)context);
					}else{
						m.setVisibility(View.INVISIBLE);
					}
				}catch(Exception e){
					e.printStackTrace();
				}	
				
				return v;
			}
			
		}
	}
	
	private class StoryDataHandler extends AsyncTask<String,String,ArrayList<StoryInfo>>{

		@Override
		protected ArrayList<StoryInfo> doInBackground(String... params) {
			ArrayList<StoryInfo> storyDatas = new ArrayList<StoryInfo>();
			String startStr = params[0];

			
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("start",startStr));
			ps.add(new BasicNameValuePair("limits","" + (maxPageSize + Integer.parseInt(startStr))));
			ps.add(new BasicNameValuePair("sessionKey",settings.getString(Constants.SESSION_KEY, null)));
			
			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				String url = null;
				if(myStoryFlag){
					url = "/bamStory/mobile/bam/getMyStories.do";
				}else{
					url = "/bamStory/mobile/bam/searchStory.do";
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
							JSONObject storyObj = (JSONObject)results.getJSONObject(i);
							StoryInfo story = new StoryInfo();
							story.setId(storyObj.getString("id"));
							story.setNickName(storyObj.getString("nickName"));
							story.setMessage(storyObj.getString("contents"));
							story.setCreated(storyObj.getString("createdStr"));
							if(!storyObj.isNull("profileImage")){
								story.setProfileImage(storyObj.getString("profileImage"));
							}
							story.setReplyCount(storyObj.getInt("replyCount"));
							if(!storyObj.isNull("originalStoryId")){
								story.setOriginalStoryId(storyObj.getString("originalStoryId"));
							}
							story.setBamstoryFlag(storyObj.getBoolean("bamstoryFlag"));
							story.setTopFlag(storyObj.getBoolean("topFlag"));
							story.setUserId(storyObj.getString("userId"));
							story.setUserType(storyObj.getString("memberType"));
							storyDatas.add(story);
						}
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return storyDatas;
		}

		@Override
		protected void onPostExecute(ArrayList<StoryInfo> results) {
			super.onPostExecute(results);
			setStories(results);
		}
		
	}
	
}

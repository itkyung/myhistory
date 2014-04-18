package com.bizwave.bamstory.activity;

import java.net.MalformedURLException;
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
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.StoryInfo;

import com.bizwave.bamstory.util.ImageThreadLoader;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.ImageThreadLoader.ImageLoadedListener;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReplyActivity extends ListActivity implements OnMenuItemClickListener{
	private MyProgressDialog progressDialog;

	static final private int REFRESH_MENU = 100;
	static final private int NEW_STORY_MENU = 200;
	private SharedPreferences settings;
	private String originalStoryId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		Bundle param = getIntent().getExtras();
		originalStoryId = param.getString("originalStoryId");
		
		
		ListView list = getListView();
		list.setCacheColorHint(getResources().getColor(android.R.color.black));
		
		LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.storylistheader,list,false);
		list.addHeaderView(header);
		setListAdapter(new ReplyListAdapter(this));

		
		refreshNewData();		
		
	}

	public void setStories(ArrayList<StoryInfo> b){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		ReplyListAdapter ad = (ReplyListAdapter)this.getListAdapter();
		
		if(ad.getStories() != null)
			ad.getStories().clear();
		ad.setStories(b);
		
		
		ad.notifyDataSetChanged();
		this.getListView().forceLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		
		int groupId = 1;
		
		MenuItem item = menu.add(groupId,REFRESH_MENU,0,"새로고침");
		item.setOnMenuItemClickListener(this);
		MenuItem item2 = menu.add(groupId,NEW_STORY_MENU,1,"댓글쓰기");
		item2.setOnMenuItemClickListener(this);

		return true;
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
	
	
	private void registStory(){
		
		String nickName = settings.getString(Constants.NICK_NAME, null);
		if(nickName == null|| "".equals(nickName)){
			buildAlertMessage("환경설정에서 닉네임을 먼저 설정하세요.",false);
			return;
		}
		Intent intent = new Intent(ReplyActivity.this,RegistStoryActivity.class);
		Bundle param = new Bundle();
		param.putString("nickName", nickName);
		param.putString("originalStoryId", originalStoryId);
		intent.putExtras(param);
		startActivityForResult(intent, NEW_STORY_MENU);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==NEW_STORY_MENU){
			if(resultCode == RESULT_OK){
				refreshNewData();
			}
		}
	}

	private void refreshNewData(){
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new ReplyDataHandler().execute(""+originalStoryId);
	
	}	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()){
		case NEW_STORY_MENU :
			registStory();
			break;
		case REFRESH_MENU :
			refreshNewData();
			break;
		}
		return false;
	}
	

	private class ReplyListAdapter extends BaseAdapter{
		private ImageThreadLoader imageLoader = new ImageThreadLoader();
		private Context mContext;
		private ArrayList<StoryInfo> stories = null;
		
		public ReplyListAdapter(Context context){
			mContext = context;
		}
		
		public void setStories(ArrayList<StoryInfo> b){
			this.stories = b;
		}
		
		public ArrayList<StoryInfo> getStories(){
			return stories;
		}
		@Override
		public int getCount() {
			return stories == null ? 0 : stories.size();
		}

		@Override
		public Object getItem(int position) {
			if(stories == null) return null;
			return stories.get(position);
			
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AssetManager assetManager = mContext.getAssets();
			
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
				
			}catch(Exception e){
				e.printStackTrace();
			}	
			
			return v;
			
			
		}
	}
	
	private class ReplyDataHandler extends AsyncTask<String,String,ArrayList<StoryInfo>>{

		@Override
		protected ArrayList<StoryInfo> doInBackground(String... params) {
			ArrayList<StoryInfo> storyDatas = new ArrayList<StoryInfo>();
			String storyId = params[0];

			
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("storyId",storyId));
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));

			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/getReplies.do");
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
							story.setBamstoryFlag(storyObj.getBoolean("bamstoryFlag"));
							story.setTopFlag(storyObj.getBoolean("topFlag"));
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

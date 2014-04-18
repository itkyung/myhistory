package com.bizwave.bamstory.activity;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.SimpleBag;
import com.bizwave.bamstory.StoryInfo;
import com.bizwave.bamstory.util.CallUtil;
import com.bizwave.bamstory.util.ImageThreadLoader;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.ImageThreadLoader.ImageLoadedListener;
import com.bizwave.bamstory.view.CustomImageButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class GirlDetailActivity extends Activity implements OnClickListener,OnItemClickListener, OnMenuItemClickListener{
	private GirlInfo girl;
	private MyProgressDialog progressDialog;
	private Context context;
	private SharedPreferences settings;
	private static final int maxPageSize = 10;
	private int start = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		super.onCreate(savedInstanceState);
		this.context = this;
		setContentView(R.layout.girldetailview);
		Bundle param = getIntent().getExtras();
		this.girl = (GirlInfo)param.getSerializable("girlInfo");
		
		Button sudaBtn = (Button)findViewById(R.id.girl_sudaViewBtn);
		sudaBtn.setOnClickListener(this);
		sudaBtn.setSelected(true);
		Button imageBtn = (Button)findViewById(R.id.girl_imageViewBtn);
		imageBtn.setOnClickListener(this);
		
		initGirlInfo();
	}
	
	public void initGirlInfo(){
		TextView nickNameView = (TextView)findViewById(R.id.girl_nickName);
		nickNameView.setText(girl.getNickName());
		TextView contactNameView = (TextView)findViewById(R.id.girl_contactName);
		contactNameView.setText(girl.getContactName() + "-" + girl.getPartnerName());
		TextView likeCountView = (TextView)findViewById(R.id.girl_likeCount);
		likeCountView.setText(girl.getLikeCount() + "명 좋아함");
		TextView profileView = (TextView)findViewById(R.id.girl_profile);
		profileView.setText(girl.getProfile());
		
		ImageView profileImg = (ImageView)findViewById(R.id.girl_profileImg);
		if(girl.getProfileThumbnail() == null || !girl.getProfileThumbnail().startsWith("/")){
			profileImg.setImageResource(R.drawable.default_girl);		
		}else{
			setImg(profileImg,girl.getProfileThumbnail());
		}
		
		ImageView profileImage1 = (ImageView)findViewById(R.id.girl_profileImage1);
		if(girl.getProfileImage1() != null){
			setImg(profileImage1,girl.getProfileImage1());
		}

		ImageView profileImage2 = (ImageView)findViewById(R.id.girl_profileImage2);
		if(girl.getProfileImage2() != null){
			setImg(profileImage2,girl.getProfileImage2());
		}
		
		ListView storyView = (ListView)findViewById(R.id.girl_stories);
		storyView.setAdapter(new GirlStoryAdapter(this));
		storyView.setOnItemClickListener(this);
		getDatas();
	}

	public void getDatas(){
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new GirlStoryDataHandler().execute(""+start);
	}
	@Override
	public void onClick(View v) {
		LinearLayout l = (LinearLayout)findViewById(R.id.girl_sudaView);
		ScrollView i = (ScrollView)findViewById(R.id.girl_imageView);		
		if(v.getId() == R.id.girl_sudaViewBtn){
			l.setVisibility(View.VISIBLE);
			i.setVisibility(View.INVISIBLE);
		}else{
			l.setVisibility(View.INVISIBLE);
			i.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void setStories(ArrayList<StoryInfo> b){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		ListView storyView = (ListView)findViewById(R.id.girl_stories);
		GirlStoryAdapter ad = (GirlStoryAdapter)storyView.getAdapter();
		if(start == 0){
			if(ad.getStories() != null){
				ad.clearData();
			}
			ad.setStories(b);
		}else{
			ad.addStories(b);
		}
		
		ad.notifyDataSetChanged();
		storyView.forceLayout();
	}
	
	private void setImg(ImageView iv,String url){
		try{
			url = url + "&sessionKey=" + settings.getString(Constants.SESSION_KEY, null);
			URL imageURL = new URL(Constants.HOST_NAME + "/bamStory" + url);
			HttpURLConnection conn = (HttpURLConnection)imageURL.openConnection();             
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 10240);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			iv.setImageBitmap(bm);
			bis.close();   
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ListView storyView = (ListView)findViewById(R.id.girl_stories);
		GirlStoryAdapter adapter = (GirlStoryAdapter)storyView.getAdapter();
		ArrayList<StoryInfo> stories = adapter.getStories();
		if(stories != null){
			if(stories.size() > 0 && stories.size() < position){
				start = start + maxPageSize;
				getDatas();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		
		MenuItem item = menu.add(groupId,Constants.NEW_STORY_MENU,0,"수다쓰기");
		item.setOnMenuItemClickListener(this);
		MenuItem item2 = menu.add(groupId,Constants.LIKE_GIRL,1,"언니 좋아요");
		item2.setOnMenuItemClickListener(this);
		MenuItem item3 = menu.add(groupId,Constants.VIEW_CONTACT,2,"상무정보");
		item3.setOnMenuItemClickListener(this);
		
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()){
		case Constants.NEW_STORY_MENU : 
			registStory();
			break;
		case Constants.LIKE_GIRL : 
			likeGirl();
			break;
		case Constants.VIEW_CONTACT : 
			viewContact();
			break;
		}
		return false;
	}

	private void registStory(){
		String nickName = settings.getString(Constants.NICK_NAME, null);
		if(nickName == null|| "".equals(nickName)){
			buildAlertMessage("환경설정에서 닉네임을 먼저 설정하세요.",false);
			return;
		}
		Intent intent = new Intent(GirlDetailActivity.this,RegistStoryActivity.class);
		Bundle param = new Bundle();
		param.putString("nickName", nickName);
		param.putBoolean("myStoryFlag", false);
		param.putString("targetId", girl.getId());
		intent.putExtras(param);
		startActivityForResult(intent, Constants.NEW_STORY_MENU);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==Constants.NEW_STORY_MENU){
			if(resultCode == RESULT_OK){
				getDatas();
			}
		}
	}	
	
	private void likeGirl(){
		AlertDialog alert = new AlertDialog.Builder(this)
		.setMessage("찜하시겠습니까?")
		.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showProgress();
				new GirlLikeDataHandler().execute("");
			}
		})
		.setNegativeButton("NO",  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
		
	}
	
	private void viewContact(){
		Bundle param = new Bundle();
		param.putString("contactId", girl.getContactId());
		param.putString("contactName", girl.getContactName());
		param.putString("partnerName", girl.getPartnerName());
		Intent intent = new Intent(this,ContactDetailActivity.class);
		intent.putExtras(param);
		startActivity(intent);
	}

	public void buildAlertMessage(String msg,final boolean finish) {
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
	
	public void showProgress(){
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
	}
	
	private class GirlStoryAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<StoryInfo> stories = null;
		private HashMap<String,String> storyMap = new HashMap<String,String>();
		
		public void clearData(){
			if(this.stories != null)
				stories.clear();
			storyMap.clear();
			
		}
		public GirlStoryAdapter(Context context){
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
			if(stories.size() <= position){
				View v=null;
				try{
				
					v = LayoutInflater.from(mContext).inflate(
							com.bizwave.bamstory.R.layout.simplestorylistview,parent,false);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				TextView contactNameText = (TextView)v.findViewById(R.id.girl_storyContents);
				if(stories.size() == 0){
					contactNameText.setText("수다 남겨주세요~~");
				}else{
					contactNameText.setText("수다 더 보기");
				}
				return v;
			}else{
				StoryInfo b = stories.get(position);
				View v=null;
				try{
					if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.simplestorylistview,parent,false);
					}else{
						v = convertView;
					}
					TextView nickNameText = (TextView)v.findViewById(R.id.girl_storyNickNameText);
					nickNameText.setText(b.getNickName());
					
					TextView createdText = (TextView)v.findViewById(R.id.girl_storyCreatedText);
					createdText.setText(b.getCreated());
					
					TextView contentsText = (TextView)v.findViewById(R.id.girl_storyContents);
					contentsText.setText(b.getMessage());
					
					v.setBackgroundColor(android.R.color.black);
					
					
				}catch(Exception e){
					e.printStackTrace();
				}	
				
				return v;
			}
			
		}
		
	}

	private class GirlStoryDataHandler extends AsyncTask<String,String,ArrayList<StoryInfo>>{

		@Override
		protected ArrayList<StoryInfo> doInBackground(String... params) {
			ArrayList<StoryInfo> storyDatas = new ArrayList<StoryInfo>();
			String startStr = params[0];

			
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("start",startStr));
			ps.add(new BasicNameValuePair("limits","" + (maxPageSize + Integer.parseInt(startStr))));
			ps.add(new BasicNameValuePair("sessionKey",settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("targetId", girl.getId()));
			
			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				String url = null;
				url = "/bamStory/mobile/bam/searchStoryForTarget.do";
				
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
	
	private class GirlLikeDataHandler extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			ArrayList<StoryInfo> storyDatas = new ArrayList<StoryInfo>();
			
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("sessionKey",settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("girlId", girl.getId()));
			
			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");	
				String url = null;
				url = "/bamStory/mobile/bam/likeGirl.do";
				
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + url);
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						boolean success = jsonObject.getBoolean("success");
						if(success)
							return null;
						String message = jsonObject.getString("message");
						return message;
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
					return exception.getMessage();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog != null){
				progressDialog.dismiss();
			}
			if(result == null){
				buildAlertMessage("찜하기에 성공하셨습니다.잠시후에 확인하세요.", false);
			}else{
				buildAlertMessage(result, false);
			}
		}
		
	}	
}

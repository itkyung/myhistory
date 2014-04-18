package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.db.Category;
import com.bizwave.bamstory.db.DBHelper;

public class BamStoryActivity extends BamStoryBaseListActivity {
	//private WebView wv;
	
	int location = 0;
	
	private String defaultParentId = new String("ROOT");
	
	ArrayList<Category> categories = null;
	ArrayList<String> categoryArray = null;
	ArrayAdapter<String> adapter = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bamstory);
		try {
			makeAdapter(defaultParentId);
			setListAdapter(adapter);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void makeAdapter(String parentId) {
		this.defaultParentId = parentId;
		DBHelper dbHelper = new DBHelper(this.getApplicationContext());
		
		if(categories != null) {
			categories.clear();
		} else {
			categories = new ArrayList<Category>();
		}
		categories.addAll(dbHelper.getCategories(parentId));
		if(adapter == null) {
			categoryArray = new ArrayList<String>();
			for(int i=0; i<categories.size(); i++) {
				categoryArray.add(categories.get(i).getText());
			}			
			adapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryArray);
		} else {
			categoryArray.clear();
			for(int i=0; i<categories.size(); i++) {
				categoryArray.add(categories.get(i).getText());
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	 protected void onListItemClick(
			 android.widget.ListView l, android.view.View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(!this.defaultParentId.equals("ROOT")){
			//현재 페이지의 상위가 ROOT가 아니면 2단계이기 때문에 바로 파트너,상무 리스트 Activity를 띄운다. 
			Bundle param = new Bundle();
			Category ca = categories.get(position);
			param.putString("categoryId", ca.getId());
			param.putString("categoryName", ca.getText());
			Intent intent = new Intent(this,PartnerContactActivity.class);
			intent.putExtras(param);
			startActivity(intent);
		}else{
			if(categories.get(position).getId() != null) {
				makeAdapter(categories.get(position).getId());
			}
		}
	}
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(this.defaultParentId.equals("ROOT")){
				return super.onKeyDown(keyCode, event);
			}
			DBHelper dbHelper = new DBHelper(this.getApplicationContext());
			Category category = dbHelper.getCategory(categories.get(0).getParentId());
			makeAdapter(category.getParentId());
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	
//	public void viewPartnerInfo(String id){
//		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceId = manager.getDeviceId(); 
//		String viewPartnerUrl = Constants.HOST_NAME + "/bamStory/mobile/bam/viewPartner.do?phoneType=ANDROID&id=" + id + "&deviceId=" + deviceId;
//		wv.loadUrl(viewPartnerUrl);
//	}

}

//public class BamStoryActivity extends BasicActivity {
//	private String bamstoryUrl = Constants.HOST_NAME + "/bamStory/mobile/bam/listCategory.do?phoneType=ANDROID";
//	private WebView wv;
//
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		//getWindow().requestFeature(Window.FEATURE_PROGRESS);
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.bamstory);
//		
//		//final Activity activity = this;
//
//		
//		wv = (WebView) findViewById(R.id.bamstoryview);
//		wv.setVerticalScrollBarEnabled(false);
//		wv.getSettings().setJavaScriptEnabled(true);
//		wv.setWebViewClient(new BamStoryWebViewClient(this));
////		wv.setWebChromeClient(new WebChromeClient(){
////			@Override
////			public void onProgressChanged(WebView view, int newProgress) {
////				activity.setProgress(newProgress * 1000);
////			}
////			
////		});
//		
//		
//		bamstoryUrl = bamstoryUrl + "&deviceId=" + deviceId;
//		
//		wv.loadUrl(bamstoryUrl);
//		
//	}
//	
//	public void viewPartnerInfo(String id){
//		String viewPartnerUrl = Constants.HOST_NAME + "/bamStory/mobile/bam/viewPartner.do?phoneType=ANDROID&id=" + id + "&deviceId=" + deviceId;
//		wv.loadUrl(viewPartnerUrl);
//	}
//
//	@Override
//	public void showProgress() {
//		showDialog(PROGRESS1);
//	}
//
//	@Override
//	public void stopProgress() {
//		dismissDialog(PROGRESS1);		
//	}
//	
//	
//	
//}

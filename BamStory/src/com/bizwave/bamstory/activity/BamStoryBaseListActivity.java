package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.Constants;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public abstract class BamStoryBaseListActivity extends ListActivity implements OnMenuItemClickListener{
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		
		int groupId = 0;
		
		MenuItem item = menu.add(groupId,Constants.GO_HOME,0,"홈으로");
		item.setOnMenuItemClickListener(this);
		MenuItem item2 = menu.add(groupId,Constants.GO_NOTICE,1,"공지사항");
		item2.setOnMenuItemClickListener(this);
//		MenuItem item3 = menu.add(groupId,Constants.GO_POLL,2,"설문조사");
//		item3.setOnMenuItemClickListener(this);
		MenuItem item4 = menu.add(groupId,Constants.GO_CONFIG,2,"환경설정");
		item4.setOnMenuItemClickListener(this);
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
	
		switch (itemId){
		case Constants.GO_HOME :

			return true;
		case Constants.GO_NOTICE : 
			goNotice();
			return true;
		case Constants.GO_POLL : 
			goPoll();
			return true;
		case Constants.GO_CONFIG :
			goConfig();
			return true;
		}
		
		return false;
	}	
	
	private void goConfig(){
		Intent i = new Intent(this,ConfigActivity.class);
		startActivity(i);
	}
	private void goNotice(){
		Intent i = new Intent(this,NoticeActivity.class);
		startActivity(i);
	}
	private void goPoll(){
		Intent i = new Intent(this,PollActivity.class);
		startActivity(i);
	}	
}

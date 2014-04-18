package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainPageActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		final ImageButton sudaBtn = (ImageButton)findViewById(R.id.main_sudaBtn);
		sudaBtn.setOnClickListener(this);
		final ImageButton bamBtn = (ImageButton)findViewById(R.id.main_bamstoryBtn);
		bamBtn.setOnClickListener(this);
		final ImageButton lBtn = (ImageButton)findViewById(R.id.main_locationBtn);
		lBtn.setOnClickListener(this);
		final ImageButton gBtn = (ImageButton)findViewById(R.id.main_girlBtn);
		gBtn.setOnClickListener(this);
		final ImageButton eBtn = (ImageButton)findViewById(R.id.main_eventBtn);
		eBtn.setOnClickListener(this);
		final ImageButton cBtn = (ImageButton)findViewById(R.id.main_configBtn);
		cBtn.setOnClickListener(this);
		final ImageButton nBtn = (ImageButton)findViewById(R.id.main_noticeBtn);
		nBtn.setOnClickListener(this);
		final ImageButton pBtn = (ImageButton)findViewById(R.id.main_pollBtn);
		pBtn.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch(v.getId()){
		case R.id.main_sudaBtn:
			i = new Intent(this,BamStoryTabActivity.class);
			i.putExtra("defaultTab", Constants.TAB_SUDA);
			startActivity(i);
			break;
		case R.id.main_bamstoryBtn:
			i = new Intent(this,BamStoryTabActivity.class);
			i.putExtra("defaultTab", Constants.TAB_BAMSTORY);
			startActivity(i);			
			break;
		case R.id.main_locationBtn:
			i = new Intent(this,BamStoryTabActivity.class);
			i.putExtra("defaultTab", Constants.TAB_LOCATION);
			startActivity(i);					
			break;
		case R.id.main_girlBtn:
			i = new Intent(this,BamStoryTabActivity.class);
			i.putExtra("defaultTab", Constants.TAB_GIRL);
			startActivity(i);					
			break;
		case R.id.main_eventBtn:
			i = new Intent(this,BamStoryTabActivity.class);
			i.putExtra("defaultTab", Constants.TAB_EVENT);
			startActivity(i);					
			break;
		case R.id.main_noticeBtn:
			i = new Intent(this,NoticeActivity.class);
			startActivity(i);				
			break;
		case R.id.main_pollBtn:
			i = new Intent(this,PollActivity.class);
			startActivity(i);					
			break;
		case R.id.main_configBtn:
			i = new Intent(this,ConfigActivity.class);
			startActivity(i);				
			break;
		}
		
	}

	
}

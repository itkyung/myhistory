package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class CommunityActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.community);
		
		ImageButton noticeBtn = (ImageButton)findViewById(R.id.NoticeBtn);
		noticeBtn.setOnClickListener(this);
		ImageButton pollBtn = (ImageButton)findViewById(R.id.PollBtn);
		pollBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.NoticeBtn:
			Intent intent = new Intent(this,NoticeActivity.class);
			startActivity(intent);
			break;
		case R.id.PollBtn:
			Intent i = new Intent(this,PollActivity.class);
			startActivity(i);
			break;
		}
	}

	
	
	
}

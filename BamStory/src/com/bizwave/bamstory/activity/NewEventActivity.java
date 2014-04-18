package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class NewEventActivity extends BamStoryBaseActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.newevent);
		ImageButton biddingBtn = (ImageButton)findViewById(R.id.BiddingBtn);
		biddingBtn.setOnClickListener(this);
		ImageButton salesBtn = (ImageButton)findViewById(R.id.SalesBtn);
		salesBtn.setOnClickListener(this);
		ImageButton drinkBtn = (ImageButton)findViewById(R.id.DrinkBtn);
		drinkBtn.setOnClickListener(this);
		ImageButton dressBtn = (ImageButton)findViewById(R.id.DressBtn);
		dressBtn.setOnClickListener(this);
		ImageButton etcBtn = (ImageButton)findViewById(R.id.EtcBtn);
		etcBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		String eventId = null;
		String eventName = null;
		switch(v.getId()){
		case R.id.BiddingBtn:
			Intent intent = new Intent(this,CurrentMonthBiddingActivity.class);
			startActivity(intent);
			return;
		case R.id.SalesBtn:
			eventId = "PRICE";
			eventName = "가격할인";
			break;
		case R.id.DrinkBtn:
			eventId = "BOTTLE";
			eventName = "술추가";
			break;
		case R.id.DressBtn:
			eventId = "COSTUME";
			eventName = "복장";
			break;
		case R.id.EtcBtn:
			eventId = "ETC";
			eventName = "기타";
			break;
		}
		
		Bundle param = new Bundle();
		param.putString("eventId", eventId);
		param.putString("eventName", eventName);
		Intent intent = new Intent(this,PartnerContactActivity.class);
		intent.putExtras(param);
		startActivity(intent);
	}

	
	
	
}

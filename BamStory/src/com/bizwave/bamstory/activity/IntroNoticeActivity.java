package com.bizwave.bamstory.activity;

import com.bizwave.bamstory.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class IntroNoticeActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intronotice);
		final ImageView background = (ImageView)findViewById(R.id.intro_background);
		background.setImageResource(R.drawable.intro_notice);
		
		final Button button = (Button)findViewById(R.id.intro_ok);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		setResult(RESULT_OK);
		finish();
	}

}

package com.bizwave.bamstory.activity;


import com.bizwave.bamstory.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MyStoryActivity extends StoryActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myStoryFlag = true;
		ListView list = getListView();
		LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.storylistheader,list,false);
        final TextView title = (TextView)header.findViewById(R.id.syListTitleBar);
        title.setText("내가쓴 수다");
		list.addHeaderView(header);
		super.onCreate(savedInstanceState);
	}
	

}

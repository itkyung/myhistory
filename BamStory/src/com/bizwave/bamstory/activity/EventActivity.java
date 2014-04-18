package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.db.Category;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.db.Event;
import com.bizwave.bamstory.util.BamStoryWebViewClient;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

public class EventActivity extends ListActivity {
		
	private ArrayList<Event> events;
	private ArrayAdapter<String> adapter = null;
	ArrayList<String> eventArray = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventview);
		try {
			makeAdapter();
			setListAdapter(adapter);
		} catch(Exception exception) {
			exception.printStackTrace();
		}		
	}
	
	private void makeAdapter() {
		DBHelper dbHelper = new DBHelper(this.getApplicationContext());
		
		if(events != null) {
			events.clear();
		} else {
			events = new ArrayList<Event>();
		}
		events.addAll(dbHelper.getEvents());
		if(adapter == null) {
			eventArray = new ArrayList<String>();
			for(int i=0; i<events.size(); i++) {
				eventArray.add(events.get(i).getName());
			}			
			adapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventArray);
		} else {
			eventArray.clear();
			for(int i=0; i<events.size(); i++) {
				eventArray.add(events.get(i).getName());
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	 protected void onListItemClick(
			 android.widget.ListView l, android.view.View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Bundle param = new Bundle();
		Event e = events.get(position);
		param.putString("eventId", e.getId());
		param.putString("eventName", e.getName());
		Intent intent = new Intent(this,PartnerContactActivity.class);
		intent.putExtras(param);
		startActivity(intent);
	}
	
}

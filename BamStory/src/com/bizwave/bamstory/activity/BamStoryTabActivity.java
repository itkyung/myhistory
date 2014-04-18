package com.bizwave.bamstory.activity;





import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.R;

public class BamStoryTabActivity extends TabActivity {
    /** Called when the activity is first created. */
	private TabHost tabHost;


	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	int defaultTab = getIntent().getIntExtra("defaultTab", 0);
    	
    	
        super.onCreate(savedInstanceState);
        tabHost = getTabHost();
        
        tabHost.addTab(tabHost.newTabSpec("community")
        		.setIndicator("밤스수다방",getResources().getDrawable(R.drawable.suda))
        		.setContent(new Intent(this,StoryActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("bamstory")
                .setIndicator("밤스토리",getResources().getDrawable(R.drawable.bs))
                .setContent(new Intent(this,BamStoryActivity.class)));        
        tabHost.addTab(tabHost.newTabSpec("location")
                .setIndicator("위치기반",getResources().getDrawable(R.drawable.gps))
                .setContent(new Intent(this,MyLocationActivity.class)));          
        tabHost.addTab(tabHost.newTabSpec("config")
        		.setIndicator("밤스언니네",getResources().getDrawable(R.drawable.tab_girl))
        		.setContent(new Intent(this,GirlActivity.class)));  
        tabHost.addTab(tabHost.newTabSpec("event")
                .setIndicator("이벤트",getResources().getDrawable(R.drawable.event))
                .setContent(new Intent(this,NewEventActivity.class)));
        
        tabHost.setCurrentTab(defaultTab);
    }
    
    public void viewPartnerInfo(String id){
    	tabHost.setCurrentTab(1);
    	Activity ac = this.getCurrentActivity();
//    	if(ac instanceof BamStoryActivity){
//    		((BamStoryActivity)ac).viewPartnerInfo(id);
//    	}
    }
    
    
    public void viewInMap(PartnerInfo partner){
    	tabHost.setCurrentTab(1);
    	Activity ac = this.getCurrentActivity();
    	if(ac instanceof MyLocationActivity){
    		((MyLocationActivity)ac).drawPartner(partner);
    	}
    	
    }
}
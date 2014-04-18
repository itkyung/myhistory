/**
 * 업소와 상무 리스트를 보여주는 액티비티
 * 업소 이름,종류, 상무이름 그리고 평점 데이타를 보여준다.
 */

package com.bizwave.bamstory.activity;


import java.util.ArrayList;

import com.bizwave.bamstory.R;
import com.bizwave.bamstory.db.Category;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.db.Partner;
import com.bizwave.bamstory.util.MyProgressDialog;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;



public class PartnerContactActivity extends ListActivity {
	public static final String CATEGORY_MODE = "CATEGORY";
	public static final String EVENT_MODE = "EVENT";
	private String id;
	private String name;

	
	private String currentMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle param = getIntent().getExtras();
		//setContentView(R.layout.partnercontact);
		
		if(param.containsKey("categoryId")){
			this.name = param.getString("categoryName");
			this.id = param.getString("categoryId");
			currentMode = CATEGORY_MODE;
		}else{
			this.name = param.getString("eventName");
			this.id = param.getString("eventId");		
			currentMode = EVENT_MODE;
		}
		ListView list = getListView();
		
		LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.contactlistheader,list,false);
		list.addHeaderView(header);
		TextView title = (TextView)header.findViewById(R.id.titleBar);
		title.setText(name);
		setListAdapter(new PartnerContactListAdapter(this));
	}

	
	
	public String getId() {
		return id;
	}



	public String getCategoryName() {
		return name;
	}



	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PartnerContactListAdapter adapter = (PartnerContactListAdapter)this.getListAdapter();
		ArrayList<Partner> partners = adapter.getPartners();
		position = position - 1;
		if(partners != null){
			Partner partner = partners.get(position);
			Bundle param = new Bundle();
			param.putString("contactId", partner.getContactId());
			param.putString("contactName", partner.getContactName());
			param.putString("partnerName", partner.getPartnerName());
			Intent intent = new Intent(this,ContactDetailActivity.class);
			intent.putExtras(param);
			startActivity(intent);
		}
		//super.onListItemClick(l, v, position, id);
	}



	private class PartnerContactListAdapter extends BaseAdapter{
		private Context mContext;
		private DBHelper dbHelper;
		private ArrayList<Partner> partners;
		
		public PartnerContactListAdapter(Context context){
			mContext = context;
			dbHelper = new DBHelper(context);
			partners = dbHelper.getPartners(getId(),currentMode.equals(CATEGORY_MODE) ? true : false);		
			
		}
		
		@Override
		public int getCount() {
			if(partners != null)
				return partners.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(partners != null)
				return partners.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(partners != null){
				AssetManager assetManager = mContext.getAssets();
				Partner p = partners.get(position);
				View v=null;
				try{
					if(convertView == null){
						v = LayoutInflater.from(mContext).inflate(
								com.bizwave.bamstory.R.layout.partnercontactlistview,parent,false);
					}else{
						v = convertView;
					}
					TextView title = (TextView)v.findViewById(R.id.partnerText);
					title.setText("[" +p.getPartnerName() + "]" + p.getContactName());
					
					ImageView typeImg = (ImageView)v.findViewById(R.id.partnerTypeImg);
					
					AssetInputStream typeBuf = (AssetInputStream)assetManager.open("icon/" + p.getPartnerType().toLowerCase()+".png");
					Bitmap typeBitmap = BitmapFactory.decodeStream(typeBuf);
					typeImg.setImageBitmap(typeBitmap);
					typeBuf.close();
					
					ImageView evalImg = (ImageView)v.findViewById(R.id.partnerEvaluationImg);
					AssetInputStream evalBuf = (AssetInputStream)assetManager.open("icon/e" + p.getEvaluationImageName());
					Bitmap eBitmap = BitmapFactory.decodeStream(evalBuf);
					evalImg.setImageBitmap(eBitmap);
					evalBuf.close();
					
					TextView evalCount = (TextView)v.findViewById(R.id.contact_evalcount);
					evalCount.setText(p.getEvalCount() + "명이 평가함");
					
					if(p.getHaveGirl() == 1){
						ImageView certFlag = (ImageView)v.findViewById(R.id.contact_girlImg);
						certFlag.setVisibility(View.VISIBLE);	
					}else{
						ImageView certFlag = (ImageView)v.findViewById(R.id.contact_girlImg);
						certFlag.setVisibility(View.GONE);
					}
					
					if(p.getBamsCertFlag() == 1){
						ImageView certFlag = (ImageView)v.findViewById(R.id.bamsCertFlagImg);
						certFlag.setVisibility(View.VISIBLE);
					}else{
						ImageView certFlag = (ImageView)v.findViewById(R.id.bamsCertFlagImg);
						certFlag.setVisibility(View.GONE);						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				return v;
			}
			return null;
		}
	
		public ArrayList<Partner> getPartners(){
			return this.partners;
		}
		
		
	}
	
}

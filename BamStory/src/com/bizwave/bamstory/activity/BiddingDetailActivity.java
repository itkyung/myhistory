package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bizwave.bamstory.BiddingActivity;
import com.bizwave.bamstory.BiddingDetailInfo;
import com.bizwave.bamstory.BiddingSimpleInfo;
import com.bizwave.bamstory.EvalInfo;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.util.MyProgressDialog;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BiddingDetailActivity extends Activity implements OnClickListener{
	private String biddingId;
	private String deviceId;
	private MyProgressDialog progressDialog;
	private BiddingDetailInfo biddingInfo;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		setContentView(R.layout.biddingdetail);
		Bundle param = getIntent().getExtras();
		this.biddingId = param.getString("biddingId");
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		this.deviceId = manager.getDeviceId();		
		
		((ListView)findViewById(R.id.bdActivities)).setAdapter(new ActivityAdapter(this));
		
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new BiddingDetailDataHandler().execute("");
	}

	public void setContent(BiddingDetailInfo info){
		if(progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}
		TextView titleBar = (TextView)findViewById(R.id.bdTitleBar);
		titleBar.setText("[" + info.getPartnerName() +"]" + info.getContactName());
		
		TextView title = (TextView)findViewById(R.id.bdTitleText);
		title.setText(info.getTitle());
		
		TextView winningBidder = (TextView)findViewById(R.id.bdWinningBidder);
		winningBidder.setText(info.getSuccessfulBidNickName());
		
		TextView startDate = (TextView)findViewById(R.id.bdStartDateText);
		startDate.setText(info.getStartDate());
		
		TextView endDate = (TextView)findViewById(R.id.bdEndDateText);
		endDate.setText(info.getEndDate());
		
		TextView startBidPrice = (TextView)findViewById(R.id.bdStartPriceText);
		startBidPrice.setText(info.getStartBidPrice() + "만원");
		
		if(info.getWinningBidPrice() != null){
			TextView winningPrice = (TextView)findViewById(R.id.bdWinningPriceText);
			winningPrice.setText(info.getWinningBidPrice() + "만원"); 
		}
		
		TextView description = (TextView)findViewById(R.id.bdDescriptionText);
		description.setText(info.getDescription());
		
		String statusLabel = null;
		if (info.getReady() != null && info.getReady()) {
			statusLabel = "경매대기";
		}else {
			if (info.getClosed() != null && info.getClosed()){
				if (info.getSuccessfulBid() != null && info.getSuccessfulBid()) {
					statusLabel = "낙찰";
				}else{
					statusLabel = "유찰";
				}
			}else{
				statusLabel = "진행중";
			}
		}
		
		TextView status = (TextView)findViewById(R.id.bdStatusText);
		status.setText(statusLabel);
		
		Button bidBtn = (Button)findViewById(R.id.bdBtn);
		bidBtn.setOnClickListener(this);
		if(info.getClosed() != null && info.getClosed()){
			bidBtn.setText("결과확인");
			
		}else{
			bidBtn.setText("경매참여");
		}
		
		ActivityAdapter ad = (ActivityAdapter)((ListView)findViewById(R.id.bdActivities)).getAdapter();
		ad.notifyDataSetChanged();
		ad.setActivities(info.getActivities());
		findViewById(R.id.bdActivities).forceLayout();
		this.biddingInfo = info;
	}
	
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.bdBtn){
			if(this.biddingInfo == null) return;
			if(this.biddingInfo.getClosed() != null && this.biddingInfo.getClosed())
				viewResult();
			else
				participateBidding();
		}
	}

	private void viewResult(){
		if(biddingInfo.getClosed() == null || !biddingInfo.getClosed()){
			buildAlertMessage("아직 경매가 진행중입니다.",false);
			return;
		}
		if(progressDialog == null){
			progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		}
		new BiddingSubmitDataHandler().execute("");
	}

	private void buildAlertMessage(String msg,final boolean finish) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(msg)
	           .setCancelable(false)
	           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   if(finish)
	            		   finish();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void participateBidding(){
		Bundle param = new Bundle();
		param.putString("biddingId", this.biddingId);
		param.putString("partnerName", this.biddingInfo.getPartnerName());
		param.putString("contactName", this.biddingInfo.getContactName());
		
		Integer highPrice = biddingInfo.getStartBidPrice();
		for(BiddingActivity ac : biddingInfo.getActivities()){
			if(ac.getBidPrice().intValue() > highPrice.intValue()){
				highPrice = ac.getBidPrice();
			}
		}
		param.putInt("highPrice", highPrice);
		Intent intent = new Intent(this,ParticipateBiddingActivity.class);
		intent.putExtras(param);
		startActivity(intent);		
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
	
	private class BiddingSubmitDataHandler extends AsyncTask<String,String,BiddingSimpleInfo>{

		@Override
		protected BiddingSimpleInfo doInBackground(String... arg0) {
			BiddingSimpleInfo info = new BiddingSimpleInfo();
			try{
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
				ps.add(new BasicNameValuePair("biddingId",biddingId));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/getMyBiddingResultBySession.do");
				httpPost.setEntity(entity);
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);
					if(jsonObject != null) {			
						if(!jsonObject.isNull("successfulBid"))
							info.setSuccessfulBid(jsonObject.getBoolean("successfulBid"));
						info.setWinningBidPrice(jsonObject.getString("winningBidPrice"));
						
					}
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			return info;
		}

		@Override
		protected void onPostExecute(BiddingSimpleInfo result) {
			super.onPostExecute(result);
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
			if(result == null){
				buildAlertMessage("데이타 통신에 문제가 있습니다. 잠시후에 다시 시도하세요.",false);
			}else{
				if(result.getSuccessfulBid() != null && result.getSuccessfulBid()){
					buildAlertMessage("축하합니다!. [" + result.getWinningBidPrice() + "만원]에 경매에 낙찰되셨습니다. 업소 방문시에 이 쿠폰을 보여주십시요.",false);
				}else{
					buildAlertMessage("아쉽습니다. 이번 경매에는 유찰되셨습니다. 다음 기회에 다시 참여해주세요. 감사합니다.",false);
				}
			}
		}	
	}
	
	private class BiddingDetailDataHandler extends AsyncTask<String, String, BiddingDetailInfo>{

		@Override
		protected BiddingDetailInfo doInBackground(String... params) {
			BiddingDetailInfo bidding = new BiddingDetailInfo();
			try{
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
				ps.add(new BasicNameValuePair("id",biddingId));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/getBiddingDetailInfo.do");
				httpPost.setEntity(entity);
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						bidding.setId(jsonObject.getString("id"));
						bidding.setPartnerName(jsonObject.getString("partnerName"));
						bidding.setContactName(jsonObject.getString("contactName"));
						bidding.setDescription(jsonObject.getString("description"));
						bidding.setTitle(jsonObject.getString("title"));
						bidding.setSuccessfulBidderDeviceId(jsonObject.getString("successfulBidderDeviceId"));
						bidding.setSuccessfulBidNickName(jsonObject.getString("successfulBidNickName"));
						if(!jsonObject.isNull("closed")){
							bidding.setClosed(jsonObject.getBoolean("closed"));
						}
						if(!jsonObject.isNull("successfulBid")){
							bidding.setSuccessfulBid(jsonObject.getBoolean("successfulBid"));
						}						
						if(!jsonObject.isNull("ready")){
							bidding.setReady(jsonObject.getBoolean("ready"));
						}						
						bidding.setStartDate(jsonObject.getString("startDate"));
						bidding.setEndDate(jsonObject.getString("endDate"));
						bidding.setStartBidPrice(jsonObject.getInt("startBidPrice"));
						if(!jsonObject.isNull("winningBidPrice"))
							bidding.setWinningBidPrice(jsonObject.getInt("winningBidPrice"));
						
						JSONArray activityObjs = jsonObject.getJSONArray("activities");
						ArrayList<BiddingActivity> activities = new ArrayList<BiddingActivity>();
						for(int i=0; i < activityObjs.length(); i++){
							JSONObject activityObj = activityObjs.getJSONObject(i);
							BiddingActivity activity = new BiddingActivity();
							activity.setId(activityObj.getString("id"));
							activity.setNickName(activityObj.getString("nickName"));
							activity.setBidDate(activityObj.getString("bidDate"));
							activity.setBidPrice(activityObj.getInt("bidPrice"));
							if(!activityObj.isNull("successfulBid")){
								activity.setSuccessfulBid(activityObj.getBoolean("successfulBid"));
							}
							activities.add(activity);
						}
						bidding.setActivities(activities);
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			
			return bidding;
		}

		@Override
		protected void onPostExecute(BiddingDetailInfo result) {

			super.onPostExecute(result);
			setContent(result);
		}	
		
	}
	
	
	private class ActivityAdapter extends BaseAdapter{
		private Context pContext;
		private ArrayList<BiddingActivity> activities;
		private LayoutInflater mInflater;
		
		public ActivityAdapter(Context context){
			this.pContext = context;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setActivities(ArrayList<BiddingActivity> activities){
			this.activities = activities;
		}
		
		@Override
		public int getCount() {
			if(this.activities != null){
				return this.activities.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if(this.activities != null){
				return this.activities.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v=null;
			try{
				if(convertView == null){
					v = LayoutInflater.from(pContext).inflate(
							com.bizwave.bamstory.R.layout.biddingactivityitem,parent,false);
				}else{
					v = convertView;
				}
				
				BiddingActivity b = this.activities.get(position);
				TextView nick = (TextView)v.findViewById(R.id.bdActivityNickName);
				nick.setTextColor(getResources().getColor(android.R.color.black));
				nick.setText(b.getNickName());

				TextView price = (TextView)v.findViewById(R.id.bdActivityPrice);
				price.setTextColor(getResources().getColor(android.R.color.black));
				price.setText(b.getBidPrice() + "만원(" + b.getBidDate() + ")");
				
			}catch(Exception e){
				e.printStackTrace();
			}		
			return v;
		}
		
	}	
}

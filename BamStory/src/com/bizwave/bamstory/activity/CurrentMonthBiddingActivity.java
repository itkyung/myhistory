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

import com.bizwave.bamstory.BiddingSimpleInfo;
import com.bizwave.bamstory.Constants;

import com.bizwave.bamstory.R;
import com.bizwave.bamstory.util.MyProgressDialog;





import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CurrentMonthBiddingActivity extends ListActivity implements OnClickListener{
	private MyProgressDialog progressDialog;
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		ListView list = getListView();
		
		LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.biddinglistheader,list,false);
		list.addHeaderView(header);
		TextView title = (TextView)header.findViewById(R.id.bdListTitleBar);
		title.setText("이달의 경매");
		setListAdapter(new BiddingListAdapter(this));
		
		Button myBidBtn = (Button)header.findViewById(R.id.bdMyBiddingBtn);
		myBidBtn.setOnClickListener(this);
		
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		
		new BiddingDataHandler().execute("");
	}
	
	public void setBiddings(ArrayList<BiddingSimpleInfo> b){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		BiddingListAdapter ad = (BiddingListAdapter)this.getListAdapter();
		ad.setBiddings(b);
		ad.notifyDataSetChanged();
		this.getListView().forceLayout();
	}
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.bdMyBiddingBtn){
			Intent intent = new Intent(this,MyBiddingActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		BiddingListAdapter adapter = (BiddingListAdapter)this.getListAdapter();
		ArrayList<BiddingSimpleInfo> biddings = adapter.getBiddings();
		position = position - 1;
		if(biddings != null){
			BiddingSimpleInfo bidding = biddings.get(position);
			Bundle param = new Bundle();
			param.putString("biddingId", bidding.getId());
			Intent intent = new Intent(this,BiddingDetailActivity.class);
			intent.putExtras(param);
			startActivity(intent);
		}
		//super.onListItemClick(l, v, position, id);
	}
	
	private class BiddingListAdapter extends BaseAdapter{
		private Context mContext;
		private ArrayList<BiddingSimpleInfo> biddings = null;
		
		public BiddingListAdapter(Context context){
			mContext = context;
		}
		
		public void setBiddings(ArrayList<BiddingSimpleInfo> b){
			this.biddings = b;
		}
		
		public ArrayList<BiddingSimpleInfo> getBiddings(){
			return biddings;
		}
		@Override
		public int getCount() {
			return biddings == null ? 0 : biddings.size();
		}

		@Override
		public Object getItem(int position) {
			if(biddings == null) return null;
			return biddings.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AssetManager assetManager = mContext.getAssets();
			BiddingSimpleInfo b = biddings.get(position);
			View v=null;
			try{
				if(convertView == null){
					v = LayoutInflater.from(mContext).inflate(
							com.bizwave.bamstory.R.layout.biddinglistview,parent,false);
				}else{
					v = convertView;
				}
				TextView partner = (TextView)v.findViewById(R.id.biddingPartnerText);
				partner.setText("[" +b.getPartnerName() + "]" + b.getContactName());
				
				TextView title = (TextView)v.findViewById(R.id.biddingTitleText);
				title.setText(b.getTitle());
				String statusImgName = null;
				String biddingResultImgName = null;
				
				if (b.getReady() != null && b.getReady()) {
					statusImgName = "biddingready.png";
					biddingResultImgName = "biddingnone.png";
				}else {
					if (b.getClosed() != null && b.getClosed()){
						//경매 종료일 경우에는 경매 종료 이미지와 낙찰,유찰 여부를 체크한다.
						statusImgName = "biddingclosed.png";
						if (b.getSuccessfulBid() != null && b.getSuccessfulBid()){
							biddingResultImgName = "successfulbid.png";
						}else{
							biddingResultImgName = "notsuccessfulbid.png";
						}
						
					}else{
						//경매가 진행중일 경우에는 경매 진행 이미지와 낙찰전 이라는 이미지를 가져온다.  
						statusImgName = "biddinginprogressing.png";
						biddingResultImgName = "biddingnone.png";
					}		
				}
				
				ImageView statusImg = (ImageView)v.findViewById(R.id.biddingStatusImg);
				
				AssetInputStream statusBuf = (AssetInputStream)assetManager.open("icon/" + statusImgName);
				Bitmap statusBitmap = BitmapFactory.decodeStream(statusBuf);
				statusImg.setImageBitmap(statusBitmap);
				statusBuf.close();
				
				ImageView resultImg = (ImageView)v.findViewById(R.id.biddingResultImg);
				AssetInputStream resultBuf = (AssetInputStream)assetManager.open("icon/" + biddingResultImgName);
				Bitmap resultBitmap = BitmapFactory.decodeStream(resultBuf);
				resultImg.setImageBitmap(resultBitmap);
				resultBuf.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}	
			
			return v;
		}
	}
	
	private class BiddingDataHandler extends AsyncTask<String,String,ArrayList<BiddingSimpleInfo>>{

		@Override
		protected ArrayList<BiddingSimpleInfo> doInBackground(String... params) {
			ArrayList<BiddingSimpleInfo> biddings = new ArrayList<BiddingSimpleInfo>();
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			
			try{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/listCurrentMonthBiddings.do");
				httpPost.setEntity(entity);
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						JSONArray results = (JSONArray)jsonObject.get("results");
						for(int i=0; i < results.length(); i++){
							JSONObject biddingObj = (JSONObject)results.getJSONObject(i);
							BiddingSimpleInfo bidding = new BiddingSimpleInfo();
							bidding.setId(biddingObj.getString("id"));
							bidding.setPartnerName(biddingObj.getString("partnerName"));
							bidding.setContactName(biddingObj.getString("contactName"));
							bidding.setTitle(biddingObj.getString("title"));
							bidding.setReady(biddingObj.getBoolean("ready"));
							bidding.setClosed(biddingObj.getBoolean("closed"));
							if(!biddingObj.isNull("successfulBid")){
							
								bidding.setSuccessfulBid(biddingObj.getBoolean("successfulBid"));
							}
							biddings.add(bidding);
						}
						
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return biddings;
		}

		@Override
		protected void onPostExecute(ArrayList<BiddingSimpleInfo> results) {
			super.onPostExecute(results);
			setBiddings(results);
		}
		
	}
}

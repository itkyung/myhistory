package com.bizwave.bamstory.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.ContactInfo;
import com.bizwave.bamstory.EvalInfo;
import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.SimpleBag;

import com.bizwave.bamstory.db.DBHelper;

import com.bizwave.bamstory.util.CallUtil;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ContactDetailActivity extends Activity implements OnClickListener{
	private String contactId;
	private String contactName;
	private Context context;
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;

	private ContactInfo currentContact;
	private MyProgressDialog progressDialog;
	private static String contactPrefix = "http://www.bamstory.com/mobile/contact";
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(this.getApplicationContext());
		db = dbHelper.getWritableDatabase();
	
		
		this.context = this.getApplicationContext();
		setContentView(R.layout.contactdetail);
		
		Bundle param = getIntent().getExtras();
		this.contactId = param.getString("contactId");
		contactName = param.getString("contactName");
		String partnerName = param.getString("partnerName");
		

		
		TextView contactNameView = (TextView)findViewById(R.id.contactName);

		
		contactNameView.setText(contactName);
		
		TextView partnerNameView = (TextView)findViewById(R.id.partnerName);
		partnerNameView.setText(partnerName);		
		
		Button firstBtn = (Button)findViewById(R.id.firstButton);
		firstBtn.setOnClickListener(this);
		firstBtn.setSelected(true);
		Button secondBtn = (Button)findViewById(R.id.secondButton);
		secondBtn.setOnClickListener(this);
		Button thirdBtn = (Button)findViewById(R.id.thirdButton);
		thirdBtn.setOnClickListener(this);
		Button fourthBtn = (Button)findViewById(R.id.fourthButton);
		fourthBtn.setOnClickListener(this);
		Button fifthBtn = (Button)findViewById(R.id.fifthButton);
		fifthBtn.setOnClickListener(this);	
		
		ImageButton phoneBtn = (ImageButton)findViewById(R.id.phoneBtn);
		phoneBtn.setOnClickListener(this);
		
		ImageButton mapBtn = (ImageButton)findViewById(R.id.mapBtn);
		mapBtn.setOnClickListener(this);	
		
		Button evalBtn = (Button)findViewById(R.id.evalBtn);
		evalBtn.setOnClickListener(this);
		
		Button girlBtn = (Button)findViewById(R.id.contact_girlBtn);
		girlBtn.setOnClickListener(this);
		
		((ListView)findViewById(R.id.prices)).setAdapter(new PriceAdapter(this));
		
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		
		new PartnerDataHandler().execute("");
	}

	
	public void setContactInfo(ContactInfo contact){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		this.currentContact = contact;
		/* 업소정보 설정 */
		AssetManager assetManager = context.getAssets();
		try{
			ImageView evalImg = (ImageView)findViewById(R.id.evalImg);
			AssetInputStream evalBuf = (AssetInputStream)assetManager.open("icon/b_e" + contact.getEvalImgName());
			Bitmap eBitmap = BitmapFactory.decodeStream(evalBuf);		
			evalImg.setImageBitmap(eBitmap);
			evalBuf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		ImageView partnerImg = (ImageView)findViewById(R.id.partnerImg);
		PartnerInfo partner = contact.getPartner();
		if(partner.getImgUrl() == null || "null".equals(partner.getImgUrl())){
			partnerImg.setImageResource(R.drawable.defaultpartner);		
		}else{
			setImg(partnerImg,partner.getImgUrl());

		}
		
		TextView type = (TextView)findViewById(R.id.partnerTypeText);
		type.setText(partner.getTypeName());
		TextView pName = (TextView)findViewById(R.id.partnerNameText);
		pName.setText(partner.getName());
		TextView address = (TextView)findViewById(R.id.addressText);
		address.setText(partner.getAddress());
		TextView startTime = (TextView)findViewById(R.id.startTimeText);
		startTime.setText(partner.getStartTime());
		TextView firstTime = (TextView)findViewById(R.id.firstTimeText);
		firstTime.setText(partner.getFirstTime());
		TextView secondTime = (TextView)findViewById(R.id.secondTimeText);
		secondTime.setText(partner.getSecondTime());
			
		
		WebView pWebview = (WebView)findViewById(R.id.partnerDescriptionWebView);
		pWebview.loadUrl(contactPrefix + "/" + partner.getDescHtml());
		
		/* 상무소개 설정 */
		WebView cWebview = (WebView)findViewById(R.id.contactDescriptionWebView);
		cWebview.loadUrl(contactPrefix + "/" + contact.getDescHtml());
	
		
		
		
		/*가격정보 설정*/
		ListView priceView = (ListView)findViewById(R.id.prices);
		PriceAdapter ad = (PriceAdapter)(priceView).getAdapter();
		ad.setPrices(contact.getPrices());
		ad.notifyDataSetChanged();
		priceView.forceLayout();
		
		/*사진설정*/
		if(partner.getImgUrl1() != null){
			ImageView img1 = (ImageView)findViewById(R.id.partnerImage1);
			setImg(img1,partner.getImgUrl1());
		}
		
		if(partner.getImgUrl2() != null){
			ImageView img2 = (ImageView)findViewById(R.id.partnerImage2);
			setImg(img2,partner.getImgUrl2());
		}		
		
		
		/* 이벤트 설정 */
		if(contact.getEvents().size() != 0){
			String eDesc = "";
			for(String event : contact.getEvents()){
				eDesc = eDesc + event + "\n";
			}
			TextView eDescView = (TextView)findViewById(R.id.eventDescription);
			eDescView.setText(eDesc);		
		}
		
		if(contact.getPriceBidding() != null && contact.getPriceBidding()){
			ImageButton pb = (ImageButton)findViewById(R.id.priceBiddinBtn);
			pb.setVisibility(View.VISIBLE);
			pb.setOnClickListener(this);
			
		}
		TextView contactNameView = (TextView)findViewById(R.id.contactName);		
		contactNameView.setText(contact.getName());
		contactName = contact.getName();
		
		TextView partnerNameView = (TextView)findViewById(R.id.partnerName);
		partnerNameView.setText(contact.getPartner().getName());	
		
		Button girlBtn = (Button)findViewById(R.id.contact_girlBtn);
		if(contact.getHaveGirl()){
			girlBtn.setVisibility(View.VISIBLE);
		}
	}
	
	private void setImg(ImageView iv,String url){
		try{
			URL imageURL = new URL(Constants.HOST_NAME + url);
			HttpURLConnection conn = (HttpURLConnection)imageURL.openConnection();             
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 10240);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			iv.setImageBitmap(bm);
			bis.close();   
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	private void viewPriceBidding(){
		Bundle param = new Bundle();
		param.putString("biddingId", currentContact.getBiddingId());
		Intent intent = new Intent(this,BiddingDetailActivity.class);
		intent.putExtras(param);
		startActivity(intent);
		
	}
	
	private void viewGirl(){
		Intent i = new Intent(this,GirlActivity.class);
		i.putExtra("contactId", contactId);
		i.putExtra("contactName", contactName);
		startActivity(i);
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
	
	//서브 메뉴가 눌렸을때 호출됨 
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.phoneBtn){
			phoneCall();
			return;
		}else if(v.getId() == R.id.mapBtn){
			viewMap();
			return;
		}else if(v.getId() == R.id.evalBtn){
			viewEvalDetail();
			return;
		}else if(v.getId() == R.id.priceBiddinBtn){
			viewPriceBidding();
		}else if(v.getId() == R.id.contact_girlBtn){
			viewGirl();
		}else{
		
			Button bt1 = (Button)findViewById(R.id.firstButton);
			Button bt2 = (Button)findViewById(R.id.secondButton);
			Button bt3 = (Button)findViewById(R.id.thirdButton);
			Button bt4 = (Button)findViewById(R.id.fourthButton);
			Button bt5 = (Button)findViewById(R.id.fifthButton);
			
			View sv1 = (View)findViewById(R.id.firstScrollView);
			View sv2 = (View)findViewById(R.id.contactDescriptionWebView);
			View sv3 = (View)findViewById(R.id.thirdScrollView);
			View sv4 = (View)findViewById(R.id.fourthScrollView);
			View sv5 = (View)findViewById(R.id.fifthScrollView);
			
			switch(v.getId()){
				case R.id.firstButton :
					setVisibility(bt1,sv1,true);
					setVisibility(bt2,sv2,false);
					setVisibility(bt3,sv3,false);
					setVisibility(bt4,sv4,false);
					setVisibility(bt5,sv5,false);				
					break;
				case R.id.secondButton :
					setVisibility(bt1,sv1,false);
					setVisibility(bt2,sv2,true);
					setVisibility(bt3,sv3,false);
					setVisibility(bt4,sv4,false);
					setVisibility(bt5,sv5,false);				
					break;				
				case R.id.thirdButton :
					setVisibility(bt1,sv1,false);
					setVisibility(bt2,sv2,false);
					setVisibility(bt3,sv3,true);
					setVisibility(bt4,sv4,false);
					setVisibility(bt5,sv5,false);				
					break;				
				case R.id.fourthButton :	
					setVisibility(bt1,sv1,false);
					setVisibility(bt2,sv2,false);
					setVisibility(bt3,sv3,false);
					setVisibility(bt4,sv4,true);
					setVisibility(bt5,sv5,false);				
					break;			
				case R.id.fifthButton :
					setVisibility(bt1,sv1,false);
					setVisibility(bt2,sv2,false);
					setVisibility(bt3,sv3,false);
					setVisibility(bt4,sv4,false);
					setVisibility(bt5,sv5,true);				
					break;				
			}
		}
	}

	private void phoneCall(){
		if(this.currentContact != null){
			AlertDialog alert = new AlertDialog.Builder(this)
			.setMessage(currentContact.getName() + "에게 전화하시겠습니까?")
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					String title = "[" + currentContact.getPartner().getName() + "]" + currentContact.getName();
					CallUtil.getInstance().insertCallDb(db, currentContact.getPhone(), contactId,title, settings.getString(Constants.SESSION_KEY, null));
					
					Uri number = Uri.parse("tel:" + currentContact.getPhone());
					Intent dial = new Intent(Intent.ACTION_CALL,number);
					startActivity(dial);
				}
			})
			.setNegativeButton("NO",  new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.show();
			
		}
	}
	
	private void viewMap(){
		
		
		PartnerInfo partner = currentContact.getPartner();
		GeoPoint point = partner.getPoint();
		
		
		Bundle param = new Bundle();
		param.putBoolean("partner", true);
		param.putString("id", partner.getId());
		param.putString("name", partner.getName());
		param.putInt("latitude", point.getLatitudeE6());
		param.putInt("longitude", point.getLongitudeE6());
		param.putString("type", partner.getType());
		param.putString("typeName", partner.getTypeName());
		Intent intent = new Intent(this,MyLocationActivity.class);
		intent.putExtras(param);
		startActivity(intent);
		
	}
	
	private void viewEvalDetail(){
		if(this.currentContact == null) return;
		Bundle param = new Bundle();
		EvalInfo info = currentContact.getEvalInfo();
		param.putString("contactId", currentContact.getId());
		param.putInt("evalCount", info.getEvalCount());
		param.putString("totalEvalImgName", currentContact.getEvalImgName());
		param.putString("facilityImgName",info.getFacilityImgName());
		param.putString("priceImgName", info.getPriceImgName());
		param.putString("contactImgName", info.getContactImgName());
		param.putString("girlImgName", info.getGirlImgName());
		param.putString("reEntryImgName", info.getReVisitImgName());
		
		Intent intent = new Intent(this,ContactEvalActivity.class);
		intent.putExtras(param);
		startActivity(intent);		
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
	
	private void setVisibility(Button bt,View v,boolean visible){
		if(visible){
			bt.setSelected(true);

			v.forceLayout();
			if(v.getId() == R.id.thirdScrollView){
				ListView priceView = (ListView)findViewById(R.id.prices);
				priceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
				PriceAdapter ad = (PriceAdapter)(priceView).getAdapter();
				ad.notifyDataSetChanged();
				priceView.requestLayout();
				
			}
			v.setVisibility(View.VISIBLE);	

		}else{
			bt.setSelected(false);
			v.setVisibility(View.INVISIBLE);			
		}
	}
	
	private class PartnerDataHandler extends AsyncTask<String,String,ContactInfo>{

		@Override
		protected ContactInfo doInBackground(String... params) {
			ContactInfo contactInfo = new ContactInfo();
			try{
				
				
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
				ps.add(new BasicNameValuePair("contactId",contactId));
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/GetContactInfo.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						PartnerInfo partner = new PartnerInfo();
						partner.setId(jsonObject.getString("partnerId"));
						partner.setName(jsonObject.getString("partnerName"));
						partner.setType(jsonObject.getString("partnerTypeKey"));
						partner.setTypeName(jsonObject.getString("partnerTypeName"));
						partner.setDesc(jsonObject.getString("partnerDesc"));
						partner.setStartTime(jsonObject.getString("startTime"));
						partner.setFirstTime(jsonObject.getString("firstTime"));
						partner.setSecondTime(jsonObject.getString("secondTime"));
						partner.setAddress(jsonObject.getString("address"));
						partner.setImgUrl(jsonObject.getString("partnerImgUrl"));
						partner.setImgUrl1(jsonObject.getString("firstImgUrl"));
						partner.setImgUrl2(jsonObject.getString("secondImgUrl"));
						partner.setDescHtml(jsonObject.getString("descHtml"));
						
						Double latitude = Double.parseDouble(jsonObject.getString("latitude"));
						Double longitude = Double.parseDouble(jsonObject.getString("longitude"));
						
						
						
						GeoPoint point = new GeoPoint((int)(latitude * 1E6),(int)(longitude * 1E6));
						partner.setPoint(point);
						
						JSONObject contactObject = jsonObject.getJSONObject("contactInfo");
						
						contactInfo.setPartner(partner);
						contactInfo.setId(contactObject.getString("contactId"));
						contactInfo.setName(contactObject.getString("contactName"));
						contactInfo.setDesc(contactObject.getString("contactDesc"));
						contactInfo.setEvalImgName(contactObject.getString("evaluationImgName"));
						contactInfo.setPhone(contactObject.getString("phoneNumber"));
						contactInfo.setDescHtml(contactObject.getString("descHtml"));
						if(!contactObject.isNull("biddingId"))
							contactInfo.setBiddingId(contactObject.getString("biddingId"));
						if(!contactObject.isNull("priceBidding"))
							contactInfo.setPriceBidding(contactObject.getBoolean("priceBidding"));
						if(!contactObject.isNull("bamsCertFlag"))
							contactInfo.setBamsCertFlag(contactObject.getBoolean("bamsCertFlag"));
						contactInfo.setHaveGirl(contactObject.getBoolean("girlFlag"));
						
						
						contactInfo.setPrices(new ArrayList<SimpleBag>());
						if(contactObject.has("prices")){
							JSONArray pricesObjs = (JSONArray)contactObject.get("prices");
							JSONObject priceObj = null;
							for(int i=0; i < pricesObjs.length(); i++){
								priceObj = (JSONObject)pricesObjs.getJSONObject(i);
								SimpleBag price = new SimpleBag();
								price.setKey(priceObj.getString("name"));
								price.setValue(priceObj.getString("price"));
								contactInfo.getPrices().add(price);
							}
						}
						EvalInfo eval = new EvalInfo();
						if(contactObject.has("evals")){
							JSONArray evalObjs = (JSONArray)contactObject.get("evals");
							JSONObject evalObj = null;
							for(int i=0; i < evalObjs.length(); i++){
								evalObj = (JSONObject)evalObjs.getJSONObject(i);
								String type = evalObj.getString("typeName");
								if(type.equals("FACILITIES")){
									eval.setFacilityImgName(evalObj.getString("evalImgName"));
								}else if(type.equals("PRICE")){
									eval.setPriceImgName(evalObj.getString("evalImgName"));
								}else if(type.equals("CONTACT")){
									eval.setContactImgName(evalObj.getString("evalImgName"));
								}else if(type.equals("GIRL")){
									eval.setGirlImgName(evalObj.getString("evalImgName"));
								}else if(type.equals("REVISIT")){
									eval.setReVisitImgName(evalObj.getString("evalImgName"));
								}
							}
							eval.setEvalCount(contactObject.getInt("evalCount"));
						}else{
							String defaultImg = "0.png";
							eval.setFacilityImgName(defaultImg);
							eval.setPriceImgName(defaultImg);
							eval.setGirlImgName(defaultImg);
							eval.setReVisitImgName(defaultImg);
							eval.setContactImgName(defaultImg);
							eval.setEvalCount(0);
						}
						contactInfo.setEvalInfo(eval);
						
						contactInfo.setEvents(new ArrayList<String>());
						if(contactObject.has("events")){
							JSONArray eventObjs = (JSONArray)contactObject.get("events");
							String eventObj = null;
							for(int i=0; i < eventObjs.length(); i++){
								eventObj = eventObjs.getString(i);
								contactInfo.getEvents().add(eventObj);
							}
						}
						
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return contactInfo;
		}

		@Override
		protected void onPostExecute(ContactInfo result) {
			super.onPostExecute(result);
			setContactInfo(result);
		}

	}	
	
	private class PriceAdapter extends BaseAdapter{
		private Context pContext;
		private ArrayList<SimpleBag> prices;
		private LayoutInflater mInflater;
		
		public PriceAdapter(Context context){
			this.pContext = context;
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setPrices(ArrayList<SimpleBag> prices){
			this.prices = prices;
		}
		
		@Override
		public int getCount() {
			if(this.prices != null){
				return this.prices.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if(this.prices != null){
				return this.prices.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v=null;
			try{
				if(convertView == null){
					v = LayoutInflater.from(pContext).inflate(
							com.bizwave.bamstory.R.layout.priceitem,parent,false);
				}else{
					v = convertView;
				}
				SimpleBag b = this.prices.get(position);
				
				TextView nick = (TextView)v.findViewById(R.id.priceNo);
				nick.setTextColor(getResources().getColor(android.R.color.white));
				nick.setText(b.getKey());

				TextView price = (TextView)v.findViewById(R.id.priceValue);
				price.setTextColor(getResources().getColor(android.R.color.white));
				price.setText(b.getValue());
				
			}catch(Exception e){
				e.printStackTrace();
			}		
			return v;
		}
		
	}
	
}

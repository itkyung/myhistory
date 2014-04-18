package com.bizwave.bamstory.activity;


import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.util.CallUtil;
import com.google.android.maps.GeoPoint;

public abstract class BasicActivity extends Activity {
	protected String deviceId;
	protected static final int PROGRESS1 = 1;
	protected static final int PROGRESS2 = 2;
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	private SimpleDateFormat fm = new SimpleDateFormat("dd'일' HH:mm");
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = manager.getDeviceId();
		dbHelper = new DBHelper(this.getApplicationContext());
		db = dbHelper.getWritableDatabase();
	}
	
	public void viewMap(String name,String latitude,String longitude,String id){
		BamStoryTabActivity tab = (BamStoryTabActivity)this.getParent();
		double dLatitude = Double.parseDouble(latitude);
		double dLongitude = Double.parseDouble(longitude);
		GeoPoint point = new GeoPoint((int)(dLatitude * 1E6),(int)(dLongitude * 1E6));
		PartnerInfo partner = new PartnerInfo();
		partner.setId(id);
		partner.setName(name);
		partner.setPoint(point);
		tab.viewInMap(partner);
	}
	
	public void callPhone(String phone,String contactId,String title){
		CallUtil.getInstance().insertCallDb(db, phone, contactId, title, deviceId);
		
		Uri number = Uri.parse("tel:" + phone);
		Intent dial = new Intent(Intent.ACTION_CALL,number);
		startActivity(dial);
	}
	
	/*
	private void insertCallDb(String phone,String contactId,String title){
		//전화 이력이 50개를 넘을시에는 제일 오래된(id가 제일 작은) Row를 삭제하고 insert한다.
		Cursor c = db.rawQuery(CallHistoryDatabaseHelper.COUNT_QUERY, null);
		c.moveToFirst();
		long count = c.getLong(0);
		c.close();
		if(count > CallHistoryDatabaseHelper.historyLimit){
			Cursor c1 = db.rawQuery(CallHistoryDatabaseHelper.MIN_QUERY, null);
			c.moveToFirst();
			long minId = c.getLong(0);
			db.delete(CallHistoryDatabaseHelper.TABLE_NAME, "id = ?", new String[]{"" + minId});
		}
		
		//DB에 이력을 남긴다.
		ContentValues params = new ContentValues();
		params.put("contactid",contactId);
		params.put("title",title);
		params.put("phone",phone);
		params.put("calldate",fm.format(new Date()));
		db.insert(CallHistoryDatabaseHelper.TABLE_NAME, null, params);
		
	}
	
	private class CallLogHandler extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			String contactId = params[0];
			String url = Constants.HOST_NAME + "/bamStory/mobile/bam/callPhone.do?contactId=" + contactId + "&deviceId=" + deviceId;
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			try{
				HttpParams hParams = httpclient.getParams();
				HttpConnectionParams.setConnectionTimeout(hParams, 5000);
				HttpConnectionParams.setSoTimeout(hParams, 5000);
				
				HttpGet hGet = new HttpGet(url);
				response = httpclient.execute(hGet);
				return null;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				httpclient.getConnectionManager().shutdown();
			}			
			
			return null;
		}
		
	}
	
	*/
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(PROGRESS1){
		case PROGRESS1 :
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("로딩중입니다...");
			return dialog;
		case PROGRESS2 : 
			ProgressDialog dialog2 = new ProgressDialog(this);
			dialog2.setMessage("로딩중입니다...");
			return dialog2;
			
		default : return null;
		}
		
	}

	abstract public void showProgress();
	abstract public void stopProgress();
}

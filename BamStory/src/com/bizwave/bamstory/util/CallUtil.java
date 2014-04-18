package com.bizwave.bamstory.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.db.DBHelper;


public class CallUtil {
	private static SimpleDateFormat fm = new SimpleDateFormat("dd'일' HH:mm");
	private static CallUtil instance;
	private String sessionKey;
	
	
	public static CallUtil getInstance(){
		if(instance == null){
			instance = new CallUtil();
		}
		return instance;
	}
	
	public void insertCallDb(SQLiteDatabase db,String phone,String contactId,String title, String sessionKey){
		this.sessionKey = sessionKey;
		new CallLogHandler().execute(contactId);
		
		
		//전화 이력이 50개를 넘을시에는 제일 오래된(id가 제일 작은) Row를 삭제하고 insert한다.
		Cursor c = db.rawQuery(DBHelper.COUNT_QUERY, null);
		c.moveToFirst();
		long count = c.getLong(0);
		c.close();
		if(count > DBHelper.historyLimit){
			Cursor c1 = db.rawQuery(DBHelper.MIN_QUERY, null);
			c.moveToFirst();
			long minId = c.getLong(0);
			db.delete(DBHelper.TABLE_NAME, "_id = ?", new String[]{"" + minId});
		}
		
		//DB에 이력을 남긴다.
		ContentValues params = new ContentValues();
		params.put("contactid",contactId);
		params.put("title",title);
		params.put("phone",phone);
		params.put("calldate",fm.format(new Date()));
		db.insert(DBHelper.TABLE_NAME, null, params);
		
	}
	
	private class CallLogHandler extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			String contactId = params[0];
			String url = Constants.HOST_NAME + "/bamStory/mobile/bam/callPhoneBySession.do?contactId=" + contactId + "&sessionKey=" + sessionKey;
			
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
	
}

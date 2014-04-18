package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.util.MyProgressDialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ParticipateBiddingActivity extends Activity implements OnTouchListener{
	private String biddingId;
	
	private Integer highPrice;
	private SharedPreferences settings;
	private MyProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.participatebidding);
			
				
		Bundle param = getIntent().getExtras();
		this.biddingId = param.getString("biddingId");
		String partnerName = param.getString("partnerName");
		String contactName  = param.getString("contactName");
		highPrice = param.getInt("highPrice");
		
		TextView title = (TextView)findViewById(R.id.bdpTitleBar);
		title.setText("[" + partnerName + "]" + contactName);
		
		TextView highPriceText = (TextView)findViewById(R.id.bdHighPriceText);
		highPriceText.setText(highPrice + "만원");
		
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		String nickName = settings.getString(Constants.NICK_NAME, "");
		
		EditText nickNameText = (EditText)findViewById(R.id.bdNickNameText);
		nickNameText.setText(nickName);
		
		ImageButton pBtn = (ImageButton)findViewById(R.id.bdParticipateBtn);
		pBtn.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.getId() != R.id.bdParticipateBtn) return false;
		EditText nickNameText = (EditText)findViewById(R.id.bdNickNameText);
		String nickName = nickNameText.getText().toString();
		if(nickName == null || nickName.equals("")){
			buildAlertMessage("닉네임은 필수입니다.",false);
			return true;
		}
		SharedPreferences.Editor nickNameEditor = settings.edit();
		nickNameEditor.putString(Constants.NICK_NAME, nickName);
		nickNameEditor.commit();
		
		EditText bidPriceText = (EditText)findViewById(R.id.bdBidPrice);
		String bidPrice = bidPriceText.getText().toString();
		if(bidPrice == null || bidPrice.equals("") ){
			buildAlertMessage("입찰가는 필수입니다.",false);
			return true;			
		}
		if(highPrice.intValue() >= Integer.valueOf(bidPrice).intValue()){
			buildAlertMessage("입찰가는 최고가보다 커야합니다.",false);
			return true;		
		}
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new BiddingSubmitHander().execute(nickName,bidPrice);
		return true;
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
	
	
	private class BiddingSubmitHander extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			String nick = params[0];
			String bidPriceStr = params[1];
			
			String responseText;
		
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("id",biddingId));
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("nickName",nick));
			ps.add(new BasicNameValuePair("bidPrice",bidPriceStr));
	
			
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/biddingBySession.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				responseText = httpClient.execute(httpPost, new BasicResponseHandler());
			}catch(Exception e){
				e.printStackTrace();
				responseText = null;
			}
			return responseText;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog != null){
				progressDialog.dismiss();
			}
			if(result == null){
				buildAlertMessage("네트워크 장애가 있습니다. 추후에 다시 시도하세요",false);
			}else{
				buildAlertMessage("성공적으로 입찰되었습니다. 잠시후에 확인하세요.",true);
			}
		}
				
		
	}
	
	
}

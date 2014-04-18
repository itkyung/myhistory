package com.bizwave.bamstory.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.ContactInfo;
import com.bizwave.bamstory.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class ContactEvalActivity extends Activity implements OnClickListener{
	private String contactId;
	private boolean editingStatus = false;
	private int facilityValue;
	private int priceValue;
	private int contactValue;
	private int girlValue;
	private int reVisitValue;
	private String deviceId;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		setContentView(R.layout.evalview);
		Context context = this;
		Bundle param = getIntent().getExtras();
		contactId = param.getString("contactId");
		
		TextView countView = (TextView)findViewById(R.id.evalCountText);
		countView.setText("" + param.getInt("evalCount") + "명 참여");
		
		AssetManager assetManager = context.getAssets();

		setImg(assetManager,(ImageView)findViewById(R.id.totalEvalImg),param.getString("totalEvalImgName"));
		setImg(assetManager,(ImageView)findViewById(R.id.facilityEvalImg),param.getString("facilityImgName"));
		setImg(assetManager,(ImageView)findViewById(R.id.contactEvalImg),param.getString("contactImgName"));
		setImg(assetManager,(ImageView)findViewById(R.id.girlEvalImg),param.getString("girlImgName"));
		setImg(assetManager,(ImageView)findViewById(R.id.priceEvalImg),param.getString("priceImgName"));
		setImg(assetManager,(ImageView)findViewById(R.id.reEntryEvalImg),param.getString("reEntryImgName"));
		
		Button evalBtn = (Button)findViewById(R.id.evalSubmitBtn);
		evalBtn.setOnClickListener(this);

		new SeekbarListener(R.id.facilitySeekBar,R.id.facilityEvalText);
		new SeekbarListener(R.id.priceSeekBar,R.id.priceEvalText);
		new SeekbarListener(R.id.contactSeekBar,R.id.contactEvalText);
		new SeekbarListener(R.id.girlSeekBar,R.id.girlEvalText);
		new SeekbarListener(R.id.reEntrySeekBar,R.id.reEntryEvalText);
		
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = manager.getDeviceId();		
		
	}
	
	private void setImg(AssetManager assetManager,ImageView view,String imgName){
		
		try{
			AssetInputStream fBuf = (AssetInputStream)assetManager.open("icon/b_e" + imgName);
			Bitmap fBitmap = BitmapFactory.decodeStream(fBuf);		
			view.setImageBitmap(fBitmap);
			fBuf.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void onClick(View arg0) {
		Button bt = (Button)findViewById(R.id.evalSubmitBtn);
		changeMode(R.id.facilityEvalImg,R.id.facilitySeekBar,R.id.facilityEvalText);
		changeMode(R.id.priceEvalImg,R.id.priceSeekBar,R.id.priceEvalText);
		changeMode(R.id.contactEvalImg,R.id.contactSeekBar,R.id.contactEvalText);
		changeMode(R.id.girlEvalImg,R.id.girlSeekBar,R.id.girlEvalText);
		changeMode(R.id.reEntryEvalImg,R.id.reEntrySeekBar,R.id.reEntryEvalText);
		if(editingStatus){
			//현재 수정모드이면 서버에 submit을 한다.
			new EvaluationDataHandler().execute("");
			bt.setText("평점주기");
			editingStatus=false;			
		}else{
			//현재 ViewMode이면 수정모드로 전환시킨다. 
			bt.setText("전송하기");
			editingStatus=true;
		}
		
	}

	private void changeMode(int ivId,int barId,int textId){
		ImageView view = (ImageView)findViewById(ivId);
		SeekBar bar = (SeekBar)findViewById(barId);
		TextView tv = (TextView)findViewById(textId);
		
		if(editingStatus){
			view.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			tv.setVisibility(View.GONE);
		}else{
			view.setVisibility(View.GONE);
			bar.setVisibility(View.VISIBLE);	
			tv.setVisibility(View.VISIBLE);
			
		}
	}
	
	public void setValue(int barId,int value){
		switch(barId){
		case R.id.facilitySeekBar:
			this.facilityValue = value;
			break;
		case R.id.priceSeekBar:
			this.priceValue = value;
			break;
		case R.id.contactSeekBar:
			this.contactValue = value;
			break;
		case R.id.girlSeekBar:
			this.girlValue = value;
			break;
		case R.id.reEntrySeekBar:
			this.reVisitValue = value;
			break;
			
		}
	}
	
	
	private void buildAlertMessage(String msg) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(msg)
	           .setCancelable(false)
	           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   finish();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	
	private class SeekbarListener implements SeekBar.OnSeekBarChangeListener{
		private int barId;
		private TextView v;
		private int value;
		
		public SeekbarListener(int barId,int textViewId){
			this.barId = barId;
			this.v  = (TextView)findViewById(textViewId);
			SeekBar fb = (SeekBar)findViewById(barId);
			fb.setOnSeekBarChangeListener(this);
		}
		
		
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			v.setText("" + progress);
			value = progress;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			setValue(barId,value);
			
		}
		
	}
	
	
	private class EvaluationDataHandler extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {

			String responseText;
		
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("contactId",contactId));
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			
			ps.add(new BasicNameValuePair("fPoint",""+facilityValue));
			ps.add(new BasicNameValuePair("pPoint",""+priceValue));
			ps.add(new BasicNameValuePair("cPoint",""+contactValue));
			ps.add(new BasicNameValuePair("gPoint",""+girlValue));
			ps.add(new BasicNameValuePair("rPoint",""+reVisitValue));
			
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/updateEvaluationBySession.do");
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
			
			if(result == null){
				buildAlertMessage("네트워크 장애가 있습니다. 추후에 다시 시도하세요");
			}else{
				buildAlertMessage("성공적으로 전송되었습니다. 결과는 잠시후에 확인가능합니다.");
			}
		}
		

		
		
	}
}

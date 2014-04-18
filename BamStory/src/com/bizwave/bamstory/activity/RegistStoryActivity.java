package com.bizwave.bamstory.activity;


import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RegistStoryActivity extends Activity implements OnClickListener,OnEditorActionListener{
	private String originalStoryId;
	private EditText contents;
	private String nickName;
	private MyProgressDialog progressDialog;
	private SharedPreferences settings;
	private String targetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		
		Bundle param = getIntent().getExtras();
		if(param.containsKey("originalStoryId")){
			originalStoryId = param.getString("originalStoryId");
		}
		if(param.containsKey("targetId")){
			targetId = param.getString("targetId");
		}
		nickName = param.getString("nickName");
		setContentView(R.layout.registstoryview);
		
		Button cancelBtn = (Button)findViewById(R.id.newStoryCancleBtn);
		cancelBtn.setOnClickListener(this);
		
		Button submitBtn = (Button)findViewById(R.id.newStorySubmitBtn);
		submitBtn.setOnClickListener(this);
		
		contents = (EditText)findViewById(R.id.newstorytext);
		contents.setOnEditorActionListener(this);
		
		contents.requestFocus();
		//InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.showSoftInput(contents, InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.newStoryCancleBtn){
			cancel();
		}else{
			submit();
		}
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(v.getId() == R.id.newstorytext){
			if(actionId == KeyEvent.ACTION_DOWN){
				String val = v.getText().toString();
				if(val != null){
					String[] lines = val.split("\n");
					if(lines.length > 3){
						buildAlertMessage("엔터키는 최대 3번입력가능합니다.",false);
						return true;
					}
				}
				
			}
		}
		return false;
	}

	public void cancel(){
		finish();
	}
	
	public void submit(){
		String contentsStr = contents.getText().toString();
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new StorySubmitHandler().execute(contentsStr,originalStoryId,targetId);
		
	}
	
	
	private void buildAlertMessage(String msg,final boolean finish) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(msg)
	           .setCancelable(false)
	           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   if(finish){
	            		   setResult(RESULT_OK,getIntent());
	            		   finish();
	            	   }
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	
	private class StorySubmitHandler extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			String content = params[0];
			String originalId=null;
			if(params.length > 1){
				originalId = params[1];
				if(params.length == 3){
					targetId = params[2];
				}
			}
			String responseText;
		
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("contents",content));
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("nickName",nickName));
			
			if(originalId != null)
				ps.add(new BasicNameValuePair("originalStoryId",originalId));
			if(targetId != null)
				ps.add(new BasicNameValuePair("targetId",targetId));
				
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				String url = null;
				if(targetId != null)
					url = "/bamStory/mobile/bam/saveStoryForTarget.do";
				else
					url = "/bamStory/mobile/bam/saveStoryBySession.do";
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + url);
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						boolean success = jsonObject.getBoolean("success");
						if(success){
							return null;
						}else{
							String message = jsonObject.getString("message");
							responseText = message;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					responseText = e.getMessage();
				}
			}catch(Exception e){
				e.printStackTrace();
				responseText = e.getMessage();
			}
			return responseText;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
			if(result != null){
				buildAlertMessage(result,false);
			}else{
				buildAlertMessage("글쓰기가 성공했습니다.",true);
			}
		}
				
		
	}
	
		
}

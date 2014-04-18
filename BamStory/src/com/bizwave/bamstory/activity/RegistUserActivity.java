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
import com.bizwave.bamstory.util.Base64;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.SHA1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegistUserActivity extends Activity implements OnClickListener{
	private String name;
	private String socialNo;
	private String loginId;
	private String password;
	private String nickName;
	private MyProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.registuser);
		
		WebView pWebview = (WebView)findViewById(R.id.regist_terms);
		pWebview.setVerticalScrollBarEnabled(true);
		pWebview.loadUrl(Constants.HOST_NAME + "/bamStory/mobile/bam/terms.html");
		
		Button registBtn = (Button)findViewById(R.id.regist_saveBtn);
		registBtn.setOnClickListener(this);
		
		Button cancelBtn  = (Button)findViewById(R.id.regist_cancelBtn);
		cancelBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.regist_saveBtn)
			submit();
		else
			cancel();
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
	
	private void submit(){
		if(validate()){
			progressDialog = MyProgressDialog.show(this,"","",true,true,null);
			new RegistUserDataHandler().execute("");
		}
	}
	
	private boolean isEmpty(String value){
		return value == null || "".equals(value) ? true : false;
	}
	private boolean validate(){
		EditText nameEdit = (EditText)findViewById(R.id.regist_name);
		name = nameEdit.getText().toString();
		if(isEmpty(name)){
			buildAlertMessage("이름은 필수입니다.",false);
			return false;
		}
		EditText social1Edit = (EditText)findViewById(R.id.regist_social1);
		if(isEmpty(social1Edit.getText().toString())){
			buildAlertMessage("주민번호는 필수입니다.",false);
			return false;
		}
		EditText social2Edit = (EditText)findViewById(R.id.regist_social2);
		if(isEmpty(social2Edit.getText().toString())){
			buildAlertMessage("주민번호는 필수입니다.",false);
			return false;
		}		
		socialNo = social1Edit.getText().toString() + social2Edit.getText().toString();
		
		EditText loginIdEdit = (EditText)findViewById(R.id.regist_id);
		loginId = loginIdEdit.getText().toString();
		if(isEmpty(loginId)){
			buildAlertMessage("아이디는 필수입니다.",false);
			return false;
		}		
		
		EditText passwordEdit = (EditText)findViewById(R.id.regist_password);
		password = passwordEdit.getText().toString();
		if(isEmpty(password)){
			buildAlertMessage("비밀번호는 필수입니다.",false);
			return false;		
		}
		
		if(password.length() < 4 || password.length() > 8){
			buildAlertMessage("비밀번호는 자릿수를 확인하세요.",false);
			return false;				
		}
		
		EditText confirmPasswordEdit = (EditText)findViewById(R.id.regist_confirmPassword);
		String confirmPassword = confirmPasswordEdit.getText().toString();		
		if(!password.equals(confirmPassword)){
			buildAlertMessage("비밀번호가 서로 다릅니다.",false);
			return false;				
		}
		
		EditText nickNameEdit = (EditText)findViewById(R.id.regist_nickname);
		nickName = nickNameEdit.getText().toString();
		if(isEmpty(nickName)){
			buildAlertMessage("닉네임은 필수입니다.",false);
			return false;		
		}
		
		CheckBox termCheck = (CheckBox)findViewById(R.id.regist_termsAgree);
		if(!termCheck.isChecked()){
			buildAlertMessage("개인정보 보호 약관에 동의하세요.",false);
			return false;				
		}
		return true;
	}
	
	private void cancel(){
		finish();
	}
	
	private class RegistUserDataHandler extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			try{
				TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String deviceId = manager.getDeviceId();		
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				ps.add(new BasicNameValuePair("name", name));
				ps.add(new BasicNameValuePair("loginId", loginId));
				ps.add(new BasicNameValuePair("passwordHash", SHA1.digest(password)));
				ps.add(new BasicNameValuePair("nickName", nickName));
				ps.add(new BasicNameValuePair("socialNumber",Base64.encodeBytes(socialNo.getBytes())));
				ps.add(new BasicNameValuePair("deviceId", deviceId));
				
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/regisitUser.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);
	
					if(jsonObject != null) {
						if(jsonObject.getBoolean("success")) {
							return null;
						}else{
							String msg = jsonObject.getString("message");
							return msg;
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
					return exception.getMessage();
				}
				finally {}
	
			}catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			}			
			
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog != null)
				progressDialog.dismiss();			
			if(result == null){
				buildAlertMessage("성공적으로 등록되었습니다.로그인하세요.",true);
			}else{
				buildAlertMessage(result, false);
			}

		}
		
		
		
		
	}
}

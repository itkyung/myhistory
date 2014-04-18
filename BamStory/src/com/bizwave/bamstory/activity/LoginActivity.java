package com.bizwave.bamstory.activity;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.db.Category;
import com.bizwave.bamstory.db.CategoryContactMapping;
import com.bizwave.bamstory.db.DBHelper;
import com.bizwave.bamstory.db.Event;
import com.bizwave.bamstory.db.EventContactMapping;
import com.bizwave.bamstory.db.Partner;
import com.bizwave.bamstory.db.RevisionInfo;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.SHA1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class LoginActivity extends Activity implements OnClickListener{
	private Context context;
	private MyProgressDialog progressDialog;
	private SharedPreferences settings;
	private static final int DISPLAY_INTRO = 1;
	private static final int REGIST_MEMBER = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.login);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		boolean autoLogin = settings.getBoolean(Constants.AUTO_LOGIN, false);
		
		String loginId = settings.getString(Constants.LOGIN_ID_KEY, null);
		final EditText loginIdField = (EditText)findViewById(R.id.login_id);
		if(loginId != null)
			loginIdField.setText(loginId);
		final CheckBox autoLoginCheckbox = (CheckBox)findViewById(R.id.login_autologin);
		boolean displayIntro = settings.getBoolean(Constants.DISPLAY_NOTICE, true);
		final Button loginBtn = (Button)findViewById(R.id.login_loginBtn);
		loginBtn.setOnClickListener(this);
		final Button registBtn = (Button)findViewById(R.id.login_registBtn);
		registBtn.setOnClickListener(this);
		
		if(autoLogin){
			autoLoginCheckbox.setChecked(true);
		}
		if(displayIntro){
			Intent i = new Intent(this,IntroNoticeActivity.class);
			startActivityForResult(i, DISPLAY_INTRO);
		}else{
			if(autoLogin){
				this.loginAction(true);
			}else{
				if(loginId == null){
					loginIdField.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(loginIdField, InputMethodManager.SHOW_IMPLICIT);
				}else{
					final EditText passwordField = (EditText)findViewById(R.id.login_password);
					passwordField.requestFocus();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == DISPLAY_INTRO){
			SharedPreferences.Editor profileEditor = settings.edit();
			profileEditor.putBoolean(Constants.DISPLAY_NOTICE, false);
			profileEditor.commit();
			boolean autoLogin = settings.getBoolean(Constants.AUTO_LOGIN, false);
			if(autoLogin)
				this.loginAction(true);
		}else{
			
		}
	}

	private void loginAction(boolean autoLogin){
		String loginId;
		String password;
		
		if(autoLogin){
			loginId = settings.getString(Constants.LOGIN_ID_KEY, null);
			password = settings.getString(Constants.PASSWORD_KEY, null);
			
		}else{
			final EditText l = (EditText)findViewById(R.id.login_id);
			loginId = l.getText().toString();
			final EditText p = (EditText)findViewById(R.id.login_password);
			password = p.getText().toString();
			
		}
		
		if(loginId == null || "".equals(loginId)){
			buildAlertMessage("아이디는 필수입니다.",false);
			return;
		}
		if(password == null || "".equals(password)){
			buildAlertMessage("비밀번호는 필수입니다.",false);
			return;
		}
		progressDialog = MyProgressDialog.show(this,"","",true,true,null);
		new LoginAction().execute(loginId,password);
	}
	
	private void registMember(){
		Intent intent = new Intent(this,RegistUserActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.login_loginBtn){
			loginAction(false);
		}else{
			registMember();
		}
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
	
	public void loginSuccess(String sessionKey){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		final EditText l = (EditText)findViewById(R.id.login_id);
		String loginId = l.getText().toString();
		final EditText p = (EditText)findViewById(R.id.login_password);
		String password = p.getText().toString();
		final CheckBox autoLoginCheckbox = (CheckBox)findViewById(R.id.login_autologin);
		
		
		SharedPreferences.Editor profileEditor = settings.edit();
		profileEditor.putString(Constants.LOGIN_ID_KEY, loginId);
		profileEditor.putString(Constants.PASSWORD_KEY, password);
		profileEditor.putBoolean(Constants.AUTO_LOGIN, autoLoginCheckbox.isChecked());
		
		profileEditor.commit();
		
		new CheckRevision().execute(sessionKey);
		startActivity(new Intent(this,MainPageActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_left, R.anim.hold);
	}
	
	public void loginFailure(String msg){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		buildAlertMessage(msg,false);
	}
	
	private class LoginReturn{
		boolean success;
		String msg;
		String sessionKey;
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getSessionKey() {
			return sessionKey;
		}
		public void setSessionKey(String sessionKey) {
			this.sessionKey = sessionKey;
		}
	}
	
	private class LoginAction extends AsyncTask<String,String,LoginReturn>{		
		@Override
		protected LoginReturn doInBackground(String... params) {
			try{
				String loginId = params[0];
				String password = params[1];
				
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();

				ps.add(new BasicNameValuePair("loginId", loginId));
				ps.add(new BasicNameValuePair("passwordHash", SHA1.digest(password)));
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/loginAction.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						if(jsonObject.getBoolean("success")) {
							JSONObject data = jsonObject.getJSONObject("data");
							String sessionKey =	data.getString("sessionKey");
							String nickName = data.getString("nickName");
							String memberType = data.getString("memberType");
							String profileImage = data.getString("profileImage");
							SharedPreferences.Editor profileEditor = settings.edit();
							profileEditor.putString(Constants.SESSION_KEY, sessionKey);
							profileEditor.putString(Constants.NICK_NAME, nickName);
							profileEditor.putString(Constants.MEMBER_TYPE, memberType);
							profileEditor.putString(Constants.PROFILE_IMAGE, profileImage);
							profileEditor.commit();
							LoginReturn returnV = new LoginReturn();
							returnV.setSuccess(true);
							returnV.setSessionKey(sessionKey);
							return returnV;
						}else{
							String msg = jsonObject.getString("message");
							LoginReturn returnV = new LoginReturn();
							returnV.setSuccess(false);
							returnV.setMsg(msg);
							return returnV;
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
					LoginReturn returnV = new LoginReturn();
					returnV.setSuccess(false);
					returnV.setMsg(exception.getMessage());
					return returnV;
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
				LoginReturn returnV = new LoginReturn();
				returnV.setSuccess(false);
				returnV.setMsg(e.getMessage());
				return returnV;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(LoginReturn returnValue) {
			super.onPostExecute(returnValue);
			if(returnValue != null && returnValue.isSuccess()){
				loginSuccess(returnValue.getSessionKey());
			}else{
				loginFailure(returnValue.getMsg());
			}
		}
	}
	
	private class CheckRevision extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			String sessionKey = params[0];
			try{
				DBHelper dbHelper = new DBHelper(context);
				ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
				RevisionInfo revisionInfo = dbHelper.getRevisionInfo();
				ps.add(new BasicNameValuePair("revision", revisionInfo.getcRevision()));
				ps.add(new BasicNameValuePair("pRevision", revisionInfo.getpRevisiion()));
				ps.add(new BasicNameValuePair("phoneType", "ANDROID"));
				ps.add(new BasicNameValuePair("sessionKey",sessionKey));
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/checkRevision.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						if(jsonObject.getBoolean("changed")) {
							dbHelper.deleteCategory();
							JSONArray categories = (JSONArray)jsonObject.get("categories");
							ArrayList<Category> categoryArray = new ArrayList<Category>();
							toJavaObjectArray(categoryArray, categories);

							dbHelper.createCategories(categoryArray);
							
							dbHelper.updateCRevisionInfo(jsonObject.getString("revision"));
						}
						
						if(jsonObject.getBoolean("pChanged")) {
							dbHelper.deleteCategoryContactMappings();
							dbHelper.deletePartner();
							JSONArray categoryContactMappings = (JSONArray)jsonObject.get("categoryContacts");
							JSONObject categoryContactMapping = null;
							ArrayList<CategoryContactMapping> changedMappings = new ArrayList<CategoryContactMapping>();
							for(int i=0; i<categoryContactMappings.length(); i++) {
								categoryContactMapping = categoryContactMappings.getJSONObject(i);
								changedMappings.add(new CategoryContactMapping(
										categoryContactMapping.getString("categoryId"), 
										categoryContactMapping.getString("contactId")));
							}
							dbHelper.createCategoryContactMapppings(changedMappings);
							JSONArray partners = (JSONArray)jsonObject.get("partners");
							JSONObject partnerObject = null;
							ArrayList<Partner> partnerList = new ArrayList<Partner>();
							Partner partner = null;
							for(int i=0; i<partners.length(); i++) {
								partnerObject = partners.getJSONObject(i);
								int bamsFlag = 0;
								if(!partnerObject.isNull("bamsCertFlag")){
									bamsFlag = partnerObject.getBoolean("bamsCertFlag") ? 1 : 0;
								}
								int haveGirl = 0;
								if(!partnerObject.isNull("girlFlag")){
									haveGirl = partnerObject.getBoolean("girlFlag") ? 1 : 0;
								}
								partner = new Partner(
										partnerObject.getString("contactId"),//cotactId를 id로 이용한다.
										partnerObject.getString("partnerName"), 
										partnerObject.getInt("idx"), 
										partnerObject.getString("contactId"), 
										partnerObject.getString("partnerTypeName"), 
										partnerObject.getString("evaluationImageName"), 
										partnerObject.getString("contactName"), 
										partnerObject.getString("partnerId"), 
										partnerObject.getString("partnerType"), 
										partnerObject.getInt("evaluation"),
										bamsFlag,haveGirl,
										partnerObject.getInt("evalCount"));
								partnerList.add(partner);
							}
							dbHelper.createPartners(partnerList);
							
							//이벤트 데이타를 다시 넣는다. 
							dbHelper.deleteEventContactMappings();
							dbHelper.deleteEvent();
							JSONArray events = (JSONArray)jsonObject.get("events");
							JSONObject eventObject = null;
							ArrayList<Event> eventList = new ArrayList<Event>();
							
							for(int i=0; i<events.length(); i++) {
								Event event = new Event();
								eventObject = events.getJSONObject(i);
								event.setId(eventObject.getString("id"));
								event.setName(eventObject.getString("text"));	
								eventList.add(event);
							}
							dbHelper.createEvents(eventList);							
							
							JSONArray eventContactMappings = (JSONArray)jsonObject.get("eventContacts");
							JSONObject eventContactObject = null;
							ArrayList<EventContactMapping> mappingList = new ArrayList<EventContactMapping>();
							
							for(int i=0; i<eventContactMappings.length(); i++){
								eventContactObject = eventContactMappings.getJSONObject(i);
								EventContactMapping mapping = new EventContactMapping();
								mapping.setContactId(eventContactObject.getString("contactId"));
								mapping.setEventId(eventContactObject.getString("eventId"));
								mappingList.add(mapping);
							}
							dbHelper.createEventContactMappings(mappingList);
							
							dbHelper.updatePRevisionInfo(jsonObject.getString("pRevision"));
						}
					}
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}

			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		private void toJavaObjectArray(ArrayList<Category> categoryList, JSONArray categories) {
			JSONObject categoryObject = null;
			Category category = null;
			for(int i=0; i<categories.length(); i++) {
				try {
					categoryObject = categories.getJSONObject(i);
					category = new Category(
							categoryObject.getString("id"), 
							categoryObject.getString("text"), 
							categoryObject.getString("type"), 
							categoryObject.getString("parentId"), 
							categoryObject.getBoolean("partner"));
				} catch (JSONException jsonException) {
					jsonException.printStackTrace();
				}
				categoryList.add(category);
				JSONArray children = null;
				try {
					children = (JSONArray)categoryObject.get("children");
					if(children != null && children.length() > 0) {
						toJavaObjectArray(categoryList, children);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
}

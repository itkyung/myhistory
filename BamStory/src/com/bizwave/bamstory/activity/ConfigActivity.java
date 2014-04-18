package com.bizwave.bamstory.activity;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.StoryInfo;
import com.bizwave.bamstory.util.ImageThreadLoader;
import com.bizwave.bamstory.util.MyProgressDialog;
import com.bizwave.bamstory.util.ImageThreadLoader.ImageLoadedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ConfigActivity extends Activity implements OnClickListener,OnEditorActionListener{
	private SharedPreferences settings;
	private String profileImageName;
	private String nickName;
	private MyProgressDialog progressDialog;
	private Context context;
	private NickNameCheckHandler checkHandler;
	private final static int PICK_IMAGE = 100;
	private final static int TAKE_PICTURE = 200;
	private ImageThreadLoader imageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		imageLoader = new ImageThreadLoader();
		
		setContentView(R.layout.config);
		
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		String loginId = settings.getString(Constants.LOGIN_ID_KEY, null);
		boolean autoLogin = settings.getBoolean(Constants.AUTO_LOGIN, false);
		String nickNameVal = settings.getString(Constants.NICK_NAME, "");
		nickName = nickNameVal;
		profileImageName = settings.getString(Constants.PROFILE_IMAGE, null);
		if(profileImageName == null){
			Double d = Math.random();
			int dValue = d.intValue() % 5 + 1;
			profileImageName = "default_" + dValue + ".png";
			SharedPreferences.Editor profileEditor = settings.edit();
			profileEditor.putString(Constants.PROFILE_IMAGE, profileImageName);
			profileEditor.commit();
		}
		final CheckBox autoLoginCheckbox = (CheckBox)findViewById(R.id.config_autoLogin);
		final TextView memberType = (TextView)findViewById(R.id.config_memberType);
		final TextView loginIdField = (TextView)findViewById(R.id.config_loginId);
		final EditText nickNameEdit = (EditText)findViewById(R.id.config_nickName);
		final ImageView profileImageView =  (ImageView)findViewById(R.id.config_profileImage);
		
		
		loginIdField.setText(loginId);
		memberType.setText(settings.getString(Constants.MEMBER_TYPE, null));
		if(autoLogin)
			autoLoginCheckbox.setChecked(true);
		else
			autoLoginCheckbox.setChecked(false);
		
		autoLoginCheckbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean value = false;
				if(autoLoginCheckbox.isChecked()){
					value = true;
				}else{
					value = false;
				}
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(Constants.AUTO_LOGIN, value);
				editor.commit();
			};
		});
		
		AssetManager assetManager = this.getAssets();
		try{
			if(profileImageName.startsWith("default")){
				AssetInputStream statusBuf = (AssetInputStream)assetManager.open("icon/" + profileImageName);
				Bitmap statusBitmap = BitmapFactory.decodeStream(statusBuf);
				profileImageView.setImageBitmap(statusBitmap);
				statusBuf.close();		
				
			}else{
				setImg(profileImageView,profileImageName);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		boolean isCheckNickName = settings.getBoolean(Constants.CHECK_NICK_NAME, false);
//		if(nickNameVal != null && !"".equals(nickNameVal) && !isCheckNickName){
//			progressDialog = MyProgressDialog.show(context,"","",true,true,null);
//			new NickNameCheckHandler().execute(nickNameVal);
//		}
		
		nickNameEdit.setText(nickNameVal);
		//nickNameEdit.setOnEditorActionListener(this);
		
		final Button saveBtn = (Button)findViewById(R.id.config_saveBtn);
		saveBtn.setOnClickListener(this);
		
		Button changeProfileBtn = (Button)findViewById(R.id.config_changeProfileBtn);
		changeProfileBtn.setOnClickListener(this);
	}
	
	public void save(){
		final EditText nickNameEdit = (EditText)findViewById(R.id.config_nickName);
		String val = nickNameEdit.getText().toString();
		progressDialog = MyProgressDialog.show(context,"","",true,true,null);
		new NickNameCheckHandler().execute(val);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		switch(v.getId()){
		case R.id.config_nickName :
			if(event == null || actionId == KeyEvent.ACTION_DOWN){
				String val = v.getText().toString();
				progressDialog = MyProgressDialog.show(context,"","",true,true,null);
				new NickNameCheckHandler().execute(val);
			}
			break;
		}
		return false;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK) return;
		if(requestCode == TAKE_PICTURE || requestCode == PICK_IMAGE){
			Uri imageUri = null;
			if(data != null){
				Bitmap thumbnail = null;
				try{
					if(data.hasExtra("data")){
						thumbnail = data.getParcelableExtra("data");
					}else{
						imageUri = data.getData();
						thumbnail = Images.Media.getBitmap(getContentResolver(), imageUri);
					}
					
					File pathCache = getCacheDir();
					File f = new File(pathCache,settings.getString(Constants.LOGIN_ID_KEY, null));
					if(f.exists())
						f.delete();
					FileOutputStream out = new FileOutputStream(f);
					thumbnail.compress(Bitmap.CompressFormat.PNG, 90, out);
					progressDialog = MyProgressDialog.show(context,"","",true,true,null);
					out.close();
					new ProfileDataHandler().execute(f);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	}

	public void pickImage(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
 	   	intent.setType("image/*");
 	   	startActivityForResult(intent, PICK_IMAGE);
	}

	public void takePhoto(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
		startActivityForResult(intent, TAKE_PICTURE);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.config_saveBtn){
			save();
		}else{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("사진선택")
		           .setCancelable(true)
		           .setPositiveButton("앨범에서", new DialogInterface.OnClickListener() {
		               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
		            	   pickImage();
		               }
		           })
		           .setNegativeButton("사진촬영", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							takePhoto();
						}
					})
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							
						}
					});
		    final AlertDialog alert = builder.create();
		    alert.show();
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
	
	private void updateProfileImage(String imageName){
		if(progressDialog != null){
			progressDialog.dismiss();
			progressDialog = null;
		}
		File pathCache = getCacheDir();
		File f = new File(pathCache,settings.getString(Constants.LOGIN_ID_KEY, null));
		if(f.exists())
			f.delete();
		final ImageView profileImageView =  (ImageView)findViewById(R.id.config_profileImage);
		try{
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(Constants.PROFILE_IMAGE, imageName);
			editor.commit();	
			setImg(profileImageView,imageName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateNickName(){
		EditText n = (EditText)findViewById(R.id.config_nickName);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.NICK_NAME, n.getText().toString());
		editor.putBoolean(Constants.CHECK_NICK_NAME, true);
		editor.commit();	
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

	private class NickNameCheckHandler extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			String nickName = params[0];

			
			String responseText;
		
			ArrayList<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair>();
			ps.add(new BasicNameValuePair("nickName",nickName));
			ps.add(new BasicNameValuePair("sessionKey", settings.getString(Constants.SESSION_KEY, null)));
			ps.add(new BasicNameValuePair("profileImage",profileImageName));

			
			try{
			
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(ps, "UTF-8");
				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/updateNickNameBySession.do");
				httpPost.setEntity(entity);
				
				HttpClient httpClient = new DefaultHttpClient();
				
				responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						boolean success = jsonObject.getBoolean("success");
						if(success) return "true";
						else return jsonObject.getString("message");
					}
				}catch(Exception e){
					e.printStackTrace();
					responseText = null;
				}
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
				progressDialog = null;
			}
			if(result == null){
				buildAlertMessage("네트워크 장애가 있습니다. 추후에 다시 시도하세요",false);
			}else if(result.equals("true")){
				updateNickName();
				buildAlertMessage("성공적으로 저장되었습니다.",false);
			}else{
				buildAlertMessage(result,false);
				
			}
		}
	}	

	private class ProfileDataHandler extends AsyncTask<File,String,String>{

		@Override
		protected String doInBackground(File... params) {

			ArrayList<StoryInfo> storyDatas = new ArrayList<StoryInfo>();
			File image = params[0];
			String profileName = null;
			DataOutputStream outputStream;
			HttpURLConnection connection;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;
			
			try{
				FileInputStream fileInputStream = new FileInputStream(image);
				URL url = new URL(Constants.HOST_NAME + "/bamStory/mobile/bam/uploadProfileBySession.do");
				connection = (HttpURLConnection) url.openConnection();
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary =  "*****";
				
				// Allow Inputs & Outputs
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// Enable POST method
				connection.setRequestMethod("POST");

				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

				outputStream = new DataOutputStream( connection.getOutputStream() );
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream.writeBytes("Content-Disposition: form-data; name=\"anyFile\";filename=\"" + settings.getString(Constants.SESSION_KEY, null) +".png\"" + lineEnd);
				outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0)
				{
					outputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				//serverResponseCode = connection.getResponseCode();
				String responseText = connection.getResponseMessage();

				fileInputStream.close();
				outputStream.flush();
				outputStream.close();
				
				if(responseText != null && "OK".equals(responseText)){
					return "/bamImages/customer/" + settings.getString(Constants.LOGIN_ID_KEY, null) + ".png";
				}else{
					buildAlertMessage("업로드에 문제가 발생했습니다.다시 시도하세요",false);
					return null;
				}
					
				/*
				FileEntity fileEntity = null;   
				fileEntity = new FileEntity(image,"application/octet-stream");

				HttpPost httpPost = new HttpPost(
						Constants.HOST_NAME + "/bamStory/mobile/bam/uploadProfile.do");
				httpPost.setEntity(fileEntity);
				httpPost.addHeader("filename", deviceId + ".png");
				httpPost.addHeader("name","anyFile");
				
				HttpClient httpClient = new DefaultHttpClient();
				
				String responseText = httpClient.execute(httpPost, new BasicResponseHandler());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(responseText);

					if(jsonObject != null) {
						boolean success = jsonObject.getBoolean("success");
						if(success){
							return "/bamImages/customer/" + deviceId + ".png";
						}else{
							String message = jsonObject.getString("message");
							buildAlertMessage(message,false);
							return null;
						}
					}
					
				} catch(Exception exception) {
					exception.printStackTrace();
				}
				finally {}
*/
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String results) {
			super.onPostExecute(results);
			if(results != null)
				updateProfileImage(results);
		}
		
	}		
}


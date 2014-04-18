package com.bizwave.bamstory.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bizwave.bamstory.Constants;
import com.bizwave.bamstory.PartnerInfo;
import com.bizwave.bamstory.R;
import com.bizwave.bamstory.map.MyLocationWrapper;
import com.bizwave.bamstory.map.PartnerItemizedOverlay;
import com.bizwave.bamstory.util.MyProgressDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class MyLocationActivity extends MapActivity implements LocationListener,OnClickListener{
	private boolean fromTab;
	private LocationManager locationManager;
	private String getParterUrl = Constants.HOST_NAME + "/bamStory/mobile/bam/getPartnersByLocation.do";
	private MyLocationOverlay myLocationOverlay;
	private String deviceId;
	private Context mContext;
	private MyProgressDialog progressDialog;
	
	private static String debugKey = "08HgmyFokl-b7PbfW_It_rriUTAU3ICBfoqPEyA";
	private static String releaseKey ="08HgmyFokl-ZtyOv9mCGEeR0VXGQVx-GjLWQuQw";
	private SharedPreferences settings;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		settings = getSharedPreferences(Constants.PREFERENCE, 0);
		mContext = this;
		
		setContentView(R.layout.mylocation);
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		
		
		final MapView map = (MapView)findViewById(R.id.map1);
		map.setSatellite(false);
		map.setBuiltInZoomControls(true);
		map.setClickable(true);
		map.setEnabled(true);
		final MapController mapControl = map.getController();
		mapControl.setZoom(16);

		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		
		String bestProvider = locationManager.getBestProvider(criteria, true);
		if(bestProvider == null){
			buildAlertMessage("위치정보를 얻을수 없습니다. 현위치 기반으로 찾고 싶으면 GPS를 켜세요.");
			return;
		}		
		
		myLocationOverlay = new MyLocationWrapper(this,map);
		myLocationOverlay.enableMyLocation();
		
		Bundle param = getIntent().getExtras();
		if(param != null && param.containsKey("partner")){
			//특정 업소의 정보를 직접 보여줄 경우 
			PartnerInfo partner = new PartnerInfo();
			partner.setId(param.getString("id"));
			partner.setName(param.getString("name"));
			int latitude = param.getInt("latitude");
			int longitude = param.getInt("longitude");
			partner.setType(param.getString("type"));
			partner.setTypeName(param.getString("typeName"));
			
			partner.setPoint(new GeoPoint(latitude,longitude));
			
			this.drawPartner(partner);
		}else{
		
			if(myLocationOverlay.isMyLocationEnabled()){
				progressDialog = MyProgressDialog.show(this,"","",true,true,null);
				myLocationOverlay.runOnFirstFix(new Runnable(){
					@Override
					public void run() {
						map.getOverlays().add(myLocationOverlay);
						moveToPoint(myLocationOverlay.getMyLocation(),true);
						map.postInvalidate();
					}
				});
			}else{
				GeoPoint point = new GeoPoint(37497606,127034054);
				moveToPoint(point,true);
			}
		}

		//locationManager.requestLocationUpdates(bestProvider, 3000, 100, this);		

		Button currLocationBtn = (Button)findViewById(R.id.CurrntBtn);
		currLocationBtn.setOnClickListener(this);
		

		
	}
	
	private void buildAlertMessage(String msg) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(msg)
	           .setCancelable(false)
	           .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   //finish();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
}
	
	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	private void moveToPoint(GeoPoint point,boolean isGetPartnerInfo){
		MapView map = (MapView)findViewById(R.id.map1);
		final MapController mapControl = map.getController();
		mapControl.animateTo(point);
		if(isGetPartnerInfo)
			getNearbyPartners(point);
	}
	
	
	
	
	private void getNearbyPartners(GeoPoint point){
		String url = getParterUrl;
		double latitude = point.getLatitudeE6()/1E6;
		double longitude = point.getLongitudeE6()/1E6;
		String sessionKey =  settings.getString(Constants.SESSION_KEY, null);
		
		url = url + "?phoneType=ANDROID&sessionKey=" + sessionKey + "&latitude=" + latitude + "&longitude=" + longitude;
		// Private 클래스를 호출해서 Background로 데이타를 가져온다. 
		
		new PartnerLocationHandler().execute(url);
	}
	

	private synchronized void clearOverlay(List<Overlay> mapOverlays){
		Iterator<Overlay> iter = mapOverlays.iterator();
		while(iter.hasNext()){
			Overlay overlay = iter.next();
			if(overlay instanceof PartnerItemizedOverlay){
				iter.remove();
			}
		}
		
	}
	
	
	public void drawPartner(PartnerInfo partner){
		final MapView map = (MapView)findViewById(R.id.map1);
		List<Overlay> mapOverlays = map.getOverlays();
		clearOverlay(mapOverlays);
		
		Drawable partner1;
		AssetManager assetManager = mContext.getAssets();
		try{
			AssetInputStream typeBuf = (AssetInputStream)assetManager.open("icon/s_" + partner.getType().toLowerCase()+".png");
			Bitmap typeBitmap = BitmapFactory.decodeStream(typeBuf);
			partner1 = new BitmapDrawable(typeBitmap);
			typeBuf.close();
		}catch(Exception e){
			
			return;
		}
		PartnerItemizedOverlay overlayItems1 = new PartnerItemizedOverlay(partner1,this);
		overlayItems1.addLocation(partner.getPoint(), partner.getId(), partner.getName(), partner.getTypeName());
		mapOverlays.add(overlayItems1);
		map.postInvalidate();		
		moveToPoint(partner.getPoint(),false);
	}
	
	public void drawPartners(ArrayList<PartnerInfo> partners){
		final MapView map = (MapView)findViewById(R.id.map1);
		if(partners.size() == 0) return;
		List<Overlay> mapOverlays = map.getOverlays();
		clearOverlay(mapOverlays);
		HashMap<String,ArrayList<PartnerInfo>> pMap = new HashMap<String,ArrayList<PartnerInfo>>();
		
		for(PartnerInfo info : partners){
			String type = info.getType();
			ArrayList<PartnerInfo> infos;
			if(pMap.containsKey(type)){
				infos = pMap.get(type);
			}else{
				infos = new ArrayList<PartnerInfo>();
				pMap.put(type, infos);
			}
			infos.add(info);
		}
		
		for(String type : pMap.keySet()){
			addOverlay(mapOverlays,pMap.get(type),type);
		}
		
		map.postInvalidate();
	}
	
	
	private void addOverlay(List<Overlay> mapOverlays,List<PartnerInfo> partners,String typeName){
		Drawable partner1;
		AssetManager assetManager = mContext.getAssets();
		try{
			AssetInputStream typeBuf = (AssetInputStream)assetManager.open("icon/s_" + typeName.toLowerCase()+".png");
			Bitmap typeBitmap = BitmapFactory.decodeStream(typeBuf);
			partner1 = new BitmapDrawable(typeBitmap);
			typeBuf.close();
		}catch(Exception e){
			return;
		}
		PartnerItemizedOverlay overlayItems1 = new PartnerItemizedOverlay(partner1,this);
		for(PartnerInfo info : partners){
			overlayItems1.addLocation(info.getPoint(), info.getId(), info.getName(), info.getTypeName());
		}
		mapOverlays.add(overlayItems1);
	}
	

	/** Location Listener **/
	/**
	 * 현재는 Location Listener를 이용하지 않는다. MyLocation OverLay로 위치를 찾는다. 
	 */
	@Override
	public void onLocationChanged(Location lc) {
		//getNearbyPartners(new GeoPoint((int)(lc.getLatitude() * 1E6),(int)(lc.getLongitude() * 1E6)));
		//locationManager.removeUpdates(this);		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.CurrntBtn){
			GeoPoint point = myLocationOverlay.getMyLocation();
			if(point == null){
				AlertDialog alert = new AlertDialog.Builder(this)
					.setMessage("GPS수신이 원활하지 않습니다. 나중에 다시 시도하세요.")
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
				
			}else{
				moveToPoint(myLocationOverlay.getMyLocation(),true);
			}
		}
	}


	public void viewPartnerInfo(String id){
		Bundle param = new Bundle();
		param.putString("partnerId", id);
		Intent intent = new Intent(this,PartnerDetailActivity.class);
		intent.putExtras(param);
		startActivity(intent);
	}

	private class PartnerLocationHandler extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			try{
				HttpParams hParams = httpclient.getParams();
				HttpConnectionParams.setConnectionTimeout(hParams, 5000);
				HttpConnectionParams.setSoTimeout(hParams, 5000);
				
				HttpGet hGet = new HttpGet(url);
				response = httpclient.execute(hGet);
				HttpEntity entity = response.getEntity();
				if(entity != null){
					InputStream is = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while((line = reader.readLine()) != null){
						sb.append(line + "/n");
					}
					is.close();
					return sb.toString();
				}
			}catch(Exception e){
				e.printStackTrace();
				AlertDialog alert = new AlertDialog.Builder(mContext)
				.setMessage("네트워크 연결이 원활하지 않습니다. 프로그램 종료후에 다시 시작하세요.")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();				
			}finally{
				httpclient.getConnectionManager().shutdown();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(progressDialog != null)
				progressDialog.dismiss();
			if(result == null) return;
			ArrayList<PartnerInfo> infos = new ArrayList<PartnerInfo>();
			try{
				JSONObject obj = new JSONObject(result);
				JSONArray array = (JSONArray)obj.get("data");
				for(int i = 0; i < array.length(); i++){
					JSONObject partner = array.getJSONObject(i);
					double latitude = partner.getDouble("latitude");
					double longitude = partner.getDouble("longitude");

					GeoPoint point = new GeoPoint((int)(latitude * 1E6),(int)(longitude * 1E6));
					PartnerInfo info = new PartnerInfo();
					info.setId(partner.getString("id"));
					info.setName(partner.getString("title"));
					info.setPoint(point);
					info.setType(partner.getString("type"));
					info.setTypeName(partner.getString("typeName"));
					infos.add(info);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			drawPartners(infos);	//얻어온 정보를 이용해서 화면에 그려준다.
		}
		
	}
	
}

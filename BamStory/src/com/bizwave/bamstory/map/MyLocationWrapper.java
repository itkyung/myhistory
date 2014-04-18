package com.bizwave.bamstory.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;

import com.bizwave.bamstory.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyLocationWrapper extends MyLocationOverlay {
	private Context context;
	private Bitmap myBitmap;
	
//	public MyLocationWrapper(){
//		
//	}
	public MyLocationWrapper(Context ct,MapView mv){
		super(ct,mv);
		context = ct;
	}
	
	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView,
			Location lastFix, GeoPoint myLocation, long when) {
		Point screenPts = mapView.getProjection().toPixels(myLocation, null);
		myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.currlocation);
		canvas.drawBitmap(myBitmap, screenPts.x - (myBitmap.getWidth() / 2),
				screenPts.y - (myBitmap.getHeight() / 2),null);
		
	}

	@Override
	public boolean onTap(GeoPoint p, MapView map) {
		// TODO Auto-generated method stub
		return super.onTap(p, map);
	}
	
	
}

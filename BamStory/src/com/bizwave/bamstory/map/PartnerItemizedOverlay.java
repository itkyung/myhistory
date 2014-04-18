package com.bizwave.bamstory.map;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.bizwave.bamstory.activity.BamStoryTabActivity;
import com.bizwave.bamstory.activity.MyLocationActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PartnerItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<LocationInfo> items = new ArrayList<LocationInfo>();
	private ArrayList<OverlayItem> oItems = new ArrayList<OverlayItem>();
	private Context mContext = null;
	private int selectedIndex;
	
	public PartnerItemizedOverlay(Drawable defaultMarker, Context context){
		super(defaultMarker);
		boundCenterBottom(defaultMarker);
		mContext = context;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return oItems.get(i);
	}

	@Override
	public int size() {
		return oItems.size();
	}
	
	public void addLocation(GeoPoint point,String id,String text,String type){
		LocationInfo info = new LocationInfo(point,id,text,type);
		items.add(info);
		OverlayItem item = new OverlayItem(point,text,null);
		oItems.add(item);
		populate();
	}

	@Override
	protected boolean onTap(int index) {
		LocationInfo info = items.get(index);
		selectedIndex = index;
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(info.getText());
		dialog.setMessage(info.getType());
		dialog.setPositiveButton("상세보기", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dl, int which) {
				//TODO 웹뷰쪽으로 이동해서 상세정보를 보여줘야한다.
				if(mContext instanceof MyLocationActivity){
					LocationInfo info = items.get(selectedIndex);
					((MyLocationActivity)mContext).viewPartnerInfo(info.getId());
				}
			}
		});
		dialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dl, int which) {
				dl.dismiss();
			}
		});
		
		
		dialog.show();
		return true;
	}
	
}

package com.bizwave.bamstory.map;

import com.google.android.maps.GeoPoint;

public class LocationInfo {
	private GeoPoint point;
	private String id;
	private String text;
	private String type;
	
	
	public LocationInfo(GeoPoint point, String id, String text, String type) {
		super();
		this.point = point;
		this.id = id;
		this.text = text;
		this.type = type;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}

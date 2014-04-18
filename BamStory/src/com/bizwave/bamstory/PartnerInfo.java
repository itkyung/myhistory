package com.bizwave.bamstory;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class PartnerInfo implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3499528695713822975L;
	
	
	private GeoPoint point;
	private String name;
	private String id;
	private String type;
	private String typeName;
	private String imgUrl;
	private String typeImgName;	
	private String imgUrl1;
	private String imgUrl2;
	private String startTime;
	private String firstTime;
	private String secondTime;
	private String desc;
	private String address;
	private String descHtml;
	
	private ArrayList<SimpleBag> contacts;
	
	public PartnerInfo(){
		
	}
	
	public PartnerInfo(GeoPoint point, String name, String id, String type, String typeName) {
		super();
		this.point = point;
		this.name = name;
		this.id = id;
		this.type = type;
		this.typeName = typeName;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getTypeImgName() {
		return typeImgName;
	}

	public void setTypeImgName(String typeImgName) {
		this.typeImgName = typeImgName;
	}

	public String getImgUrl1() {
		return "null".equals(imgUrl1) ? null : imgUrl1;
	}

	public void setImgUrl1(String imgUrl1) {
		this.imgUrl1 = imgUrl1;
	}

	public String getImgUrl2() {
		return "null".equals(imgUrl2) ? null : imgUrl2;
	}

	public void setImgUrl2(String imgUrl2) {
		this.imgUrl2 = imgUrl2;
	}

	public String getStartTime() {
		return startTime == null || "null".equals(startTime) ? "" : startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getFirstTime() {
		return firstTime == null || "null".equals(firstTime) ? "" : firstTime;
	}

	public void setFirstTime(String firstTime) {
		this.firstTime = firstTime;
	}

	public String getSecondTime() {
		return secondTime == null || "null".equals(secondTime) ? "" : secondTime;
	}

	public void setSecondTime(String secondTime) {
		this.secondTime = secondTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<SimpleBag> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<SimpleBag> contacts) {
		this.contacts = contacts;
	}

	public String getDescHtml() {
		return descHtml;
	}

	public void setDescHtml(String descHtml) {
		this.descHtml = descHtml;
	}
	
	
}

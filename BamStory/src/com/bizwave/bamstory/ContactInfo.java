package com.bizwave.bamstory;

import java.util.ArrayList;

public class ContactInfo {
	private String id;
	private String name;
	private String phone;
	private String desc;
	private String evalImgName;
	private PartnerInfo partner;
	private ArrayList<SimpleBag> prices;
	private ArrayList<String> events;
	private EvalInfo evalInfo;
	private String descHtml;
	private Boolean priceBidding;
	private Boolean bamsCertFlag;
	private String biddingId;
	private Boolean haveGirl;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public PartnerInfo getPartner() {
		return partner;
	}
	public void setPartner(PartnerInfo partner) {
		this.partner = partner;
	}
	public ArrayList<SimpleBag> getPrices() {
		return prices;
	}
	public void setPrices(ArrayList<SimpleBag> prices) {
		this.prices = prices;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getEvalImgName() {
		return evalImgName;
	}
	public void setEvalImgName(String evalImgName) {
		this.evalImgName = evalImgName;
	}
	public ArrayList<String> getEvents() {
		return events;
	}
	public void setEvents(ArrayList<String> events) {
		this.events = events;
	}
	public EvalInfo getEvalInfo() {
		return evalInfo;
	}
	public void setEvalInfo(EvalInfo evalInfo) {
		this.evalInfo = evalInfo;
	}
	public String getDescHtml() {
		return descHtml;
	}
	public void setDescHtml(String descHtml) {
		this.descHtml = descHtml;
	}
	public Boolean getPriceBidding() {
		return priceBidding;
	}
	public void setPriceBidding(Boolean priceBidding) {
		this.priceBidding = priceBidding;
	}
	public Boolean getBamsCertFlag() {
		return bamsCertFlag;
	}
	public void setBamsCertFlag(Boolean bamsCertFlag) {
		this.bamsCertFlag = bamsCertFlag;
	}
	public String getBiddingId() {
		return biddingId;
	}
	public void setBiddingId(String biddingId) {
		this.biddingId = biddingId;
	}
	public Boolean getHaveGirl() {
		return haveGirl;
	}
	public void setHaveGirl(Boolean haveGirl) {
		this.haveGirl = haveGirl;
	}

	
	
}

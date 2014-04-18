package com.bizwave.bamstory;

import java.util.ArrayList;

public class BiddingDetailInfo {
	private String id;
	private String partnerName;
	private String contactName;
	private String description;
	private String title;
	private String successfulBidderDeviceId;
	private String successfulBidNickName;
	private Boolean closed;
	private Boolean successfulBid;
	private Boolean ready;
	private String startDate;
	private String endDate;
	private Integer startBidPrice;
	private Integer winningBidPrice;
	
	private ArrayList<BiddingActivity> activities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSuccessfulBidderDeviceId() {
		return successfulBidderDeviceId;
	}

	public void setSuccessfulBidderDeviceId(String successfulBidderDeviceId) {
		this.successfulBidderDeviceId = successfulBidderDeviceId;
	}

	public String getSuccessfulBidNickName() {
		return successfulBidNickName;
	}

	public void setSuccessfulBidNickName(String successfulBidNickName) {
		this.successfulBidNickName = successfulBidNickName;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	public Boolean getSuccessfulBid() {
		return successfulBid;
	}

	public void setSuccessfulBid(Boolean successfulBid) {
		this.successfulBid = successfulBid;
	}

	public Boolean getReady() {
		return ready;
	}

	public void setReady(Boolean ready) {
		this.ready = ready;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getStartBidPrice() {
		return startBidPrice;
	}

	public void setStartBidPrice(Integer startBidPrice) {
		this.startBidPrice = startBidPrice;
	}

	public Integer getWinningBidPrice() {
		return winningBidPrice;
	}

	public void setWinningBidPrice(Integer winningBidPrice) {
		this.winningBidPrice = winningBidPrice;
	}

	public ArrayList<BiddingActivity> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<BiddingActivity> activities) {
		this.activities = activities;
	}
	
	
	
	
}

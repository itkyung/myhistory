package com.bizwave.bamstory;

public class BiddingSimpleInfo {
	private String id;
	private String partnerName;
	private String contactName;
	private String title;
	private Boolean ready;
	private Boolean closed;
	private Boolean successfulBid;
	private String bidDate;
	private String bidPrice;
	private String winningBidPrice;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getReady() {
		return ready;
	}
	public void setReady(Boolean ready) {
		this.ready = ready;
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
	public String getBidDate() {
		return bidDate;
	}
	public void setBidDate(String bidDate) {
		this.bidDate = bidDate;
	}
	public String getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}
	public String getWinningBidPrice() {
		return winningBidPrice;
	}
	public void setWinningBidPrice(String winningBidPrice) {
		this.winningBidPrice = winningBidPrice;
	}
	
	
}

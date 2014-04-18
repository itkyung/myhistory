package com.bizwave.bamstory;

public class BiddingActivity {
	private String id;
	private String nickName;
	private Integer bidPrice;
	private String bidDate;
	private Boolean successfulBid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(Integer bidPrice) {
		this.bidPrice = bidPrice;
	}
	public String getBidDate() {
		return bidDate;
	}
	public void setBidDate(String bidDate) {
		this.bidDate = bidDate;
	}
	public Boolean getSuccessfulBid() {
		return successfulBid;
	}
	public void setSuccessfulBid(Boolean successfulBid) {
		this.successfulBid = successfulBid;
	}
	
	
}

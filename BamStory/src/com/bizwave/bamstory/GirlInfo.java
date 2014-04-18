package com.bizwave.bamstory;

import java.io.Serializable;

public class GirlInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7358420992142988255L;
	private String id;
	private String nickName;
	private int likeCount;
	private String partnerName;
	private String contactName;
	private String contactId;
	private String profileThumbnail;
	private String profileImage1;
	private String profileImage2;
	private String profile;
	
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
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
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
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getProfileThumbnail() {
		return profileThumbnail;
	}
	public void setProfileThumbnail(String profileThumbnail) {
		this.profileThumbnail = profileThumbnail;
	}
	public String getProfileImage1() {
		return profileImage1;
	}
	public void setProfileImage1(String profileImage1) {
		this.profileImage1 = profileImage1;
	}
	public String getProfileImage2() {
		return profileImage2;
	}
	public void setProfileImage2(String profileImage2) {
		this.profileImage2 = profileImage2;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	
}

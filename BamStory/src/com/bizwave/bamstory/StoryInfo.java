package com.bizwave.bamstory;

public class StoryInfo {
	private String id;
	private String nickName;
	private String message;
	private int replyCount;
	private String created;
	private String originalStoryId;
	private String profileImage;
	private boolean topFlag;
	private boolean bamstoryFlag;
	private String userId;
	private String userType;
	
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getOriginalStoryId() {
		return originalStoryId;
	}
	public void setOriginalStoryId(String originalStoryId) {
		this.originalStoryId = originalStoryId;
	}
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public boolean isTopFlag() {
		return topFlag;
	}
	public void setTopFlag(boolean topFlag) {
		this.topFlag = topFlag;
	}
	public boolean isBamstoryFlag() {
		return bamstoryFlag;
	}
	public void setBamstoryFlag(boolean bamstoryFlag) {
		this.bamstoryFlag = bamstoryFlag;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
}

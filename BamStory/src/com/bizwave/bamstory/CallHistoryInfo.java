package com.bizwave.bamstory;

import java.util.Date;

public class CallHistoryInfo {
	private String id;
	private String title;
	private String contactId;

	private String callDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getCallDate() {
		return callDate;
	}
	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	
}

package com.bizwave.bamstory.db;

public class EventContactMapping {
	public static final String TABLE_NAME = "bam_eventcontact";
	private String eventId;
	private String contactId;
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	
}

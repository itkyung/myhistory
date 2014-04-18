package com.bizwave.bamstory.db;

public class CategoryContactMapping {
	public static final String TABLE_NAME = "bam_categorycontact";
	private String categoryId;
	private String contactId;
	
	public CategoryContactMapping(String categoryId, String contactId) {
		this.categoryId = categoryId;
		this.contactId = contactId;
	}

	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
}

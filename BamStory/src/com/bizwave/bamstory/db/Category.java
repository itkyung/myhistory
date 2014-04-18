package com.bizwave.bamstory.db;

public class Category {
	public static final String TABLE_NAME = "bam_category";
	private String id;
	private String text;
	private String type;
	private String parentId;
	private Boolean partner;
	
	public Category(String id, String text, String type, String parentId, Boolean partner) {
		this.id = id;
		this.text = text;
		this.type = type;
		this.parentId = parentId;
		this.partner = partner;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Boolean isPartner() {
		return partner;
	}
	public void setPartner(Boolean partner) {
		this.partner = partner;
	}
}

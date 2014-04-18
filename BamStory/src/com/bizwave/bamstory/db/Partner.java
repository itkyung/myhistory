package com.bizwave.bamstory.db;

public class Partner {
	public static final String TABLE_NAME = "bam_partnercontact";
		
	private String _id;
	private String partnerName;
	private int idx;
	private String contactId;
	private String partnerTypeName;
	private String evaluationImageName;
	private String contactName;
	private String partnerId;
	private int bamsCertFlag;
	private int haveGirl;
	private int evalCount;
	
	private String partnerType;
	private double evaluation;
	
	public Partner(String _id,
			String partnerName, int idx, String contactId, String partnerTypeName,
			String evaluationIamgeName, String contactName, String partnerId,
			String partnerType, double evaluation, int bamsCertFlag,int haveGirl,int evalCount) {
		this.partnerName = partnerName;
		this.idx = idx;
		this.contactId = contactId;
		this.partnerTypeName = partnerTypeName;
		this.evaluationImageName = evaluationIamgeName;
		this.contactName = contactName;
		this.partnerId = partnerId;
		this.partnerType = partnerType;
		this.evaluation = evaluation;
		this.bamsCertFlag = bamsCertFlag;
		this.haveGirl = haveGirl;
		this.evalCount = evalCount;
	}
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public String getPartnerTypeName() {
		return partnerTypeName;
	}
	public void setPartnerTypeName(String partnerTypeName) {
		this.partnerTypeName = partnerTypeName;
	}
	public String getEvaluationImageName() {
		return evaluationImageName;
	}
	public void setEvaluationImageName(String evaluationImageName) {
		this.evaluationImageName = evaluationImageName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getPartnerType() {
		return partnerType;
	}
	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}
	public double getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}

	public int getBamsCertFlag() {
		return bamsCertFlag;
	}

	public void setBamsCertFlag(int bamsCertFlag) {
		this.bamsCertFlag = bamsCertFlag;
	}

	public int getHaveGirl() {
		return haveGirl;
	}

	public void setHaveGirl(int haveGirl) {
		this.haveGirl = haveGirl;
	}

	public int getEvalCount() {
		return evalCount;
	}

	public void setEvalCount(int evalCount) {
		this.evalCount = evalCount;
	}
	
	
}

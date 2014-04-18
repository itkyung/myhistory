package com.bizwave.bamstory.db;

public class RevisionInfo {
	public static final String TABLE_NAME = "bam_revisioninfo";
	private String cRevision;
	private String pRevisiion;
	
	public RevisionInfo(String cRevision, String pRevision) {
		this.cRevision = cRevision;
		this.pRevisiion = pRevision;
	}

	public String getcRevision() {
		return cRevision;
	}
	public void setcRevision(String cRevision) {
		this.cRevision = cRevision;
	}
	public String getpRevisiion() {
		return pRevisiion;
	}
	public void setpRevisiion(String pRevisiion) {
		this.pRevisiion = pRevisiion;
	}
}

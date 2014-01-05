package com.budoudoh.codetalker.models;

public class WatsonObject {
	private String ResponseId;
	private String Status;
	private WatsonNBest[] NBest;
	
	public WatsonObject(String responseId, String status, WatsonNBest[] nBest) {
		super();
		ResponseId = responseId;
		Status = status;
		NBest = nBest;
	}

	public String getResponseId() {
		return ResponseId;
	}

	public void setResponseId(String responseId) {
		ResponseId = responseId;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public WatsonNBest[] getNBest() {
		return NBest;
	}

	public void setNBest(WatsonNBest[] nBest) {
		NBest = nBest;
	}
}

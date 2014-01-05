package com.budoudoh.codetalker.models;

public class WatsonResult {
	private WatsonObject Recognition;

	public WatsonResult(WatsonObject recognition) {
		super();
		Recognition = recognition;
	}

	public WatsonObject getRecognition() {
		return Recognition;
	}

	public void setRecognition(WatsonObject recognition) {
		Recognition = recognition;
	}
}


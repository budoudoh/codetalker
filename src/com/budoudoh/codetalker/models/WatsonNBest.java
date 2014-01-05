package com.budoudoh.codetalker.models;

public class WatsonNBest {
	private double Confidence;
	private String Grade;
	private String Hypothesis;
	private String LanguageId;
	private String ResultText;
	private Double[] WordScores;
	private String[] Words;
	
	public WatsonNBest(double confidence, String grade, String hypothesis,
			String languageId, String resultText, Double[] wordScores,
			String[] words) {
		super();
		Confidence = confidence;
		Grade = grade;
		Hypothesis = hypothesis;
		LanguageId = languageId;
		ResultText = resultText;
		WordScores = wordScores;
		Words = words;
	}

	public double getConfidence() {
		return Confidence;
	}

	public void setConfidence(double confidence) {
		Confidence = confidence;
	}

	public String getGrade() {
		return Grade;
	}

	public void setGrade(String grade) {
		Grade = grade;
	}

	public String getHypothesis() {
		return Hypothesis;
	}

	public void setHypothesis(String hypothesis) {
		Hypothesis = hypothesis;
	}

	public String getLanguageId() {
		return LanguageId;
	}

	public void setLanguageId(String languageId) {
		LanguageId = languageId;
	}

	public String getResultText() {
		return ResultText;
	}

	public void setResultText(String resultText) {
		ResultText = resultText;
	}

	public Double[] getWordScores() {
		return WordScores;
	}

	public void setWordScores(Double[] wordScores) {
		WordScores = wordScores;
	}

	public String[] getWords() {
		return Words;
	}

	public void setWords(String[] words) {
		Words = words;
	}
	
}

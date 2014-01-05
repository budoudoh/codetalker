package com.budoudoh.codetalker.interfaces;

interface CodeTalkerServiceInterface{
	void refreshProfileCache();
	boolean loadProfileCache();
	void fetchCategories();
	void refreshPurchasedProfiles();
	void refreshUser();
}
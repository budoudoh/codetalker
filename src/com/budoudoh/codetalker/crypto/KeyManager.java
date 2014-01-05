package com.budoudoh.codetalker.crypto;


public class KeyManager {

   private static final String TAG = "KeyManager";
   private String key;
   private String iv;
   
   public KeyManager(String key, String iv) {
	this.key = key;
	this.iv = iv;
   }

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getIv() {
		return iv;
	}
	
	public void setIv(String iv) {
		this.iv = iv;
	}
}
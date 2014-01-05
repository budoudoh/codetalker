package com.budoudoh.codetalker.models;

public class StoredCredentials 
{
	private String name;
	private String username;
	private String passwordHash;
	private String key;
	private String iv;
	private String wav_file;
	

	public StoredCredentials(String name, String username, String passwordHash,
			String key, String iv, String wav_file) {
		super();
		this.name = name;
		this.username = username;
		this.passwordHash = passwordHash;
		this.key = key;
		this.iv = iv;
		this.wav_file = wav_file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

	public String getWav_file() {
		return wav_file;
	}

	public void setWav_file(String wav_file) {
		this.wav_file = wav_file;
	};
	
	
}

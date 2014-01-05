package com.budoudoh.codetalker;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.inputmethod.InputConnection;

import com.budoudoh.codetalker.crypto.Crypto;
import com.budoudoh.codetalker.crypto.KeyManager;
import com.budoudoh.codetalker.models.StoredCredentials;
import com.budoudoh.codetalker.settings.Preferences;
import com.google.gson.Gson;

public class CodeTalker extends Application {
	public static final boolean DEFAULT_USE_WATSON = false;
	public static final String DEFAULT_WATSON_SDK_REQUEST_URL = "https://api.att.com/speech/v3/speechToText";
	public static final String DEFAULT_WATSON_OAUTH_URL = "https://api.att.com/oauth/token";
	public static final String DEFAULT_WATSON_KEY = "hrk7xrk0f8cck6jnysuaydnt3x6novab";
	public static final String DEFAULT_WATSON_SECRET = "foukamzf5onlibspycrre3ireha0ipdg";
	public static final int  DEFAULT_WATSON_OAUTH_TIMEOUT = 3600000;
	private static final String AUDIO_RECORDER_FOLDER = "CodeTalker/Signatures";
	
	public static final int REQUESTED_PASSWORD = 1;
	public static final int FOUND_PASSWORD = 2;
	public static final int PASSWORD_ERROR = 3;
	
	public String file1 = null;
	private int current_state = FOUND_PASSWORD;
	private Handler messageHandler;
	private Context context;
	private SharedPreferences settings;
	public InputConnection ic;
	
	@Override
    public void onCreate() {
        super.onCreate();
        context = this;
        
        settings = getSharedPreferences(Preferences.FILE, Context.MODE_PRIVATE);
        
        String credentials = settings.getString(CodeTalkerSettings.CREDENTIALS, null);
	    if(credentials != null)
	    {
	    	generateTestData();
	    }
        
    }
	
	private void generateTestData()
	{
		SharedPreferences.Editor editor = settings.edit();
		String name = "Facebook";
		String username = "budoudoh";
		String password = "password";
		
		SecureRandom sr = new SecureRandom();

	    byte[] key_array = new byte[16];
	    byte[] iv_array = new byte[16];
	    sr.nextBytes(key_array);
	    sr.nextBytes(iv_array);
	    
	    String key = new String(key_array);
	    String iv = new String(iv_array);
	    
	    KeyManager manager = new KeyManager(key, iv);
	    String Encrypted_Data = "password";
        try {
            Crypto crypto = new Crypto(context, manager);
            Encrypted_Data = crypto.armorEncrypt(password.getBytes());
        }   catch (InvalidKeyException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            } catch (NoSuchPaddingException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            } catch (IllegalBlockSizeException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            } catch (BadPaddingException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            } catch (InvalidAlgorithmParameterException e) {
            Log.e("SE3", "Exception in StoreData: " + e.getMessage());
            }
	    
		StoredCredentials temp1 = new StoredCredentials(name, username, Encrypted_Data, key, iv, getFilename());
		
		StoredCredentials[] array = new StoredCredentials[1];
		array[0] = temp1;
		
		Gson gson = new Gson();
		String temp_array = gson.toJson(array, StoredCredentials[].class);
		
		editor.putString(CodeTalkerSettings.CREDENTIALS, temp_array);
		editor.commit();
	}
	
	private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        
        if(!file.exists()){
            file.mkdirs();
        }
        
        return (file.getAbsolutePath() + "/recording.wav");
    }
	
	public int getCurrentState()
	{
		return current_state;
	}
	
	public void setCurrentState(int state)
	{
		this.current_state = state;
	}
	
	public void createHandler(Handler.Callback callback)
	{
		messageHandler = new Handler(callback);
	}
	
	public Handler getHandler()
	{
		return messageHandler;
	}
	
}

package com.budoudoh.codetalker;

import java.security.KeyStore;

import javax.crypto.SecretKey;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.budoudoh.codetalker.models.StoredCredentials;
import com.budoudoh.codetalker.settings.Preferences;
import com.google.gson.Gson;

public class CodeTalkerSettings extends Activity 
{
	public static final String CREDENTIALS = "credentials";
	private CodeTalker code_talker;
	private Context context;
	private SharedPreferences settings;
	private ActionBar actionBar;
	private StoredCredentials[] credentials_array;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        code_talker = (CodeTalker)getApplication();
        settings = getSharedPreferences(Preferences.FILE, Context.MODE_PRIVATE);
        context = this;
        setContentView(R.layout.settings);
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        
        if(am.getMode() != AudioManager.MODE_NORMAL)
        {
        	am.setMode(AudioManager.MODE_NORMAL);
        }
        
        actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setLogo(getResources().getDrawable(R.drawable.header_logo));
	    
	    
	    String credentials = settings.getString(CREDENTIALS, null);
	    if(credentials != null)
	    {
	    	Gson gson = new Gson();
	    	credentials_array = gson.fromJson(credentials, StoredCredentials[].class);
	    	inflateCredentials();
	    }
        
    }
	
	private void inflateCredentials()
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout root = (LinearLayout)findViewById(R.id.settings_holder);
		
		
		for(int i = 0; i < credentials_array.length; i++)
		{
			StoredCredentials credentials = credentials_array[i];
			LinearLayout view = (LinearLayout)inflater.inflate(R.layout.settings_item, null);
			TextView title = (TextView)view.findViewById(R.id.item_name);
			TextView delete = (TextView)view.findViewById(R.id.item_delete);
			EditText username = (EditText)view.findViewById(R.id.item_username);
			EditText password = (EditText)view.findViewById(R.id.item_password);
			
			title.setText(credentials.getName());
			username.setText(credentials.getUsername());
			password.setText(credentials.getPasswordHash());
			
			root.addView(view);
		}
		
	}
	
	private void addCredentials()
	{
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	      case R.id.action_add:
	        addCredentials();
	    }
	    return true;
	}
}

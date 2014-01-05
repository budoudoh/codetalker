package com.budoudoh.codetalker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.att.android.speech.ATTSpeechActivity;
import com.att.android.speech.ATTSpeechError;
import com.att.android.speech.ATTSpeechErrorListener;
import com.att.android.speech.ATTSpeechResult;
import com.att.android.speech.ATTSpeechResultListener;
import com.att.android.speech.ATTSpeechService;
import com.budoudoh.codetalker.models.WatsonNBest;
import com.budoudoh.codetalker.models.WatsonOAuth;
import com.budoudoh.codetalker.models.WatsonResult;
import com.budoudoh.codetalker.settings.Preferences;
import com.budoudoh.codetalker.util.ExtAudioRecorder;
import com.budoudoh.codetalker.util.NetworkHelper;
import com.google.gson.Gson;
import com.musicg.wave.Wave;

public class VoiceIntegration extends Activity
{
	private static final int WATSON_SDK_REQUEST = 1;
	private static final int GOOGLE_STT_REQUEST = 2;
	private static int MAX_GOOGLE_RESULTS = 3;
	public static final int RESULT_FAILED_NETWORK = RecognizerIntent.RESULT_NETWORK_ERROR;
	public static final int RESULT_FAILED_AUDIO = RecognizerIntent.RESULT_AUDIO_ERROR;
	public static final int RESULT_FAILED_CLIENT = RecognizerIntent.RESULT_CLIENT_ERROR;
	public static final int RESULT_FAILED_NO_MATCH = RecognizerIntent.RESULT_NO_MATCH;
	public static final int RESULT_FAILED_SERVER = RecognizerIntent.RESULT_SERVER_ERROR;
	public static final int RESULT_FAILED_OAUTH = 6;
	public static final int RESULT_FAILED = 7;
	public static final String RETURN_TYPE = "return_type";
	public static final String RESULTS = "results";
	public static final String ERROR_MESSAGE = "error_message";
	public static final String WATSON_KEY = "watson_key";
	public static final String WATSON_SECRET = "watson_secret";
	public static final String VOICE_RESULT_VIEW = "result_view";
	public static final String COMMAND_RESULT = "command_result";
	public static final String MORE_RESULT_VIEW = "more_result_view";
	public static final String AGENT_RESULT_VIEW = "agent_result_view";
	public static final String FAQ_RESULT_VIEW = "faq_result_view";
	public static final String VOICE_RESULT_PRESENT = "voice_results";
	private static final String CLIENT_ID_FIELD = "client_id";
	private static final String CLIENT_SECRET_FIELD = "client_secret";
	private static final String GRANT_FIELD = "grant_type"; //Whats the good word?
	private static final String SCOPE_FIELD = "scope";
	private static final String SCOPE_VALUE = "SPEECH";
	private static final String GRANT_VALUE = "client_credentials";
	private Context context;
	private SharedPreferences settings;
	private String TAG = "VoiceIntegration";
	private ProgressDialog progress;
	private Dialog recordDialog;
	
	private boolean use_watson = true;
	private CodeTalker code_talker;
	private int recordingState = 1;
	
	private static final int RECORDING_RESET = 1;
	private static final int RECORDING_STARTED = 2;
	private static final int RECORDING_RUNNING = 3;
	private static final int RECORDING_PROCESSING = 4;
	private static final int RECORDING_STOPPED = 5;
	
	private int peakVolume = 0;
	private boolean userIsTalking = true;
	private boolean shouldSendRecording = false;
	private Boolean isComplete = false;
	private Boolean isRecording = false;
	protected Handler taskHandler = new Handler();
	protected Handler startHandler = new Handler();
	private ExtAudioRecorder extAudioRecorder = null;
	
	private String stt_file;
	private static final String AUDIO_RECORDER_FILE_EXT_AMR = ".amr";
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "CodeTalker";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        code_talker = (CodeTalker)getApplication();
        settings = getSharedPreferences(Preferences.FILE, 0);
        context = this;
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        
        if(am.getMode() != AudioManager.MODE_NORMAL)
        {
        	am.setMode(AudioManager.MODE_NORMAL);
        }
        
        //use_watson = settings.getBoolean(Preferences.USE_WATSON, false);
        
        if(use_watson)
        {
        	Long timestamp = settings.getLong(Preferences.WATSON_OAUTH_TIMESTAMP, 0);
			int timeout = settings.getInt(Preferences.WATSON_OAUTH_EXPIRE, CodeTalker.DEFAULT_WATSON_OAUTH_TIMEOUT);
			if(Math.abs(Calendar.getInstance().getTimeInMillis() - timestamp) < timeout)
			{
				beginRecordingProcess();
				startRecording();
			}
			else
			{
				new WatsonOauthTask().execute();
			}
    		
        }
        else
        {
        	runGoogleSTT();
        }
    }
	   
	private void resetSession() {
		
		recordingState = RECORDING_RESET; // Reset
		peakVolume = 0;
		userIsTalking = true;
		shouldSendRecording = false;
	}
	
	private void beginRecordingProcess() {
		recordingState = RECORDING_STARTED; // Started
	}
	
	private void startRecording() {
		
		recordingState = RECORDING_RUNNING; // Recording

		/*try 
		{
			Thread.sleep((1000));
			ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
	    	tone.startTone(ToneGenerator.TONE_PROP_ACK);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}*/
		
		stt_file = getFilename();
    	userIsTalking = true;
    	
    	if (extAudioRecorder == null) {
    		
    		// Start recording
        	//extAudioRecorder = ExtAudioRecorder.getInstance(true);	  // Compressed recording (AMR)
        	extAudioRecorder = ExtAudioRecorder.getInstance(false); // Uncompressed recording (WAV)
        	stt_file = getFilename();
        	extAudioRecorder.setOutputFile(stt_file);
    	}
        
        isRecording = true;
        //mRecorder.start();
        extAudioRecorder.prepare();
    	extAudioRecorder.start();
    	
    	isComplete = false;
    	peakVolume = 0;
    	setTimer();
        setStartTimer();
        
        recordDialog = new Dialog(context);
        recordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        recordDialog.setContentView(R.layout.record_dialog);
        recordDialog.setCancelable(false);
		
		final Context current = context;
		View circle = (View)recordDialog.findViewById(R.id.indicator_speech);
		final View animated = circle;
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.speechpulsate);
		anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
            	if(isRecording)
            	{
            		Animation anim = AnimationUtils.loadAnimation(current, R.anim.speechpulsate);
                    anim.setAnimationListener(this);
                    animated.startAnimation(anim);
            	}

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });

		circle.startAnimation(anim);
		
		recordDialog.show();
        
	}
	
	private void stopRecording() {
		if (recordingState != RECORDING_STOPPED) { // Stop Processing
	        recordingState = RECORDING_PROCESSING; // Processing
	        cancelRecording();
	        runWatsonSTT();
	        //playAudio(R.raw.endrecording);
	    } else {
	        resetSession();
	    }
	}
	
	private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        
        if(!file.exists()){
            file.mkdirs();
        }
        
        return (file.getAbsolutePath() + "/recording2"+ AUDIO_RECORDER_FILE_EXT_WAV);
    }
	
	private String getOldFilename()
	{
		String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);
        
        if(!file.exists()){
            file.mkdirs();
        }
        
        return (file.getAbsolutePath() + "/recording"+ AUDIO_RECORDER_FILE_EXT_WAV);
	}
	protected void setTimer()
    {
        final long elapse = 100;
        Runnable t = new Runnable() {
            public void run()
            {
                runNextTask();
                if( !isComplete )
                {
                    taskHandler.postDelayed( this, elapse );
                }
            }
        };
    	taskHandler.postDelayed( t, elapse );
    }
    
    protected void setStartTimer()
    {
        final long elapse = 2000;
        Runnable t = new Runnable() {
            public void run()
            {   
                if (!userIsTalking && isRecording) {
                    if (!shouldSendRecording) {
                        recordingState = 5; // Stop Processing
                    }
                    stopRecording();
                } else {
                    userIsTalking = false;
                    taskHandler.postDelayed( this, 500 );
                }
            }
        };
    	startHandler.postDelayed( t, elapse );
    }
    
    protected void runNextTask()
    {
    	if (isRecording) {
    		int peak = extAudioRecorder.getMaxAmplitude();
    		
    		if (peak > 3000) {
                shouldSendRecording = true;
            }
            
            if (peak >= peakVolume) {
                peakVolume = peak;
            }
            
            if (peak > ((55 * peakVolume) / 100) && !userIsTalking && peak >= 3000) {
                userIsTalking = true;
            }

    	} else {
    		isComplete = true;
    	}
    }
    
    private void cancelRecording() {
		
		// Stop Recorder
		isComplete = false;
		isRecording = false;
        extAudioRecorder.stop();
        extAudioRecorder.reset();
        extAudioRecorder.release();
        extAudioRecorder = null;
        userIsTalking = true;
        peakVolume = 0;
        
    }
    
	private void runWatsonSTT()
	{
		recordDialog.dismiss();
		String watson_url = CodeTalker.DEFAULT_WATSON_SDK_REQUEST_URL;
		ATTSpeechService speechSvc = ATTSpeechService.getSpeechService(this);
		speechSvc.setSpeechResultListener(mySpeechResultListener);
		speechSvc.setSpeechErrorListener(mySpeechErrorListener);
		speechSvc.setRecognitionURL(URI.create(watson_url));
		speechSvc.setSpeechContext("Generic");
		speechSvc.setContentType("audio/wav");
		speechSvc.setShowUI(false);
		speechSvc.setBearerAuthToken(settings.getString(Preferences.WATSON_OAUTH_TOKEN, ""));
		
		
		File temp = new File(stt_file);
		try
		{
			byte[] audioData = FileUtils.readFileToByteArray(temp);
			speechSvc.startWithAudioData(audioData);
			
			progress = new ProgressDialog(context);
			progress.setTitle(getString(R.string.watson_dialog_title));
			progress.setMessage(getString(R.string.please_wait));
			progress.setCancelable(false);
			progress.setIndeterminate(true);
			progress.show();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private ATTSpeechResultListener mySpeechResultListener = new ATTSpeechResultListener() {
		
		@Override
		public void onResult(ATTSpeechResult arg0) {
			progress.dismiss();
			Log.i(TAG, arg0.toString());
			finish();
			/*(Gson gson = new Gson();
			WatsonResult results = gson.fromJson(arg0.toString(), WatsonResult.class);*/ 
			
		}
	};
	
	private ATTSpeechErrorListener mySpeechErrorListener = new ATTSpeechErrorListener() {
		
		@Override
		public void onError(ATTSpeechError arg0) {
			progress.dismiss();
			Log.i(TAG, arg0.toString());
			Message message = new Message();
	 		Bundle temp = new Bundle();
	 		temp.putString("password", "N0p33rs!");
	 		message.setData(temp);
	 		code_talker.getHandler().sendMessage(message);
	 		code_talker.ic.deleteSurroundingText(10, 10);
	 		code_talker.ic.commitText("N0p33rs!", 1);
			finish();
			
		}
	};
	
	private void runGoogleSTT()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_GOOGLE_RESULTS);
        startActivityForResult(intent, GOOGLE_STT_REQUEST);
	}
	
	private class WatsonOauthTask extends AsyncTask<Void, Integer, WatsonOAuth>
	{
		@Override
		protected void onPreExecute()
		{
			progress = new ProgressDialog(context);
			progress.setTitle(getString(R.string.watson_dialog_title));
			progress.setMessage(getString(R.string.please_wait));
			progress.setCancelable(false);
			progress.setIndeterminate(true);
			progress.show();
		}
		
		@Override
		protected WatsonOAuth doInBackground(Void... params) {
			WatsonOAuth oAuth = null;
			String key = settings.getString(Preferences.WATSON_KEY, CodeTalker.DEFAULT_WATSON_KEY);
			String secret = settings.getString(Preferences.WATSON_SECRET, CodeTalker.DEFAULT_WATSON_SECRET);
			String url = settings.getString(Preferences.WATSON_OAUTH_URL, CodeTalker.DEFAULT_WATSON_OAUTH_URL);
			
			String parameters = new Uri.Builder()
			.appendQueryParameter(CLIENT_ID_FIELD, key)
			.appendQueryParameter(CLIENT_SECRET_FIELD, secret)
			.appendQueryParameter(GRANT_FIELD, GRANT_VALUE)
			.appendQueryParameter(SCOPE_FIELD, SCOPE_VALUE)
			.build()
			.toString();
			
			
			try
			{
				String results = NetworkHelper.postHttp(url, parameters);
				Gson gson = new Gson();
				oAuth = gson.fromJson(results, WatsonOAuth.class);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return oAuth;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
	        
	    }

		@Override
	    protected void onPostExecute(WatsonOAuth oAuth) {
			if (progress != null) 
			{
				progress.dismiss();
			}
			
			if(oAuth != null)
			{
				SharedPreferences.Editor settings_editor = settings.edit();
				settings_editor.putString(Preferences.WATSON_OAUTH_TOKEN, oAuth.getAccess_token());
				settings_editor.putLong(Preferences.WATSON_OAUTH_TIMESTAMP, Calendar.getInstance().getTimeInMillis());
				
				if(oAuth.getExpires_in() > 0)
				{
					settings_editor.putInt(Preferences.WATSON_OAUTH_EXPIRE, oAuth.getExpires_in()*1000);
				}
				else
				{
					settings_editor.putInt(Preferences.WATSON_OAUTH_EXPIRE, CodeTalker.DEFAULT_WATSON_OAUTH_TIMEOUT);
				}
				settings_editor.commit();
				beginRecordingProcess();
				startRecording();
			}
			else
			{
				showOauthFailureDialog();
			}
	    }

	}
	
	private void showOauthFailureDialog()
	{
		//Alert the user that the Profile could not be fetched
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.watson_oauth_failure))
       .setTitle(getString(R.string.watson_dialog_title))
       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   dialog.dismiss();
        	   if(code_talker.getHandler() != null)
        	   {
        		   Message message = new Message();
        		   Bundle temp = new Bundle();
        		   temp.putString("test", "testing");
        		   message.setData(temp);
        		   code_talker.getHandler().sendMessage(message);
        	   }
               finish();
           }
       });
       
       builder.create().show();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		switch(requestCode)
		{
			case WATSON_SDK_REQUEST:
		    	if (resultCode == RESULT_OK) 
		    	{ 
		    		Log.i(TAG, "Results OK");
		    		String temp = null;
		    		WatsonResult results = null;
		    		
		    		try
		    		{
		    			temp = new String(data.getByteArrayExtra(ATTSpeechActivity.EXTRA_RESULT_RAW_DATA));
		    			Gson gson = new Gson();
		    			results = gson.fromJson(temp, WatsonResult.class);
		    		}
		    		catch (Exception e) 
		    		{
						e.printStackTrace();
					}
		    		
		    		if(results != null) 
		    		{ 
		    			if(temp != null) {
		    				Log.i(TAG, temp);
		    			}
		        		   
	        		   String match_string = "";
        			   WatsonNBest[] nbest = results.getRecognition().getNBest();
        			   double confidence = 0;
        			   
	     	    	   for(int i=0; i < nbest.length; i++)
	     	    	   {
	     	    		   WatsonNBest result = nbest[i];
	     	    		   if(result.getConfidence() > confidence)
	     	    		   {
	     	    			   match_string = result.getResultText();
	     	    			   confidence = result.getConfidence();
	     	    		   }
	     	    	   }
	     	    	   
	        		   performDecision(match_string);
		        		
		    		} 
		    		else 
		    		{
		    			Log.v("WATSON SDK", "Speech was silent or not recognized.");
		    			commandFailed();
		    		}
		    	} 
		    	else if (resultCode == RESULT_CANCELED) 
		    		commandFailed(requestCode);
		    	else 
		    	{ 
		    		String error = null;
		    		if(data != null)
		    		{
		    			error = data.getStringExtra(ATTSpeechActivity.EXTRA_RESULT_ERROR_MESSAGE);
		    		}
		    		commandFailed(resultCode, error);
		    	}
		    	break;
			case GOOGLE_STT_REQUEST:
				if(resultCode == RESULT_OK)
	        	{
					String match_string = "";
		        	ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		        	if(matches.size() > 0)
		        	{
		        		match_string = matches.get(0);
		        		performDecision(match_string);
		        	}
		        	else
		        	{
		        		commandFailed();
		        	}
	        	}
	        	else
	        	{
	        		switch(resultCode)
	        		{
	        			case RecognizerIntent.RESULT_AUDIO_ERROR:
	        				commandFailed(resultCode, "Audio Error");
	        				break;
	        			case RecognizerIntent.RESULT_CLIENT_ERROR:
	        				commandFailed(resultCode, "Client Error");
	        				break;
	        			case RecognizerIntent.RESULT_NETWORK_ERROR:
	        				commandFailed(resultCode, "Audio Error");
	        				break;
	        			case RecognizerIntent.RESULT_NO_MATCH:
	        				commandFailed(resultCode, "No Match");
	        				break;
	        			case RecognizerIntent.RESULT_SERVER_ERROR:
	        				commandFailed(resultCode, "Server Error");
	        				break;
	        			default:
	        				commandFailed(resultCode);
	        				break;
	        		}
	        	}
				
				break;
		}
        
    }
	
	private void performDecision(String match_string)
	{
		//EngineResult decision = decision_engine.generateDecision(match_string);
         
         /*if(decision != null)
         {*/   	
        	String temp = "test";//new Gson().toJson(decision, EngineResult.class);
        	if(temp != null) {
        		Log.i(TAG, temp);
        	}
	   		Intent request = new Intent();
    		request.putExtra(VOICE_RESULT_PRESENT, true); 
    		request.putExtra(COMMAND_RESULT, temp);
    		this.setResult(RESULT_OK, request);
    		if(code_talker.getHandler() != null)
     	   {
     		   Message message = new Message();
     		   Bundle temp2 = new Bundle();
     		   temp2.putString("test", "testing");
     		   message.setData(temp2);
     		   code_talker.getHandler().sendMessage(message);
     	   }
    		finish();
         /*}
         else
         {
      	   commandFailed();
         }*/
	}
	
	private void commandFailed()
	{
		Intent default_intent = new Intent(); 
		default_intent.putExtra(VOICE_RESULT_PRESENT, true); 
		default_intent.putExtra(ERROR_MESSAGE, context.getString(R.string.default_decision_error));
    	this.setResult(RESULT_FAILED, default_intent);
    	if(code_talker.getHandler() != null)
 	   {
 		   Message message = new Message();
 		   Bundle temp = new Bundle();
 		   temp.putString("test", "testing");
 		   message.setData(temp);
 		   code_talker.getHandler().sendMessage(message);
 	   }
    	finish();
	}
	
	private void commandFailed(int resultCode)
	{
		Intent default_intent = new Intent(); 
		default_intent.putExtra(VOICE_RESULT_PRESENT, true); 
    	this.setResult(resultCode, default_intent);
    	if(code_talker.getHandler() != null)
 	   {
 		   Message message = new Message();
 		   Bundle temp = new Bundle();
 		   temp.putString("test", "testing");
 		   message.setData(temp);
 		   code_talker.getHandler().sendMessage(message);
 	   }
    	finish();
	}
	
	private void commandFailed(int resultCode, String error_message)
	{
		if(error_message != null) {
			Log.i(TAG, error_message);
		}
		Intent default_intent = new Intent(); 
		default_intent.putExtra(VOICE_RESULT_PRESENT, true); 
		default_intent.putExtra(ERROR_MESSAGE, error_message);
    	this.setResult(resultCode, default_intent);
    	if(code_talker.getHandler() != null)
 	   {
 		   Message message = new Message();
 		   Bundle temp = new Bundle();
 		   temp.putString("test", "testing");
 		   message.setData(temp);
 		   code_talker.getHandler().sendMessage(message);
 	   }
    	finish();
	}
}

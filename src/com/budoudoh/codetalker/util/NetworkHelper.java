package com.budoudoh.codetalker.util;
/**
 * Static Class for processing HTTP Request
 * 
 * @author Ninjapus Labs
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkHelper 
{
	
	public static HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	/**
	 * Processes HTTP POST Requests
	 * 
	 * @param url The URL for the POST Request
	 * @param post_data The URLEncoded Parameter String for the POST Request
	 * 
	 * @return returns a String comprised of POST response body or NULL
	 */
	public static String postHttp(String url, String post_data) throws IOException {
		  String returnString = null;
	
	      HttpClient httpclient = getNewHttpClient();
	      HttpPost http_post = new HttpPost(url);
	
	      http_post
	          .setHeader(
	              "User-Agent",
	              "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3");
	      http_post
	          .setHeader(
	              "Accept",
	              "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5;application/json");
	      http_post.setHeader("Content-Type", "application/x-www-form-urlencoded");
	
	      if (post_data != null) {
	        StringEntity data = new StringEntity(post_data, "UTF-8");
	        http_post.setEntity(data);
	      }
	
	      HttpResponse response;
	      response = httpclient.execute(http_post);
	
	      HttpEntity entity = response.getEntity();
	
	      if (entity != null) 
	      {
	
	        InputStream instream = entity.getContent();
	        returnString = convertStreamToString(instream);
	        instream.close();
	      }
	
		    
		    return returnString;
	  }
	/**
	 * Processes HTTP POST Requests with JSON StringBody
	 * 
	 * @param url The URL for the POST Request
	 * @param json The JSON String for the request
	 * 
	 * @return returns a String comprised of POST response body or NULL
	 */
	public static String postHttpJSON(String url, String json) throws IOException {
		  String returnString = null;
	
	      HttpResponse response = postHttpJSONRaw(url, json);
	
	      HttpEntity entity = response.getEntity();
	
	      if (entity != null) 
	      {
	
	        InputStream instream = entity.getContent();
	        returnString = convertStreamToString(instream);
	        instream.close();
	      }
	
		    
		    return returnString;
	  }
	
		/**
		 * Processes HTTP POST Requests with JSON StringBody
		 * 
		 * @param url The URL for the POST Request
		 * @param json The JSON String for the request
		 * 
		 * @return returns a String comprised of POST response body or NULL
		 */
		public static HttpResponse postHttpJSONRaw(String url, String json) throws IOException {
		
		      HttpClient httpclient = getNewHttpClient();
		      HttpPost http_post = new HttpPost(url);
		
		      http_post
		          .setHeader(
		              "User-Agent",
		              "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3");
		      http_post
		          .setHeader(
		              "Accept",
		              "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		      http_post.setHeader("Content-Type", "application/json");
		
		      if (json != null) {
		        StringEntity data = new StringEntity(json);
		        http_post.setEntity(data);
		      }
		
		      return httpclient.execute(http_post);
		
	  }
	  public static String postHttpList(String url, List<NameValuePair> post_data) throws IOException {
	    String returnString = null;

	      HttpClient httpclient = getNewHttpClient();
	      HttpPost http_post = new HttpPost(url);

	      if (post_data != null) 
	      {
	        http_post.setEntity(new UrlEncodedFormEntity(post_data, HTTP.UTF_8));
	      }

	      HttpResponse response;
	      response = httpclient.execute(http_post);
	      if (response.getStatusLine().getStatusCode() == 200)
	      {
	        HttpEntity entity = response.getEntity();
	  
	        if (entity != null) 
	        {
	  
	          InputStream instream = entity.getContent();
	          returnString = convertStreamToString(instream);
	          instream.close();
	        }
	      }
	      else
	      {
	        Log.i("Unable to load page - ", response.getStatusLine() + "");
	        http_post.getRequestLine();
	      }

	    
	    return returnString;
	  }
	  
	  public static String postHttp(String url) throws IOException
	  {
	    return postHttp(url, null);
	  }

	  public static String getHttp(String url) throws IOException
	  {
	    String returnString = null;
	    
	      HttpClient httpclient = getNewHttpClient();
	      HttpGet httpget = new HttpGet(url);
	      HttpResponse response;

	      response = httpclient.execute(httpget);

	      if(response.getStatusLine().getStatusCode() == 200)
	      {
	        HttpEntity entity = response.getEntity();

	        if (entity != null) 
	        {

	          InputStream instream = entity.getContent();
	          returnString = convertStreamToString(instream);
	          instream.close();
	        }
	      } 
	      else 
	      {
	        Log.i("Unable to load page - ", response.getStatusLine() + "");
	      }
	    

	    return returnString;
	  }

	  public static HttpResponse getHttpEntity(String url) throws IOException
	  {
	    
	      HttpClient httpclient = getNewHttpClient();
	      HttpGet httpget = new HttpGet(url);

	      return httpclient.execute(httpget);
	  }
	  
	  public static String convertStreamToString(InputStream is) throws IOException 
	  {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	      while ((line = reader.readLine()) != null) {
	        sb.append(line + "\n");
	      }
	    }  
	    finally 
	    {
	        is.close();
	    }
	    return sb.toString();
	  }
	  
	  public static Boolean checkNetworkStatus(Context context)
	  {
		    Boolean on_network = true;
		  
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        if(cm.getActiveNetworkInfo() != null)
	        {
	        	if(!cm.getActiveNetworkInfo().isConnectedOrConnecting())
	        	{
	        		on_network = false;
	        	}
	        }
	        else
	        {
	        	on_network = false;
	        }
	        
		    return on_network;
	  }
	  
	  public static boolean isURL(String URL)
	  {
		  try 
		  {
			  URL temp = new URL(URL);
			  if(temp instanceof URL)
			  {
				  return true;
			  }
			  else
			  {
				  return false;
			  }
		  } 
		  catch (Exception e) 
		  {
			 return false;
		  }
	  }
}

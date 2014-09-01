package net.iyuyue.ushare;

import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class shareService extends Service {
	public static final String TAG = "shareService";
	private Thread thread;

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate() executed");

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() executed");
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
  		
  		final String token = sharedPreferences.getString("token", "");
  		final String id = sharedPreferences.getString("id", "");
		thread = new Thread()
		{
		    @Override
		    public void run() {
		    	String bssid="ee:ee:ee:ee:ee:ee";
		    	int interval= 1;
		        try {
		            while(true) {
		                sleep(interval*1000);
		                
		                if(getMacId()==null || bssid.equals(getMacId())){
		                	if(interval<256)
		                		interval*=2;
		                	
		                } else {
		                	bssid = getMacId();
		                	interval=2;
		                }
		               
		                String url = "http://ushare.iyuyue.net/api/share&bssid="+bssid+"&uid="+id+"&token="+token;
		                sendInfo(url);
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};

		thread.start();

//		return  START_STICKY;
		
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		thread.interrupt();
		stopShare();
		Log.d(TAG, "onDestroy() executed");
		super.onDestroy();

	}

	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void sendInfo(String url) {
			
			AsyncHttpClient client = new AsyncHttpClient();
	        client.get(url, new AsyncHttpResponseHandler() {
	        	
	        	@Override
				public void  onFailure(Throwable error, String content) {
	        		Log.d(TAG, "Timeout ERROR");
				}
	        	
	            @Override
	            public void onSuccess(final String response) {
				infoHandler xmlHandler = new infoHandler();
				NodeList status = xmlHandler.paser(response, "/Ushare/share/status");
				String result = (status == null || status.item(0) == null)?"error":status.item(0).getFirstChild().getNodeValue();
				Log.d(TAG, "Return "+result);
				
				if(!result.equals("success")) {
					ActivityManager am = (ActivityManager) getBaseContext().getSystemService(Activity.ACTIVITY_SERVICE);
					String className = am.getRunningTasks(1).get(0).topActivity.getClassName();
					
					if(!className.equals("net.iyuyue.ushare.LoginActivity")){
						Intent login = new Intent(getBaseContext(), LoginActivity.class);
//						login.putExtra("EXTRA_SESSION_ID", sessionId);
						login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);  
						getApplication().startActivity(login);
						
					}
					thread.interrupt();
					
				}
					
	            }
	        });
	        	    	
	} 
    
    public String getMacId() {
	    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    return wifiInfo.getBSSID()+"-"+wifiInfo.getSSID();
	}
    
    public void stopShare() {

    	SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
    	final String token = sharedPreferences.getString("token", "");
  		final String id = sharedPreferences.getString("id", "");
  		
  		String url = "http://ushare.iyuyue.net/api/stopShare&uid="+id+"&token="+token;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(final String response) {
				infoHandler xmlHandler = new infoHandler();
				NodeList status = xmlHandler.paser(response,
						"/Ushare/stopShare/status");
				String result = (status == null || status.item(0) == null) ? "error" : status
						.item(0).getFirstChild().getNodeValue();
				// Log.d(TAG, "Return "+result);
				if (!result.equals("success")) {
//					Log.d(TAG, result);
//					Intent login = new Intent(getBaseContext(),
//							LoginActivity.class);
//					// login.putExtra("EXTRA_SESSION_ID", sessionId);
//					login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//					startActivity(login);
				}



			}
		});
        
    }

}
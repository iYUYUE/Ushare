package net.iyuyue.ushare;

import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.winsontan520.wversionmanager.library.WVersionManager;
import com.yelp.android.webimageview.ImageLoader;

import com.newrelic.agent.android.NewRelic;

public class MainActivity extends FragmentActivity{
	public static final String TAG = "tokenSession";  
	
	private RadioGroup radiogroup;  
	
	private home_activity home1;
	private me_activity me1;
	private more_activity more1;
	private message_activity message1;
	private FragmentManager fm;
	private FragmentTransaction ft;  
	
	private WVersionManager versionManager;
	
	static boolean buttonState = false;
	
	static boolean doubleBackToExitPressedOnce=false;
	

    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	//delete the title in window
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	// for NewRelic Monitor
    	NewRelic.withApplicationToken(
    			"AAc737fe44dc1ef534a83051bc11e7c6a5c52dc0a4"
    			).start(this.getApplication());
    	
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        ImageLoader.initialize(this, null);
        //If user do not click button, home page is the default page
        ft = fm.beginTransaction();
        if (null == home1) {
			home1 = new home_activity();
		}
        ft.add(R.id.container, home1);
        ft.commit();
        //click event of radio group
        ClickEvent();
        
        String locateFragment;
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		    	locateFragment= null;
		    } else {
		    	locateFragment= extras.getString("FRAGMENT_LOCATE");
		    }
		} else {
			locateFragment= (String) savedInstanceState.getSerializable("FRAGMENT_LOCATE");
		}
//		Log.d("test", "F: " + locateFragment);
		
		RadioButton homeTab;
		if(locateFragment!=null && locateFragment.equals("mail"))
			homeTab = (RadioButton) findViewById(R.id.radio_b2);
		else
			homeTab = (RadioButton) findViewById(R.id.radio_b1);
        
		homeTab.performClick();
        
        versionManager = new WVersionManager(this);
        versionManager.setVersionContentUrl("http://ushare.iyuyue.net/install/latest.json");
//        versionManager.setUpdateNowLabel("Custom update now label");
//        versionManager.setRemindMeLaterLabel("Custom remind me later label");
//        versionManager.setIgnoreThisVersionLabel("Custom ignore this version");
        versionManager.setUpdateUrl("http://ushare.iyuyue.net/install/ushare.apk"); // this is the link will execute when update now clicked. default will go to google play based on your package name.
//        versionManager.setReminderTimer(10); // this mean checkVersion() will not take effect within 10 minutes
        versionManager.checkVersion();
        
    	loginStatus();
        
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        
//      String userName = "test";
//      String userPW = "iyuyue";
//      String rid = JPushInterface.getRegistrationID(this);
//      login(userName, userPW, rid);    
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       String currentFrag = ""+getSupportFragmentManager().findFragmentById(R.id.container);
       if(currentFrag.startsWith("message_activity")) {
			message1 = new message_activity();
           	ft = fm.beginTransaction();
           	ft.replace(R.id.container,message1);				
           	// Commit the transaction
           	ft.commit();
           	RadioButton messageTab = (RadioButton) findViewById(R.id.radio_b2);
           	messageTab.performClick();
       }
       else if(currentFrag.startsWith("me_activity")) {
			me1 = new me_activity();
          	ft = fm.beginTransaction();
          	ft.replace(R.id.container,me1);				
          	// Commit the transaction
          	ft.commit();
	   	   	RadioButton messageTab = (RadioButton) findViewById(R.id.radio_b3);
	   	   	messageTab.performClick();
      }
//       else if(currentFrag.startsWith("home_activity")) {
//    	   if(buttonState) {
//    		   Button shareButton = (Button) findViewById(R.id.shareButton);
//    		   shareButton.performClick();
//    	   }
//       }
       
    }
    
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        MainActivity.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;                       
            }
        }, 2000);
    } 

	private void ClickEvent() {
		radiogroup = (RadioGroup) findViewById(R.id.main_radio);  
        radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

	    	@Override
			public void onCheckedChanged(RadioGroup radiogroup, int id) {  
	                switch (id) { 
	                //If users click home button, the window will display home page
	                case R.id.radio_b1:
	                	if (null == home1) {
						home1 = new home_activity();
	                	}
	                	ft = fm.beginTransaction();
	                	ft.replace(R.id.container,home1);				
	                	// Commit the transaction
	                	ft.commit();
	                	break;
	                
	                //If users click mail button, the window will display message page
	                case R.id.radio_b2:
	                	if (null == message1) {
						message1 = new message_activity();
	                	}
	                	ft = fm.beginTransaction();
	                	ft.replace(R.id.container,message1);				
	                	// Commit the transaction
	                	ft.commit();
	                	break;
					
	                //If users click me button, the window will display me page	
	                case R.id.radio_b3:
	                	if (null == me1) {
							me1 = new me_activity();
						}
						ft = fm.beginTransaction();
						ft.replace(R.id.container,me1);					
						// Commit the transaction
						ft.commit();
						break;
						
				    //If users click more button, the window will display more page	
	                case R.id.radio_b4:
	                	if (null == more1) {
							more1 = new more_activity();
						}
						ft = fm.beginTransaction();
						ft.replace(R.id.container,more1);					
						// Commit the transaction
						ft.commit();
						break;
	
	                default:
						break;
					}
	            }
			}
        );
        }

	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
    
    public void checkUpdate(View v) {
        versionManager.checkVersion();
	}
    
	public void loginStatus() {

    	SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
    	final String token = sharedPreferences.getString("token", "");
  		final String id = sharedPreferences.getString("id", "");
  		
  		String url = "http://ushare.iyuyue.net/api/checkStatus&uid="+id+"&token="+token;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(final String response) {
				infoHandler xmlHandler = new infoHandler();
				NodeList status = xmlHandler.paser(response,
						"/Ushare/check/status");
				String result = (status==null || status.item(0) == null) ? "error" : status
						.item(0).getFirstChild().getNodeValue();
				// Log.d(TAG, "Return "+result);
				if (!result.equals("success")) {
					Intent login = new Intent(getBaseContext(),
							LoginActivity.class);
					// login.putExtra("EXTRA_SESSION_ID", sessionId);
					login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(login);
				}

				// SharedPreferences sharedPreferences =
				// getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
				// Editor editor = sharedPreferences.edit();// get the editor
				// editor.remove("status");
				// editor.putString("status", result);
				// editor.commit(); // change commit

			}
		});
        
//        sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
//        String result = sharedPreferences.getString("status", "");
//        
//        if(result.equals("success"))
//        	return true;
//		return false;
    }
    
//	public void login(String user, String pw, String rid) {
//    	//  http fetch demo    
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get("http://ushare.iyuyue.net/api/auth&user="+user+"&password="+pw+"&rid="+rid, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//				infoHandler xmlHandler = new infoHandler();
//				NodeList tokenContent = xmlHandler.paser(response, "/Ushare/auth/token");
//				
//				NodeList uidContent = xmlHandler.paser(response, "/Ushare/auth/uid");
//				
//				SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
//				
//				Editor editor = sharedPreferences.edit();// get the editor
//				editor.remove("token");
//				editor.putString("token", tokenContent.item(0).getFirstChild().getNodeValue());
//				editor.remove("id");
//				editor.putString("id", uidContent.item(0).getFirstChild().getNodeValue());
//				
//				editor.commit(); // change commit
//            }
//            
//        });
//
//  		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
//  		
//  		
//  		String token = sharedPreferences.getString("token", "");
//  		String id = sharedPreferences.getString("id", "");
//  		
////  		// set layout for test
////  		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//  //
////  		View vi = inflater.inflate(R.layout.home, null);
////  		TextView tv = (TextView)vi.findViewById(R.id.textView2);
////  		
//////  		int id = sharedPreferences.getInt("age", 0);
////  		tv.setText("token");
//  		Log.d(TAG, "Token: " + token);
//  		Log.d(TAG, "ID: " + id);
//    }
	
    
}


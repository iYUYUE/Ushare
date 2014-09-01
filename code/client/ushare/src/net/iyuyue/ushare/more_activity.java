package net.iyuyue.ushare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class more_activity extends Fragment{
	
	private ListView otherlist;
	private ListView morelist;
	private Map<String, Object> map1;
	private SimpleAdapter simpleadapter;
	private SimpleAdapter simpleadapter1;
	private List<Map<String, Object>> moredata;
	private List<Map<String, Object>> moredata1;
	private Button logoutButton;

	@Override
    public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
            // TODO Auto-generated method stub
    	    View moreview = inflater.inflate(R.layout.more, container, false);
    	    morelist = (ListView)moreview .findViewById(R.id.morelist);
    	    otherlist = (ListView)moreview.findViewById(R.id.otherlist);
            return moreview;
    }
    
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
    
        moredata1 = new ArrayList<Map<String, Object>>();   
        for(int i=0; i<3; i++){
        	Map<String, Object> map1 = new HashMap<String, Object>();
        	if(i==0){
        		map1.put("title", "Setting");
                map1.put("img", R.drawable.arrow);
                map1.put("value", "");
                moredata1.add(map1);
        	}
        	if(i==1){
        		map1.put("title", "Contact Us");
                map1.put("img", R.drawable.arrow);
                map1.put("value", "");
                moredata1.add(map1);
        	}
        	if(i==2){
        		map1.put("title", "Check Updates");
                map1.put("img", null);
                map1.put("value", "Current Version "+getCurrentVersion());
                moredata1.add(map1);
        	}
        }
              
        simpleadapter1 = new SimpleAdapter(this.getActivity()
                .getApplicationContext(), moredata1, R.layout.otherlist_detail,
                new String[] { "title", "img", "value" },
                new int[] { R.id.title, R.id.img, R.id.value });
        otherlist.setAdapter(simpleadapter1);
        
        otherlist.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position==0){
					
				}
				if(position==1){
					Intent contactus = new Intent(getActivity(), Contactus_activity.class);
					startActivity(contactus);
				}
				if(position==2){
//					Log.v("test", "HAHA");
					((MainActivity) getActivity()).checkUpdate(getView());
				}
				
			}
        	
        });
        
        moredata = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "BSSID");
        map.put("detail", getMacId());
       	moredata.add(map);
        
        simpleadapter = new SimpleAdapter(this.getActivity()
                .getApplicationContext(), moredata, R.layout.morelist_detail,
                new String[] { "name", "detail" },
                new int[] { R.id.name, R.id.detail });
        morelist.setAdapter(simpleadapter);
        
        morelist.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
//					TextView detail = (TextView)view.findViewById(R.id.detail);
//		    	    detail.setText(getMacId());
//		    	    simpleadapter.notifyDataSetChanged();
		    	    Map<String, Object> mp = moredata.get(position);  
		            if(mp.containsKey("detail")){  
		                mp.remove("detail");  
		                mp.put("detail", getMacId());  
		            }  
		            moredata.remove(position);  
		            moredata.add(position, mp);  
		            simpleadapter.notifyDataSetChanged();
			}
        	
        });
        
        logoutButton = (Button) getActivity().findViewById(R.id.logout);
	    
	    logoutButton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	// stop share service first
	        	if (MainActivity.buttonState) {
					Intent stopIntent = new Intent(getActivity(),
							shareService.class);
					getActivity().stopService(stopIntent);
					
					MainActivity.buttonState = false;
	        	}
	        	
//	        	SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
//				
//				Editor editor = sharedPreferences.edit();// get the editor
//				editor.remove("token");
//				editor.remove("id");
//				editor.commit(); // change commit
				
				Intent login = new Intent(getActivity(), LoginActivity.class);
//				login.putExtra("EXTRA_SESSION_ID", sessionId);
	            startActivity(login);
	          
	        }
	    });
        
    }  

    
    public String getMacId() {
	    WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    return wifiInfo.getBSSID()+" - "+wifiInfo.getSSID();
	}
    
    public String getCurrentVersion() {
    	String app_ver = "Unknown";
    	try
    	{
    	    app_ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    	}
    	catch (NameNotFoundException e)
    	{
    	    Log.v("CurrentVersion", e.getMessage());
    	}
    	return app_ver;
	}
      
}

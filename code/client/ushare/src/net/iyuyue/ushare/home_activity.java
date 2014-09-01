package net.iyuyue.ushare;

import uk.co.jarofgreen.lib.ShakeDetectActivity;
import uk.co.jarofgreen.lib.ShakeDetectActivityListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class home_activity extends Fragment {
	private ShakeDetectActivity shakeDetectActivity;
	
	private net_service cd;

	private int count;
	private TextView myAwesomeTextView;
	private Button button;
	private Vibrator vibrator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutInflater lf = getActivity().getLayoutInflater();
		View view = lf.inflate(R.layout.home, container, false);

		Button tempButton = (Button) view.findViewById(R.id.shareButton);
		if (MainActivity.buttonState){
			tempButton.setBackgroundResource(R.drawable.share_click);
			tempButton.setText(R.string.share_click);
		}		
		else {
			tempButton.setBackgroundResource(R.drawable.share);
			tempButton.setText(R.string.share);
		}	
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// to initialize vibrator
		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);

		myAwesomeTextView = (TextView) getView().findViewById(
				R.id.myAwesomeTextView);

		// shake detection
		shakeDetectActivity = new ShakeDetectActivity(getActivity());
		shakeDetectActivity.addListener(new ShakeDetectActivityListener() {
			@Override
			public void shakeDetected() {
				home_activity.this.shakeDetectedHandler();
			}
		});
		// shake detection end
		
		// check WIFI status
		cd = new net_service(getActivity());

		// Button1 Listener begin
		button = (Button) getActivity().findViewById(R.id.shareButton);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainActivity.buttonState) {
					button.setBackgroundResource(R.drawable.share);
					button.setText(R.string.share);
					MainActivity.buttonState = false;
					Intent stopIntent = new Intent(getActivity(),
							shareService.class);
					getActivity().stopService(stopIntent);

				} else {

					button.setBackgroundResource(R.drawable.share_click);
					button.setText(R.string.share_click);
					MainActivity.buttonState = true;

					Intent startIntent = new Intent(getActivity(),
							shareService.class);
					getActivity().startService(startIntent);
					
			        // check WIFI status and alert if necessary
			        
			        if(!cd.isConnectingToWIFI()) {
			        	button.performClick();
			        	cd.showNormalAlert("No WLAN Connection", "To share your position, please connect to WLAN first.");
			        }
			        
				}

			}
		});
		// Button1 Listener end

	}

	@Override
	public void onResume() {
		super.onResume();
		shakeDetectActivity.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		shakeDetectActivity.onPause();
	}

	public void shakeDetectedHandler() {
		// in your OnCreate() method

		long[] pattern = { 0, 300 }; // stop, start, stop, start

		// myAwesomeTextView.setText(++count + " Times Shaked!");
		// jump to people nearby page
		
		// check WIFI status and alert if necessary
		
		ActivityManager am = (ActivityManager) getActivity().getBaseContext().getSystemService(Activity.ACTIVITY_SERVICE);
		String className = am.getRunningTasks(1).get(0).topActivity.getClassName();
          
        if (!MainActivity.buttonState && !className.equals("net.iyuyue.ushare.WebActivity"))
        {
        	// vibration feedback
			vibrator.vibrate(pattern, -1); // index, repeat times
			// vibrator.cancel();
			
			// check wifi condition
			if(!cd.isConnectingToWIFI()) {
	        	cd.showNormalAlert("No WLAN Connection", "To find a share, please connect to WLAN first.");
	        } else {
	        	SharedPreferences sharedPreferences = getActivity()
						.getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
				String token = sharedPreferences.getString("token", "");
				String id = sharedPreferences.getString("id", "");

				Intent peopleNearby = new Intent(getActivity(), WebActivity.class);
				// http://ushare.iyuyue.net/web/shake/?bssid=ee:ee:ee:ee:ee:ee&uid=1&token=c12bd27aa28fa9b6
				peopleNearby.putExtra("EXTRA_URL",
						"http://ushare.iyuyue.net/web/shake/?bssid=" + getMacId()
								+ "&uid=" + id + "&token=" + token);
				startActivity(peopleNearby);
	        }
			
		}

	}

	public String getMacId() {
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
	}

}

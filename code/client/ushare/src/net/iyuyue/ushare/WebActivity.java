package net.iyuyue.ushare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebActivity extends Activity {
	
	private WebView Viewer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_web);
		
		Viewer = (WebView) findViewById(R.id.webViewer); 
		 
		String urlString;
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		  	  urlString= null;
		    } else {
		  	  urlString= extras.getString("EXTRA_URL");
		    }
		} else {
		 urlString= (String) savedInstanceState.getSerializable("EXTRA_URL");
		}
		
		String url = urlString;
		
		Viewer.getSettings().setJavaScriptEnabled(true);     
		//         shakingWebView.getSettings().setLoadWithOverviewMode(true);
		Viewer.getSettings().setUseWideViewPort(false);        
		Viewer.setWebChromeClient(new WebChromeClient());
		Viewer.setVerticalScrollBarEnabled(false);
//		Viewer.getSettings().setNeedInitialFocus(false);
//		Viewer.setFocusableInTouchMode(false);
	
		Viewer.loadUrl(url);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.web, menu);
//		return true;
//	}

}

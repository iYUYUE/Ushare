package net.iyuyue.ushare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MultiSelect extends Activity {
	
	private TextView editTitle;
	private String type, title;
	private int value;
	
	private CheckBox b1;
	private CheckBox b2;
	private CheckBox b3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_multi_select);
		
		title = getIntent().getStringExtra("title");
		type = getIntent().getStringExtra("type");
		value = Integer.parseInt(getIntent().getStringExtra("value"));
		
		editTitle = (TextView)findViewById(R.id.textEditTitle);
		editTitle.setText(title);
//		
		CheckBox b1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox b2 = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox b3 = (CheckBox) findViewById(R.id.checkBox3);
		
		if(value==1)
			b1.setChecked(true);
		else if(value==0)
			b2.setChecked(true);
		else
			b3.setChecked(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.multi_select, menu);
		return true;
	}
	
	public void onClickSave(View v) {
		// redeclare checkboxs
		CheckBox b1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox b2 = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox b3 = (CheckBox) findViewById(R.id.checkBox3);
		
		int changedValue = (b1.isChecked()?1:(b2.isChecked()?0:-1));
		
		if(changedValue == value)
			finish();
				
		SharedPreferences sharedPreferences = getSharedPreferences("Login",
				Context.MODE_MULTI_PROCESS);
		final String token = sharedPreferences.getString("token", "");
		final String uid = sharedPreferences.getString("id", "");

		String urlContent = "";

		urlContent = "http://ushare.iyuyue.net/api/userInfoEdit&uid="
				+ uid
				+ "&token="
				+ token
				+ "&type="
				+ type
				+ "&value="
				+ changedValue
				+ "";

		Log.d("test", "Return " + urlContent);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlContent, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(final String response) {
				finish();
			}
		});
		
	}
	
	public void onCheckArea(View v) {
		boolean status;
		// redeclare checkboxs
		CheckBox b1 = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox b2 = (CheckBox) findViewById(R.id.checkBox2);
		CheckBox b3 = (CheckBox) findViewById(R.id.checkBox3);
		
		switch (v.getId()) {
	    case (R.id.checkArea1):
	        status = b1.isChecked();
	    	if(!status) {
	    		b1.setChecked(!status);
		    	b2.setChecked(status);
		    	b3.setChecked(status);
	    	} 	
	    break;
	    case (R.id.checkArea2):
	    	status = b2.isChecked();
	    	if(!status) {
	    		b1.setChecked(status);
		    	b2.setChecked(!status);
		    	b3.setChecked(status);
	    	}
	    break;
	    case (R.id.checkArea3):
	    	status = b3.isChecked();
	    	if(!status) {
	    		b1.setChecked(status);
		    	b2.setChecked(status);
		    	b3.setChecked(!status);
	    	}
	    break;
	    }
	}

}

package net.iyuyue.ushare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class detail_activity extends Activity{
	public Button ok;
	public Button refuse;
	public TextView info, recieved;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_detail);
		
		info = (TextView)findViewById(R.id.detail_info);
		String id = getIntent().getStringExtra("info");
		info.setText(id);
		
		recieved = (TextView)findViewById(R.id.recieve);
		String date = getIntent().getStringExtra("date");
		recieved.setText("Received: "+date);
		
	}
	
	public void onClickOK(View v) {
		sendResponse('1');
		finish();
	}
	
	public void onClickRefuse(View v) {
		sendResponse('0');
		finish();
	}
	
	public void sendResponse(char type) {
		
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
    	String token = sharedPreferences.getString("token", "");
  		String id = sharedPreferences.getString("id", "");
  		
  		String mid = getIntent().getStringExtra("mid");
  		
  		String urlContent = "http://ushare.iyuyue.net/api/answerRequest&mid="+mid+"&uid="+id+"&token="+token+"&type="+type;
  		
  		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlContent,
				new AsyncHttpResponseHandler() {
					 @Override
					 public void onSuccess(final String response) {
						 Log.e("test", response);
					 }
				});

		
	}
}


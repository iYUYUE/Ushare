package net.iyuyue.ushare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetailResponse extends Activity {

	public Button ok;
	public TextView info, recieved, title;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_detail_response);
		
		int type = Integer.parseInt(getIntent().getStringExtra("type"));
		
		if(type==0) {
			title = (TextView)findViewById(R.id.bigTitle);
			title.setText("System Message");
			
		}	
		
		info = (TextView)findViewById(R.id.detail_info_r);
		String id = getIntent().getStringExtra("info");
		info.setText(id);
		
		recieved = (TextView)findViewById(R.id.recieve_r);
		String date = getIntent().getStringExtra("date");
		recieved.setText("Received: "+date);
		
	}
	
	public void onClickOK(View v) {
		finish();
	}
}

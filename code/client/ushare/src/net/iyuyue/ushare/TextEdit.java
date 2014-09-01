package net.iyuyue.ushare;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class TextEdit extends Activity {
	
	private TextView editTitle;
	private TextView editInput;
	private String type, title, value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_text_edit);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); 
		
		title = getIntent().getStringExtra("title");
		type = getIntent().getStringExtra("type");
		value = getIntent().getStringExtra("value");
		
		editTitle = (TextView)findViewById(R.id.textEditTitle);
		editTitle.setText(title);
		
		editInput = (TextView)findViewById(R.id.textEditInput);
		
		if(type.equals("phone"))
			editInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		if(!type.equals("updates")) {	
			editInput.setSingleLine();
		}	
		editInput.setText(value);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_edit, menu);
		return true;
	}
	
	public void onClickSave(View v) {
		
		if(editInput.getText().toString().equals(value))
			finish();
		
		SharedPreferences sharedPreferences = getSharedPreferences("Login",
				Context.MODE_MULTI_PROCESS);
		final String token = sharedPreferences.getString("token", "");
		final String uid = sharedPreferences.getString("id", "");

		String urlContent = "";

		try {
			urlContent = "http://ushare.iyuyue.net/api/userInfoEdit&uid="
					+ uid
					+ "&token="
					+ token
					+ "&type="
					+ type
					+ "&value="
					+ URLEncoder
							.encode(editInput.getText().toString(), "UTF-8")
					+ "";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("test", "Return " + urlContent);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlContent, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(final String response) {
				finish();
			}
		});
	}

}

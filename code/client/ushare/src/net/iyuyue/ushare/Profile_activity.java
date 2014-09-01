package net.iyuyue.ushare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yelp.android.webimageview.ImageLoader;
import com.yelp.android.webimageview.WebImageView;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Profile_activity extends Activity {
	private ListView profilelist;
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
//		ImageLoader.initialize(this, null);

		setContentView(R.layout.profile);

		// mDialog.setMessage("Loading...");
		mDialog = new ProgressDialog(this);
		mDialog.show();
		// mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.progressdialog);

		SharedPreferences sharedPreferences = getSharedPreferences("Login",
				Context.MODE_MULTI_PROCESS);
		String token = sharedPreferences.getString("token", "");
		String id = sharedPreferences.getString("id", "");

		String mid = getIntent().getStringExtra("mid");

		String urlContent = "http://ushare.iyuyue.net/api/getContacts&mid="
				+ mid + "&uid=" + id + "&token=" + token;

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlContent, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error, String content) {
				// dismiss loader
				mDialog.dismiss();
			}

			@Override
			public void onSuccess(final String response) {
				// dismiss loader
				mDialog.dismiss();

				infoHandler xmlHandler = new infoHandler();
				NodeList status = xmlHandler.paser(response,
						"/Ushare/getContacts/status");
				String result = (status == null || status.item(0) == null) ? "error"
						: status.item(0).getFirstChild().getNodeValue();
				// Log.d(TAG, "Return " + result);
				
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

				if (result.equals("success")) {

					final NodeList userName = xmlHandler.paser(response,
							"/Ushare/getContacts/name");
					final NodeList userGender = xmlHandler.paser(response,
							"/Ushare/getContacts/gender");
					final NodeList userEmail = xmlHandler.paser(response,
							"/Ushare/getContacts/email");
					final NodeList userPhone = xmlHandler.paser(response,
							"/Ushare/getContacts/phone");
					final NodeList userAvatar = xmlHandler.paser(response,
							"/Ushare/getContacts/avatar");
					final NodeList userUpdates = xmlHandler.paser(response,
							"/Ushare/getContacts/updates");

					// get user information

					final int genderType = (userGender.item(0).getFirstChild() == null) ? -1
							: Integer.parseInt(userGender.item(0)
									.getFirstChild().getNodeValue());

					final String emailAdd = (userEmail.item(0).getFirstChild() == null) ? ""
							: (userEmail.item(0).getFirstChild().getNodeValue());

					final String name = (userName.item(0).getFirstChild() == null) ? ""
							: (userName.item(0).getFirstChild().getNodeValue());

					final String phone = (userPhone.item(0).getFirstChild() == null) ? ""
							: (userPhone.item(0).getFirstChild().getNodeValue());

					final String slogan = (userUpdates.item(0).getFirstChild() == null) ? ""
							: (userUpdates.item(0).getFirstChild()
									.getNodeValue());
					
					WebImageView userImage = (WebImageView)findViewById(R.id.portrait);
					if(userAvatar.item(0) != null && userAvatar.item(0).getFirstChild() != null) {
//						Log.d("test", userAvatar.item(0).getFirstChild().getNodeValue());
						userImage.setImageUrl(userAvatar.item(0).getFirstChild().getNodeValue());
					} else {
						userImage.setImageResource(R.drawable.noneavatar);
					}
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("profiletitle", "Name");
					map.put("profiledetail", name);
					list.add(map);

					map = new HashMap<String, Object>();
					map.put("profiletitle", "Gender");
					
					if (genderType == 1)
						map.put("profiledetail", "Male");
					else if (genderType == 0)
						map.put("profiledetail", "Female");
					else
						map.put("profiledetail", "Secret");
					
					list.add(map);

					map = new HashMap<String, Object>();
					map.put("profiletitle", "Email");
					map.put("profiledetail", emailAdd);
					list.add(map);

					map = new HashMap<String, Object>();
					map.put("profiletitle", "Phone");
					map.put("profiledetail", phone);
					list.add(map);
					
					profilelist = (ListView) findViewById(R.id.profilelist);
					SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.profile_list, new String[] {
									"profiletitle", "profiledetail" },
							new int[] { R.id.profiletitle, R.id.profiledetail });
					profilelist.setAdapter(adapter);
					
					profilelist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0,
								View arg1, int position, long arg3) {

							if (position == 2) {
								Intent i = new Intent(Intent.ACTION_SEND);
								i.setType("message/rfc822");
								i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailAdd});
								i.putExtra(Intent.EXTRA_SUBJECT, "Say Hello");
								i.putExtra(Intent.EXTRA_TEXT   , "Hi, I am");
								try {
								    startActivity(Intent.createChooser(i, "Send mail..."));
								} catch (android.content.ActivityNotFoundException ex) {
								    Toast.makeText(getBaseContext(), "There are no email client installed.", Toast.LENGTH_SHORT).show();
								}				

							}
							else if (position == 3) {
								Intent intent=new Intent(); 
								intent.setAction(Intent.ACTION_CALL);   
								intent.setData(Uri.parse("tel:"+phone));
								startActivity(intent);
							}
						}

					});
	
				} else {
//					Intent login = new Intent(getBaseContext(),
//							LoginActivity.class);
//					login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//					startActivity(login);
					WebImageView userImage = (WebImageView)findViewById(R.id.portrait);
					userImage.setImageResource(R.drawable.errormark);
					

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("profiletitle", "Sorry, the contacts is not available or has expired.");
//					map.put("profiledetail", "");
					list.add(map);		
					
					profilelist = (ListView) findViewById(R.id.profilelist);
					SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.profile_list, new String[] {
									"profiletitle", "profiledetail" },
							new int[] { R.id.profiletitle, R.id.profiledetail });
					profilelist.setAdapter(adapter);

				}			

			}
		});

	}

}

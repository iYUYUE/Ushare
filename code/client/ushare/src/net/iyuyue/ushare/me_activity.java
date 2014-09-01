package net.iyuyue.ushare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class me_activity extends Fragment {
	private ListView meListView;
	private ListView meListView_below;
	ProgressDialog mDialog;
	private String[] mStrings = {
			"http://ushare.iyuyue.net/web/rsc/avatar/8.png"
	 };

	public static final String TAG = "meService";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.me, container, false);

		meListView = (ListView) rootView.findViewById(R.id.meList);
		meListView_below = (ListView) rootView.findViewById(R.id.meList_below);
		return rootView;

		// TODO Auto-generated method stub
		// return inflater.inflate(R.layout.me, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// mDialog.setMessage("Loading...");
		mDialog = new ProgressDialog(getActivity());
		mDialog.show();
		// mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.progressdialog);

		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);

		String token = sharedPreferences.getString("token", "");
		String id = sharedPreferences.getString("id", "");

		String url = "http://ushare.iyuyue.net/api/userInfo&uid=" + id
				+ "&token=" + token;

		AsyncHttpClient client = new AsyncHttpClient();

		client.get(url, new AsyncHttpResponseHandler() {
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
						"/Ushare/userInfo/status");
				String result = (status == null || status.item(0) == null) ? "error" : status
						.item(0).getFirstChild().getNodeValue();
//				Log.d(TAG, "Return " + result);

				if (result.equals("success")) {

					final NodeList userName = xmlHandler.paser(response,
							"/Ushare/userInfo/name");
					final NodeList userGender = xmlHandler.paser(response,
							"/Ushare/userInfo/gender");
					final NodeList userEmail = xmlHandler.paser(response,
							"/Ushare/userInfo/email");
					final NodeList userPhone = xmlHandler.paser(response,
							"/Ushare/userInfo/phone");
					final NodeList userAvatar = xmlHandler.paser(response,
							"/Ushare/userInfo/avatar");
					final NodeList userUpdates = xmlHandler.paser(response,
							"/Ushare/userInfo/updates");
					
					// get user information
					
					final int genderType = (userGender.item(0)
							.getFirstChild() == null) ? -1 : Integer
							.parseInt(userGender.item(0)
									.getFirstChild().getNodeValue());
					
					final String emailAdd = (userEmail.item(0)
							.getFirstChild() == null) ? "" : (userEmail
							.item(0).getFirstChild().getNodeValue());
					
					final String name = (userName.item(0).getFirstChild() == null) ? ""
							: (userName.item(0).getFirstChild()
									.getNodeValue());
					
					final String phone = (userPhone.item(0).getFirstChild() == null) ? ""
							: (userPhone.item(0).getFirstChild()
									.getNodeValue());
					
					final String slogan = (userUpdates.item(0)
							.getFirstChild() == null) ? ""
							: (userUpdates.item(0).getFirstChild()
									.getNodeValue());

					List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

					for (int i = 0; i < 4; i++) {

						Map<String, Object> map = new HashMap<String, Object>();

						if (i == 0) {
							
							map.put("name", "Profile Photo");
							
							if(userAvatar.item(0) != null && userAvatar.item(0).getFirstChild() != null) {
									
							map.put("portrait", userAvatar.item(0).getFirstChild().getNodeValue());
//							try {
//								URL thumb_u = new URL(userAvatar.item(0).getFirstChild().getNodeValue());
//								Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
//								map.put("portrait", thumb_d);
//							} catch (MalformedURLException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (DOMException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}		
							} else {
								map.put("portrait", R.drawable.noneavatar);
							}
							map.put("icon", R.drawable.arrow);
						}
						if (i == 1) {
							
							map.put("name", "Name");
							map.put("detial", name);
							map.put("icon", R.drawable.arrow);
						}

						if (i == 2) {
							
							map.put("name", "Email");
							map.put("detial", emailAdd);
							map.put("icon", R.drawable.arrow);
						}

						if (i == 3) {
							
							map.put("name", "Phone");
							map.put("detial", phone);
							map.put("icon", R.drawable.arrow);
						}
						listData.add(map);
					}
					
				            
					SimpleAdapter simpleAdapter = new myAdapter(
							getActivity()

							.getApplicationContext(),
							listData,
							R.layout.me_list,

							new String[] { "name", "portrait", "detial", "icon" },

							new int[] { R.id.name, R.id.photo, R.id.detial,
									R.id.ItemImage });

					meListView.setAdapter(simpleAdapter);
					meListView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int position, long arg3) {
									if (position == 1) {

									}
									if (position == 2) {									
										
										Intent emailIntent = new Intent(getActivity(), TextEdit.class);
										emailIntent.putExtra("title", "Email");
										emailIntent.putExtra("type", "email");
										emailIntent.putExtra("value", emailAdd);
//										login.putExtra("EXTRA_SESSION_ID", sessionId);
										startActivityForResult(emailIntent, 1);

									}
									if (position == 3) {
										Intent phoneIntent = new Intent(getActivity(), TextEdit.class);
										phoneIntent.putExtra("title", "Phone");
										phoneIntent.putExtra("type", "phone");
										phoneIntent.putExtra("value", phone);
//										login.putExtra("EXTRA_SESSION_ID", sessionId);
										startActivityForResult(phoneIntent, 1);

									}

								}

							});

					// below
					List<Map<String, Object>> listData_below = new ArrayList<Map<String, Object>>();

					for (int i = 0; i < 2; i++) {

						Map<String, Object> map_below = new HashMap<String, Object>();

						if (i == 0) {
							map_below.put("name", "Gender");

							if (genderType == 1)
								map_below.put("detial", "Male");
							else if (genderType == 0)
								map_below.put("detial", "Female");
							else
								map_below.put("detial", "Secret");

							map_below.put("icon", R.drawable.arrow);
						}
						if (i == 1) {
							
							map_below.put("name", "What's up");
							map_below.put("detial", slogan);
							map_below.put("icon", R.drawable.arrow);
						}

						listData_below.add(map_below);
					}

					SimpleAdapter simpleAdapter_below = new SimpleAdapter(
							getActivity()

							.getApplicationContext(),
							listData_below,
							R.layout.me_list,

							new String[] { "name", "portrait", "detial", "icon" },

							new int[] { R.id.name, R.id.photo, R.id.detial,
									R.id.ItemImage });

					meListView_below.setAdapter(simpleAdapter_below);

					meListView_below
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int position, long arg3) {
									if (position == 0) {
										Intent genderIntent = new Intent(getActivity(), MultiSelect.class);
										genderIntent.putExtra("title", "Gender");
										genderIntent.putExtra("type", "gender");
										genderIntent.putExtra("value", genderType+"");
//										login.putExtra("EXTRA_SESSION_ID", sessionId);
										startActivityForResult(genderIntent, 1);
									}
									if (position == 1) {
										Intent updatesIntent = new Intent(getActivity(), TextEdit.class);
										updatesIntent.putExtra("title", "What's up");
										updatesIntent.putExtra("type", "updates");
										updatesIntent.putExtra("value", slogan);
//										login.putExtra("EXTRA_SESSION_ID", sessionId);
										startActivityForResult(updatesIntent, 1);
									}

								}

							});

				} else {
					Intent login = new Intent(getActivity(),
							LoginActivity.class);
					login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivityForResult(login, 1);
				}

			}
		});
		
//		AsyncHttpClient imgclient = new AsyncHttpClient();
//	    imgclient.get("http://ushare.iyuyue.net/web/rsc/avatar/8.png", new AsyncHttpResponseHandler(){
//	        public void onSuccess(String response){
//	            try{
//	                byte[] imageAsBytes = response.getBytes();
//	                Drawable d = (Drawable) new BitmapDrawable(getActivity().getApplicationContext().getResources(), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
//	                Map<String, Object> mp = listData.get(0);
//	                mp.put("portrait", d);
//	                simpleAdapter.notifyDataSetChanged();
//	                
//	            } catch(Throwable e){
//	                e.printStackTrace();
//	            }
//	        }
//	    });
		
		

	}

}
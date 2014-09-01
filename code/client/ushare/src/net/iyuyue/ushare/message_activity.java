package net.iyuyue.ushare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.iyuyue.ushare.library.SwipeDismissListViewTouchListener;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class message_activity extends Fragment {

	public static final String TAG = "messageService";
	
	public static final int MAX_LENGTH = 76;
	
	private int messageNum;
//	private Activity mActivity;
	ProgressDialog mDialog;
	
//	@Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mActivity = activity;
//    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.message, container, false);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
//        mDialog.setMessage("Loading...");
		mDialog = new ProgressDialog(getActivity());
		mDialog.show();
//      mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.progressdialog);
        
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);

		String token = sharedPreferences.getString("token", "");
		String id = sharedPreferences.getString("id", "");

		String url = "http://ushare.iyuyue.net/api/messageList&uid=" + id
				+ "&token=" + token;

		AsyncHttpClient client = new AsyncHttpClient();

		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void  onFailure(Throwable error, String content) {
				// dismiss loader
				mDialog.dismiss();
			}

			@Override
			public void onSuccess(final String response) {
				// dismiss loader
				mDialog.dismiss();
				
				infoHandler xmlHandler = new infoHandler();
				NodeList status = xmlHandler.paser(response,
						"/Ushare/messageList/status");
				String result = (status == null || status.item(0) == null) ? "error" : status
						.item(0).getFirstChild().getNodeValue();
				Log.d(TAG, "Return " + result);
						
				if (result.equals("success")) {
					ListView listview = (ListView) getView().findViewById(
							R.id.messageList);

					final List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();

					final SimpleAdapter messageAdapter = new SimpleAdapter(
							getActivity().getApplicationContext(), listdata,
							R.layout.mail_list, new String[] { "read", "title",
									"info", "date" }, new int[] { R.id.read,
									R.id.title, R.id.info, R.id.date });
					listview.setAdapter(messageAdapter);

					final List<Node> messagesID = toList(xmlHandler.paser(response,
							"/Ushare/messageList/message/id"));
					final NodeList messageUnread = xmlHandler.paser(response,
							"/Ushare/messageList/message/unread");
					final NodeList messageType = xmlHandler.paser(response,
							"/Ushare/messageList/message/type");
					final NodeList messageContent = xmlHandler.paser(response,
							"/Ushare/messageList/message/content");
					final NodeList messageDate = xmlHandler.paser(response,
							"/Ushare/messageList/message/rec_date");
					//
					messageNum = messagesID.size();
					// Log.d(TAG, "messageNum "+messageNum);
					if (messageNum > 0) {
						for (int i = 0; i < messageNum; i++) {
							int j = messageNum - i - 1;
							Map<String, Object> map = new HashMap<String, Object>();
							if (Integer.parseInt(messageUnread.item(j)
									.getFirstChild().getNodeValue()) == 1)
								map.put("read", R.drawable.unread);
							int type = Integer.parseInt(messageType.item(j)
									.getFirstChild().getNodeValue());
							if (type == 1)
								map.put("title", "Ushare Request");
							else if (type == 2)
								map.put("title", "Ushare Response");
							else if (type == 3)
								map.put("title", "Ushare Contacts");
							else if (type == 0)
								map.put("title", "System Message");
							else
								map.put("title", "Unknown");
							String messageInfo = messageContent.item(j).getFirstChild().getNodeValue();
//							if(messageInfo.length()>MAX_LENGTH)
//								map.put("info", messageInfo.substring(0, MAX_LENGTH-4)+"...");
//							else
								map.put("info", messageInfo);
							map.put("date", relativeTime(messageDate.item(j).getFirstChild().getNodeValue()));
//							map.put("date", relativeTime("2014-04-22 12:47:48"));
							listdata.add(map);
							
							SharedPreferences sharedPreferences = getActivity()
									.getSharedPreferences("Login",
											Context.MODE_MULTI_PROCESS);
							final String token = sharedPreferences.getString("token",
									"");
							final String uid = sharedPreferences.getString("id", "");
							
							listview.setOnItemClickListener(new OnItemClickListener() {

								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Map<String, Object> mp = listdata.get(position);
//									Log.d("test", "HAHA " + mp.containsKey("read"));
									if (mp.containsKey("read")) {
										mp.remove("read");
									}
									listdata.remove(position);
									listdata.add(position, mp);
//									Log.d(TAG, "line "+position);
									messageAdapter.notifyDataSetChanged();
									
									Intent intent;
									int messsageType = Integer.parseInt(messageType.item(messageNum - position - 1).getFirstChild().getNodeValue());
									if(messsageType==1) 
										intent = new Intent(getActivity(), detail_activity.class);
									else if (messsageType==3)
										intent = new Intent(getActivity(), Profile_activity.class);
									else
										intent = new Intent(getActivity(), MessageDetailResponse.class);
//									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//									intent.putExtra("title",
//											messageType.item(messageNum - position - 1)
//													.getFirstChild().getNodeValue());
									intent.putExtra("type", messsageType+"");
									intent.putExtra("info", messageContent.item(messageNum - position - 1).getFirstChild().getNodeValue());
									intent.putExtra("date", messageDate.item(messageNum - position - 1).getFirstChild().getNodeValue());
									intent.putExtra("mid", messagesID.get(messageNum - position - 1).getFirstChild().getNodeValue());
									startActivity(intent);
									
									if (Integer.parseInt(messageUnread.item(messageNum - position - 1)
											.getFirstChild().getNodeValue()) == 1) 
									{
										// update unread status

										String urlContent = "http://ushare.iyuyue.net/web/rsc/readMessage&mid="
												+ messagesID
														.get(messageNum - position - 1)
														.getFirstChild().getNodeValue()
												+ "&uid=" + uid + "&token=" + token;
										AsyncHttpClient client = new AsyncHttpClient();
										client.get(urlContent,
												new AsyncHttpResponseHandler() {
													// @Override
													// public void onSuccess(final String
													// response) {
													//
													// }
												});
									}
									
									
								}
							});
							
							// swipe-to-dismiss based on the idea from
							// https://github.com/romannurik/Android-SwipeToDismiss
							
					        SwipeDismissListViewTouchListener touchListener =
					                new SwipeDismissListViewTouchListener(
					                		listview,
					                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
//					                            @Override
//					                            public boolean canDismiss(int position) {
//					                                return true;
//					                            }

					                            @Override
					                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
					                                for (int position : reverseSortedPositions) {
					                                	listdata.remove(position);
					                                	
					                                	String urlContent = "http://ushare.iyuyue.net/web/rsc/deleteMessage&mid="
					    										+ messagesID
					    												.get(messageNum - position - 1)
					    												.getFirstChild().getNodeValue()
					    										+ "&uid=" + uid + "&token=" + token;
					                                	
					                                	messagesID.remove(messageNum - position - 1);
					                                	messageNum--;
					                                	
					    								AsyncHttpClient client = new AsyncHttpClient();
					    								client.get(urlContent,
					    										new AsyncHttpResponseHandler() {
					    											 @Override
					    											 public void onSuccess(final String
					    											 response) {
//					    												 Log.d("test", "Return " + response);
					    											
					    											 }
					    										});
					    								
					                                }
					                                
					                                messageAdapter.notifyDataSetChanged();
					                                
					                            }
					                        });
					        
					        listview.setOnTouchListener(touchListener);
					        listview.setOnScrollListener(touchListener.makeScrollListener());
						}
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("title", "No new message available.");
						listdata.add(map);
					}
					
					

				} else {
					Intent login = new Intent(getActivity(),
							LoginActivity.class);
					login.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivityForResult(login, 1);
				}

			}
		});

	}
	
	public static List<Node> toList(final NodeList list) {
		ArrayList<Node> result = new ArrayList<Node>();
		for(int i=0; i<list.getLength(); i++)
			result.add(list.item(i));	
		return result;

	  }
	
	public static String relativeTime(String time) {
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formater = new SimpleDateFormat("yy-MM-dd");
		SimpleDateFormat formater2 = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat formater3 = new SimpleDateFormat("EEEE");
		
		Date date = new Date();
		
		try {
			date = parser.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Calendar cal=Calendar.getInstance();
		
		Calendar cal_old=Calendar.getInstance();
		cal_old.setTime(date);
		
		Calendar cal_yest = Calendar.getInstance();
		cal_yest.add(Calendar.DATE, -1);
		
		Calendar cal_lastweek = Calendar.getInstance();
		cal_lastweek.add(Calendar.DATE, -7);
		
		long interval = (cal.getTimeInMillis() / 1000L) -  (cal_old.getTimeInMillis() / 1000L);
		
		if(interval<0)
			interval=0;
//		Log.d(TAG, "interval1 " + parser.format(cal.getTime()));
//		Log.d(TAG, "interval2 " + parser.format(cal_old.getTime()));
		
		if (interval < 60 * 60 - 30) {
			interval = Math.round((double)interval / 60);
			if(interval==0)
				return "just now";
	        return interval == 1 ? interval + " minute ago" : interval + " minutes ago";
	    }
	    else if (interval <= 6 * 60 * 60 + 60) {
	    	interval = Math.round((double)interval / (60 * 60));
	        return interval <= 1 ? interval + " hour ago" : interval + " hours ago";
	    }
	    else if (formater.format(cal_old.getTime()).equals(formater.format(cal.getTime()))) {
	        return formater2.format(cal_old.getTime());
	    }
	    else if (((String)formater.format(cal_old.getTime())).equals((String)formater.format(cal_yest.getTime()))) {
	        return "Yesterday";
	    }
	    else if ((cal_old.getTimeInMillis() / 1000L) >= (cal_lastweek.getTimeInMillis() / 1000L)) {
	    	return formater3.format(cal_old.getTime());
	    }
	    else {
	    	return formater.format(cal_old.getTime());
	    }
	}

}

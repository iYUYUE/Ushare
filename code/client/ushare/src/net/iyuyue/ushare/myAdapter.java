package net.iyuyue.ushare;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.yelp.android.webimageview.WebImageView;

public class myAdapter extends SimpleAdapter {

	public myAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setViewImage(final ImageView v, final String value) {
//		URL imageUrl = null;
//		Bitmap bitmap = null;
//		try {
//			imageUrl = new URL(value);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
		// try {
		// HttpURLConnection conn = (HttpURLConnection) imageUrl
		// .openConnection();
		// conn.connect();
		// InputStream is = conn.getInputStream();
		//
		// bitmap = BitmapFactory.decodeStream(is);
		//
		// is.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		if(value.length()>1)
			((WebImageView) v).setImageUrl(value);
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.get(value, new AsyncHttpResponseHandler() {
//			public void onSuccess(String response) {
//				try {	
//					byte[] imageAsBytes = response.getBytes();
//					Log.d("test", (v instanceof WebImageView)+value);	
//					v.setImageBitmap(BitmapFactory.decodeByteArray(
//							imageAsBytes, 0, imageAsBytes.length));
//					v.refreshDrawableState();
//				} catch (Throwable e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		v.setImageBitmap(bitmap);
	}

}

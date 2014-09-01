package net.iyuyue.ushare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Contactus_activity extends Activity {
	
	private ListView contactlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_us);
		contactlist = (ListView)this.findViewById(R.id.contactlist);
		SimpleAdapter adapter = new SimpleAdapter(this,ContactData(),R.layout.contact_list,
					new String[]{"contacttitle","contactdetail"},
					new int[]{R.id.contacttitle,R.id.contactdetail});
		contactlist.setAdapter(adapter);
		}

		private List<Map<String, Object>> ContactData() {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("contacttitle", "Weibo");
			map.put("contactdetail", "Hello_Ushare");
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("contacttitle", "Site");
			map.put("contactdetail", "http://ushare.iyuyue.net");
			list.add(map);

			map = new HashMap<String, Object>();
			map.put("contacttitle", "Email");
			map.put("contactdetail", "ushare@support.com");
			list.add(map);
			
			return list;
		}
	}


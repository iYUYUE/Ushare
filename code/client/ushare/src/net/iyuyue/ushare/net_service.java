/*	the idea and code based on
	http://www.androidhive.info/2012/07/android-detect-internet-connection-status/
*/
package net.iyuyue.ushare;

import net.iyuyue.ushare.library.DialogUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.TextView;


public class net_service {
    
    private Context _context;
     
    public net_service(Context context){
        this._context = context;
    }
 
//    public boolean isConnectingToInternet(){
//        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
//          if (connectivity != null) 
//          {
//              NetworkInfo[] info = connectivity.getAllNetworkInfo();
//              if (info != null) 
//                  for (int i = 0; i < info.length; i++) 
//                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
//                      {
//                          return true;
//                      }
// 
//          }
//          return false;
//    }
    
    public boolean isConnectingToWIFI(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
          	NetworkInfo networkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
          	boolean isWifiConn = networkInfo.isConnected();
          	if(isWifiConn)
          		return true;
 
          }
          return false;
    }
    
//    public void showAlert(String title, String message) {
//    	Looper.prepare();
//    	
//    	AlertDialog.Builder builder = DialogUtils.getAlertDialog(_context, true);  
////    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    	// "No WIFI Connection"
//    	builder.setTitle(title);
//    	
//    	Boolean status=false;
//    	builder.setIcon((status) ? R.drawable.success : R.drawable.fail);
//    	
//    	// "To share your position, please connect to WIFI first"
//    	TextView myMsg = new TextView(_context);
//    	myMsg.setText(message);
//    	myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
//    	myMsg.setTextColor(Color.BLACK);
//    	myMsg.setTextSize(17);
//    	myMsg.setPadding(40, 18, 40, 30);
//    	builder.setView(myMsg);
//    	
//    	builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int whichButton) {
//    	    	Intent dialogIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//    	    	dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    	    	_context.startActivity(dialogIntent);
//    	    	
//    	        dialog.dismiss();
//    	    }
//    	});
//    	
//    	builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
//    	    public void onClick(DialogInterface dialog, int whichButton) {
//    	        dialog.dismiss();
//    	    }
//    	});
//    	
//    	AlertDialog alert = builder.create();
//    	
//    	alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//    	
//    	alert.show();
//    	
//    	Looper.loop();
//    }
    
    public void showNormalAlert(String title, String message) {
    	
    	AlertDialog.Builder builder = DialogUtils.getAlertDialog(_context, true);  
//    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	// "No WIFI Connection"
    	builder.setTitle(title);
    	
    	Boolean status=false;
    	builder.setIcon((status) ? R.drawable.success : R.drawable.fail);
    	
    	// "To share your position, please connect to WIFI first"
    	TextView myMsg = new TextView(_context);
    	myMsg.setText(message);
    	myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
    	myMsg.setTextColor(Color.BLACK);
    	myMsg.setTextSize(17);
    	myMsg.setPadding(40, 18, 40, 30);
    	builder.setView(myMsg);
    	
    	builder.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	    	Intent dialogIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
    	    	dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    	_context.startActivity(dialogIntent);
    	    	
    	        dialog.dismiss();
    	    }
    	});
    	
    	builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	        dialog.dismiss();
    	    }
    	});
    	
    	AlertDialog alert = builder.create();
    	
//    	alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    	
    	alert.show();
    	
    }
    
}

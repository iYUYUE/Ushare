package net.iyuyue.ushare;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	public static final String TAG = "loginSession";

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login); 

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		// clean up login history data
		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
		
		Editor editor = sharedPreferences.edit();// get the editor
		editor.remove("token");
		editor.remove("id");
		editor.commit(); // change commit
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} 
//		else if (mPassword.length() < 4) {
//			mPasswordView.setError(getString(R.string.error_invalid_password));
//			focusView = mPasswordView;
//			cancel = true;
//		}

		// Check for a valid email address.
//		if (TextUtils.isEmpty(mEmail)) {
//			mEmailView.setError(getString(R.string.error_field_required));
//			focusView = mEmailView;
//			cancel = true;
//		} else if (!mEmail.contains("@")) {
//			mEmailView.setError(getString(R.string.error_invalid_email));
//			focusView = mEmailView;
//			cancel = true;
//		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void clickRegister(View mView) {
		Intent registerPage = new Intent(this, WebActivity.class);
		registerPage.putExtra("EXTRA_URL", "http://ushare.iyuyue.net/web/rsc/signup");
        startActivity(registerPage); 
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private Context mContext;
		public UserLoginTask (Context context){
	         mContext = context;
	    }
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				return false;
//			}

			String rid = JPushInterface.getRegistrationID(mContext);
			return login(mEmail, mPassword, rid);
	        
//	        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
//	        String result = sharedPreferences.getString("status", "");
////	        
//	        if(result.equals("success"))
//	        	return true;
			// TODO: register the new account here.
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
		
		
		public boolean login(String user, String pw, String rid) {
	    	//  http fetch demo    
			
			String responseBody = "";
			 try{
		           HttpClient httpclient = new DefaultHttpClient();

		           HttpGet request = new HttpGet();
		           URI api = new URI("http://ushare.iyuyue.net/api/auth&user="+user+"&password="+pw+"&rid="+rid);
		           request.setURI(api);
		           HttpResponse response = httpclient.execute(request);
		           int responseCode = response.getStatusLine().getStatusCode();
		           switch(responseCode) {
			           case 200:
			        	   HttpEntity entity = response.getEntity();
			               if(entity != null) {
			                   responseBody = EntityUtils.toString(entity);
			               	}
			               break;
		           }
		       }catch(Exception e){
		           Log.e(TAG, "Error in http connection "+e.toString());
		       }
			 
			 // network error
			 if(responseBody == "")
				 return false;
			 
			 	infoHandler xmlHandler = new infoHandler();
//				
				NodeList status = xmlHandler.paser(responseBody, "/Ushare/auth/status");
				
				String result = (status == null || status.item(0)==null)?"error":status.item(0).getFirstChild().getNodeValue();
				
				if(result.equals("success")) {
					NodeList tokenContent = xmlHandler.paser(responseBody, "/Ushare/auth/token");
					
					NodeList uidContent = xmlHandler.paser(responseBody, "/Ushare/auth/uid");
					
					SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
					
					Editor editor = sharedPreferences.edit();// get the editor
					editor.remove("token");
					editor.putString("token", tokenContent.item(0).getFirstChild().getNodeValue());
					editor.remove("id");
					editor.putString("id", uidContent.item(0).getFirstChild().getNodeValue());
//					editor.remove("status");
//					editor.putString("status", status.item(0).getFirstChild().getNodeValue());
					
					editor.commit(); // change commit
					
					return true;
				}
			 
//			Log.d(TAG, "Token: " + responseBody);
	  		SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);
	  		
	  		String token = sharedPreferences.getString("token", "");
	  		String id = sharedPreferences.getString("id", "");
	  		
	  		Log.d(TAG, "Token: " + token);
	  		Log.d(TAG, "ID: " + id);
	  		
	  		return false;
	    }
		
	}
	
}

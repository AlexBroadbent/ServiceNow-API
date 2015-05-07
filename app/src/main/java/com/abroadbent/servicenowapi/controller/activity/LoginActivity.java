package com.abroadbent.servicenowapi.controller.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.controller.ConnectionHandler;
import com.abroadbent.servicenowapi.controller.handler.UserRecordHandler;
import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.model.object.UserRecord;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 *
 * TODO: Add remember me option
 */
public class LoginActivity extends Activity implements AppConstants {
    private static final String LOG_TAG = "LoginActivity";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private SharedPreferences mSharedPreferences;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private TextView mInstanceName;
    private View mProgressView;
    private View mLoginFormView;
    private View mChangeInstanceView;

    // Request Codes for Instance
    private final int CHANGE_INSTANCE_REQUEST = 0;
    private final int CHANGE_INSTANCE_SUCCESS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mInstanceName = (TextView) findViewById(R.id.instance_name);
        mInstanceName.setText("Instance Name: " + mSharedPreferences.getString(PREF_INSTANCE_NAME, mSharedPreferences.getString(BASE_URL, "")));

        // Print out instance settings for testing
        Log.d("LoginActivity.onCreate", "PREF_INSTANCE_NAME: " + mSharedPreferences.getString(PREF_INSTANCE_NAME, ""));
        Log.d("LoginActivity.onCreate", "PREF_INSTANCE_URL: " + mSharedPreferences.getString(PREF_INSTANCE_URL, ""));
        Log.d("LoginActivity.onCreate", "BASE_URL: " + mSharedPreferences.getString(BASE_URL, ""));
        Log.d("LoginActivity.onCreate", "BASE_API_URL: " + mSharedPreferences.getString(BASE_API_URL, ""));

        Button mChangeInstanceButton = (Button) findViewById(R.id.change_instance);
        mChangeInstanceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChangeInstanceDialog(mSharedPreferences, LoginActivity.this);
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
        mChangeInstanceView = findViewById(R.id.change_instance_layout);
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
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mChangeInstanceView.setVisibility(show ? View.GONE : View.VISIBLE);
            mChangeInstanceView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mChangeInstanceView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mChangeInstanceView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mUrl;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
            mUrl = mSharedPreferences.getString(BASE_API_URL, DEMO_INSTANCE_BASE_API_URL);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Connect to the instance using the credentials, if there's a result then the details are valid, otherwise they're not.
                String connectUrl = mUrl + "/api/now/table/sys_user?sysparm_query=";
                try {
                    connectUrl += URLEncoder.encode("user_name="+mUsername, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Log.w("LoginActivity", "URLEncoder encoding of '" + connectUrl + "' caused a UnsupportedEncodingException");
                }

                HttpClient client = ConnectionHandler.getInstance();
                String basicAuth = Base64.encodeToString((mUsername + ":" + mPassword).getBytes(), Base64.NO_WRAP);
                HttpGet request = ConnectionHandler.getRequest(connectUrl, basicAuth);
                HttpResponse httpResponse = client.execute(request);
                Header[] headers = httpResponse.getAllHeaders();

                List<UserRecord> user = UserRecordHandler.parseRecordList(httpResponse.getEntity().getContent());
                if (user.size() != 1)
                    throw new Exception("Only 1 User should be returned");
                UserRecord userRecord = user.get(0);

                // Store login credentials for future API calls
                mSharedPreferences.edit()
                        .putString(USER_API_USERNAME, mUsername)
                        .putString(USER_API_PASSWORD, mPassword)
                        .putString(USER_API_BASIC_AUTH, basicAuth.replace("=", ""))
                        .putString(USER_API_COOKIE, ConnectionHandler.getCookies())
                        .putString(USER_SYS_ID, userRecord.getSysId())
                        .putLong(PREF_LAST_USER_LOGIN, new Date().getTime())
                .apply();

            }
            catch (Exception e) {
                Log.e("LoginActivity.doInBack", "Exception: " + e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                // TODO: Load Approval, Incident and Request records

                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("LoginActivity.oAR", "Request: " + requestCode + ", Result: " + resultCode + ", Data: " + data);

        if (requestCode == CHANGE_INSTANCE_REQUEST) {
            if (resultCode == CHANGE_INSTANCE_SUCCESS) {

                // Update SharedPreferences with new instance settings
                mSharedPreferences.edit()
                        .putString(PREF_INSTANCE_NAME, data.getStringExtra(PREF_INSTANCE_NAME))
                        .putString(PREF_INSTANCE_URL, data.getStringExtra(PREF_INSTANCE_URL))
                .apply();

                // Call method to update BaseUrl and BaseApiUrl variables
                StartActivity.getURL(mSharedPreferences);

                // Update instance name label
                mInstanceName.setText("Instance Name: " + mSharedPreferences.getString(PREF_INSTANCE_NAME, mSharedPreferences.getString(BASE_URL, "")));
            }
        }
    }

    // COPIED FROM STARTACTIVITY - CAN'T BE STATIC
    public final void displayChangeInstanceDialog(final SharedPreferences sharedPreferences, final Context context) {
        // Get instance name using dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_instance_name_title);
        final EditText instanceName = new EditText(context);
        instanceName.setHint("Instance Name");
        builder.setView(instanceName);
        //setViewBasedOnVersion(builder);
        builder.setPositiveButton(R.string.dialog_instance_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Store entered text
                //String enteredName = ((EditText)findViewById(R.id.instance_name)).getText().toString().trim();
                //String enteredUrl = ((EditText)findViewById(R.id.instance_url)).getText().toString().trim();

                String enteredName = instanceName.getText().toString().trim();

                // Store entered value
                if (!enteredName.equals("") && !enteredName.isEmpty())
                    sharedPreferences.edit()
                            .putString(PREF_INSTANCE_NAME, enteredName)
                            .putString(PREF_INSTANCE_URL, null)
                            .commit();
                    //else if (!enteredUrl.equals("") && !enteredUrl.isEmpty())
                    //  sharedPreferences.edit().putString(PREF_INSTANCE_URL, enteredUrl).commit();
                else
                    sharedPreferences.edit().putString(PREF_INSTANCE_NAME, DEMO_INSTANCE_NAME).commit();

                // Call method to set base url and base api url
                StartActivity.getURL(sharedPreferences);

                mInstanceName.setText("Instance Name: " + enteredName);
            }
        });
        builder.setNegativeButton(R.string.dialog_instance_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Use demo instance - demonightlyuk
                sharedPreferences.edit()
                        .putString(PREF_INSTANCE_NAME, DEMO_INSTANCE_NAME)
                        .putString(PREF_INSTANCE_URL, null)
                        .commit();

                StartActivity.getURL(sharedPreferences);

                mInstanceName.setText("Instance Name: " + sharedPreferences.getString(DEMO_INSTANCE_NAME, ""));
            }
        });
        builder.show();
    }
}




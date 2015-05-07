package com.abroadbent.servicenowapi.controller.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.model.AppConstants;

/**
 *      Determine what activity to start, if there are no saved details in shared preferences
 *          for the instance then the instance name is requested TODO: add options for URL input
 *          , if there are login credentials stored already - load the homeActivity for those settings
 *          TODO: Is that secure?
 *
 *
 *      TODO: Create a static method for getUrlFromSharedPreferences()?
 *
 *  @author      alexander.broadbent
 *  @version     17/12/2014
 */
public class StartActivity extends Activity implements AppConstants {

    protected static SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        // DEBUG: Clear the SharedPreferences on startup so login and start activities run + set AVD to use localhost
        //mSharedPreferences.edit().clear().putString(PREF_INSTANCE_URL, "http://10.0.2.2:8080").apply();//.putString(PREF_INSTANCE_NAME, "empalexander").apply();

        // Print out the url at start of app for debug
        Log.d("StartActivity.onCreate", "getURL() on startup: " + getURL(mSharedPreferences));

        // If user credentials are saved then just straight to
        if (mSharedPreferences.getString(USER_API_BASIC_AUTH, null) != null) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }


        // Determine if an instance is set to load
        if (getURL(mSharedPreferences) == null) {
            // TODO: put text box on dialog

            // Get instance name using dialog window
            displayChangeInstanceDialog(mSharedPreferences, this);
        }
        else {
            // Start LoginActivity
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    // FIXME: finish this
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setViewBasedOnVersion(AlertDialog.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setView(R.layout.popup_change_instance);
        else {
            builder.setMessage(R.string.dialog_instance_name_message);

            final LinearLayout layout = new LinearLayout(this);
            final EditText instanceName = new EditText(this);
            instanceName.setHint("Instance Name");
            instanceName.setId(R.id.instance_name);
            final EditText instanceUrl = new EditText(this);
            instanceUrl.setHint("Instance URL");
            instanceUrl.setId(R.id.instance_url);

            layout.addView(instanceName, 0);
            layout.addView(instanceUrl, 1);

            builder.setView(layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: Remove/Limit menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_about) {
            // Show about dialog...
            Log.i("Action_About", "Show about dialog...");
        }
        if (id == R.id.action_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *      <p>Get the appropriate URL to load by going through stored settings<br/>
     *      - Uses the order: Instance Name, Instance URL, Demo</p>
     *
     * @return  URL for the WebView to load
     */
    public static String getURL(SharedPreferences sharedPreferences) {

        // Put preferences in variables for ease
        String instanceName = sharedPreferences.getString(PREF_INSTANCE_NAME, null);
        String instanceURL = sharedPreferences.getString(PREF_INSTANCE_URL, null);

        String url;
        if (instanceName != null) {
            url = "https://" + instanceName + ".service-now.com/$m.do";
        }
        else if (instanceURL != null) {
            url = instanceURL;

            if (! url.startsWith("http"))
                url = "http://" + url;
            // Check if a port is included - if not then add default 8080 port
            if (! url.contains(":"))
                url += ":" + sharedPreferences.getString(PREF_INSTANCE_PORT, "8080");

            // Check if the url ends with the mobile url
            if (! url.endsWith("$m.do"))
                url += "/$m.do";
        }
        else {
            return null;
        }

        // Save to BASE_URL in SharedPreferences, so activities can get URL from SharedPrefs + BASE_API_URL
        sharedPreferences.edit().putString(BASE_URL, url).putString(BASE_API_URL, url.replace("/$m.do", "")).apply();

        return url;
    }

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
                getURL(sharedPreferences);

                // Start LoginActivity
                Intent loginIntent = new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
                finish();

            }
        });
        builder.setNegativeButton(R.string.dialog_instance_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Use demo
                sharedPreferences.edit()
                        .putString(PREF_INSTANCE_NAME, DEMO_INSTANCE_NAME)
                        .putString(PREF_INSTANCE_URL, null)
                        .commit();
                getURL(sharedPreferences);

                // Start LoginActivity
                Intent loginIntent = new Intent(context, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
        builder.show();
    }
}

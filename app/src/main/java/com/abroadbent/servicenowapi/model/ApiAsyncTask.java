package com.abroadbent.servicenowapi.model;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.model.object.ApiResponseRecord;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * @author      alexander.broadbent
 * @version     14/01/2015
 */
public class ApiAsyncTask extends AsyncTask<URL, Integer, ApiResponseRecord> {

    // ViewType describes the table that the task is executed on
    ViewType type;
    boolean updateUI;

    ExpandableListView mListViewLayout;
    ProgressBar mProgressBar;

    public ApiAsyncTask(ViewType type, boolean usesFragmentListViewLayout, ExpandableListView layout) {
        this.type = type;
        this.updateUI = usesFragmentListViewLayout;
        this.mListViewLayout = layout;
    }


    @Override
    protected ApiResponseRecord doInBackground(URL... params) {

        String response = "";

        URL url = params[0];

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url.getPath());

        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String line = "";
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        catch (ClientProtocolException e) {
            Log.e("ApiObject.doInBackground", "ClientProtocolException: " + e.getMessage());
        }
        catch (IOException e) {
            Log.e("ApiObject.doInBackground", "IOException: " + e.getMessage());
        }

        return null;//ApiResponseRecord.parseResponse(response, type);
    }

    @Override
    protected void onPreExecute() {
        // Initialise progress bar on UI

        if (updateUI) {
            mProgressBar = (ProgressBar) mListViewLayout.findViewById(R.id.fragment_list_view_progress);

            showProgress(false);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // Update progress

        if (updateUI) {
            // Do something?
        }
    }

    @Override
    protected void onPostExecute(ApiResponseRecord result) {
        // Update UI with progress and resultant object

        if (updateUI) {
            showProgress(false);
        }
    }

    @Override
    protected void onCancelled() {
        if (updateUI)
            showProgress(false);
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
            int shortAnimTime = 200; // getResources().getInteger(android.R.integer.config_shortAnimTime);

            mListViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mListViewLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mListViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

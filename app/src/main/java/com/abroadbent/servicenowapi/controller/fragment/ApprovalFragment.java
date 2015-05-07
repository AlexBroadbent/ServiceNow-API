package com.abroadbent.servicenowapi.controller.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.controller.ConnectionHandler;
import com.abroadbent.servicenowapi.controller.handler.ApprovalRecordHandler;
import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.model.exception.NoRecordFoundException;
import com.abroadbent.servicenowapi.model.object.ApprovalRecord;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 *      Display all Approval records for the user
 *
 *      TODO: Add short_description field from task table
 *
 *  @author     alexander.broadbent
 *  @version    17/12/2014
 */
public class ApprovalFragment extends Fragment implements AppConstants {
    private static final String LOG_TAG = "ApprovalFragment";

    protected SharedPreferences mSharedPreferences;
    protected ApprovalGetTask mApprovalTask = null;
    protected String mUrl;

    protected ListView mApprovalListView;
    protected View mProgressView;
    protected View mSearchView;

    protected List<ApprovalRecord> mApprovalRecordList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {

        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // TODO: Store and retrieve list onPause and onResume from savedInstanceBundle

        // Get baseURL REPLACE: Don't use demo instance - throw error instead that base_url is invalid/non-existent
        mUrl = mSharedPreferences.getString(BASE_API_URL, THROW_ERROR);//DEMO_INSTANCE_BASE_API_URL);
        //if (mUrl.equals(THROW_ERROR))

        // Set class's UI components
        mApprovalListView = (ListView) rootView.findViewById(R.id.fragment_list_view_container);
        mProgressView = rootView.findViewById(R.id.fragment_list_view_progress);
        mSearchView = rootView.findViewById(R.id.search_view);

        // Make API Call to get all the home tiles
        showProgress(true);
        mApprovalTask = new ApprovalGetTask();
        mApprovalTask.execute((Void) null);

        return rootView;
    }


    public class ApprovalGetTask extends AsyncTask<Void, Void, List<ApprovalRecord>> {
        private static final String LOG_TAG = "ApprovalGetTask";

        @Override
        protected List<ApprovalRecord> doInBackground(Void... parms) {
            String connectUrl = mUrl + "/api/now/table/sysapproval_approver?sysparm_query=";

            // Connect to client and create request from safely encoded URL string
            HttpClient client = ConnectionHandler.getInstance();

            // Safely encode URL parameters
            try {
                String params = "approver=" + mSharedPreferences.getString(USER_SYS_ID, THROW_ERROR);
                // TODO: Add a condition for after the date of the most recent approval record in the DB
                params = URLEncoder.encode(params, "UTF-8");

                connectUrl += params;
            } catch (UnsupportedEncodingException e) {
                Log.w(LOG_TAG, "URLEncoder encoding of '" + connectUrl + "' caused a UnsupportedEncodingException");
            }

            Log.d(LOG_TAG, "API URL: " + connectUrl);

            String basicAuth = mSharedPreferences.getString(USER_API_BASIC_AUTH, "");
            Log.d("BasicAuth", basicAuth);

            HttpGet request = ConnectionHandler.getRequest(connectUrl, basicAuth);

            try {
                // Execute request
                HttpResponse httpResponse = client.execute(request);

                // Check if an error is returned
                if (httpResponse.getStatusLine().getStatusCode() == 404) {
                    throw new NoRecordFoundException("No Records Found");
                }
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    throw new Exception(httpResponse.getStatusLine().getStatusCode() + " - " + httpResponse.getStatusLine().getReasonPhrase());
                }

                // parse response into list of Approval records
                return ApprovalRecordHandler.parseResult(httpResponse, basicAuth);
            }
            catch (Exception e) {
                // TODO: Improve error handling
                Log.e(LOG_TAG + ".dIB", "Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ApprovalRecord> result) {
            mApprovalTask = null;
            mApprovalRecordList = result;
            updateApprovalRecordListView();

        }

        @Override
        protected void onCancelled() {
            mApprovalTask = null;
            showProgress(false);
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

            mApprovalListView.setVisibility(show ? View.GONE : View.VISIBLE);
            mApprovalListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mApprovalListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSearchView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);
            mApprovalListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     *  Update view on UI to display the list of records
     */
    protected void updateApprovalRecordListView() {
        
        if (mApprovalRecordList != null && mApprovalRecordList.size() > 0) {
            //TODO: Wait until all tasks have finished, or update UI at the end of each task

            // Once all tasks have finished, display records on UI
            showProgress(false);

            // Put records onto UI
            //ListViewLoader

            // TODO: Update last date - could do this by storing a list in RecordHandler and getting the latest date on the fly
        }
        else {
            // Put empty tile into list if it returned no results
            TextView textView = new TextView(getActivity().getApplicationContext());
            textView.setText("No Approval Records Found");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            mApprovalListView.addView(textView);
        }
    }

    private boolean areTasksFinished() {
        for (ApprovalRecord record : mApprovalRecordList) {
            if (! record.isTaskFinished()) {
                return false;
            }
        }
        return true;
    }

}

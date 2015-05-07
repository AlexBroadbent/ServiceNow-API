package com.abroadbent.servicenowapi.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import com.abroadbent.servicenowapi.model.AppConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author      alexander.broadbent
 * @version     21/02/2015
 */
public class AggregateTableCount implements AppConstants {

    private static final String ns = null;
    protected static String tableName = null;
    protected static CountTableTask countTableTask = null;
    protected static View linkedView = null;
    protected static SharedPreferences mSharedPreferences;


    public static void runCountTask(String table, View view, Context context) {
        tableName = table;
        linkedView = view;
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        countTableTask = new CountTableTask();
        countTableTask.execute();
    }


    public static class CountTableTask extends AsyncTask<Void, Void, Integer> {
        private static final String LOG_TAG = "CountTableTask";

        @Override
        protected Integer doInBackground(Void... params) {
            String connectUrl = mSharedPreferences.getString(BASE_API_URL, THROW_ERROR) + "/api/now/stats/"+tableName+"?sysparm_count=true";

            HttpClient client = ConnectionHandler.getInstance();
            HttpGet request = ConnectionHandler.getRequest(connectUrl, mSharedPreferences.getString(USER_API_BASIC_AUTH, THROW_ERROR));

            try {
                // Execute request
                HttpResponse httpResponse = client.execute(request);

                return parseCountResult(httpResponse.getEntity().getContent());
            }
            catch (Exception e) {
                Log.e(LOG_TAG + ".dIB", e.getMessage());
                return -1;
            }
        }

        @Override
        public void onPostExecute(Integer result) {
            Log.d(LOG_TAG, "Count of " + tableName + ": " + result);

            // Set count to view
            if (result != -1)
                ((TextView) linkedView).setText(result);
            else
                ((TextView) linkedView).setText("Error");
        }
    }



    protected static Integer parseCountResult(InputStream in) throws XmlPullParserException, IOException, NumberFormatException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
            parser.require(XmlPullParser.START_TAG, ns, RESULT);
            parser.require(XmlPullParser.START_TAG, ns, STATS);
            parser.require(XmlPullParser.START_TAG, ns, COUNT);
            return Integer.parseInt(parser.getText());
        }
        finally {
            in.close();
        }
    }
/*
    protected static Integer readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, NumberFormatException {
        Integer count = 0;

        parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
        parser.require(XmlPullParser.START_TAG, ns, RESULT);
        parser.require(XmlPullParser.START_TAG, ns, STATS);
        parser.require(XmlPullParser.START_TAG, ns, COUNT);
        if (parser.next() == XmlPullParser.TEXT) {
            count = Integer.parseInt(parser.getText());
        }
        // Are the closing tags required?
        parser.require(XmlPullParser.END_TAG, ns, COUNT);
        parser.require(XmlPullParser.END_TAG, ns, STATS);
        parser.require(XmlPullParser.END_TAG, ns, RESULT);
        parser.require(XmlPullParser.END_TAG, ns, RESPONSE);

        return count;
    }
    */
}

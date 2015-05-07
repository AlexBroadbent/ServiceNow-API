package com.abroadbent.servicenowapi.controller.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.abroadbent.servicenowapi.R;
import com.abroadbent.servicenowapi.controller.AggregateTableCount;
import com.abroadbent.servicenowapi.model.AppConstants;

import java.text.DateFormat;
import java.util.Date;

/**
 *      Display a homepage for the user, with some USEFUL information
 *
 * @author      alexander.broadbent
 * @version     24/01/2015
 */
public class HomeFragment extends Fragment implements AppConstants {

    protected SharedPreferences mSharedPreferences;
    protected ListView layout;
    protected boolean countLoaded = false;

    protected View mApprovalCountView;
    protected View mIncidentCountView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Load Instance Name
        TextView instanceName = (TextView) rootView.findViewById(R.id.instance_name);
        instanceName.setText(mSharedPreferences.getString(PREF_INSTANCE_NAME, mSharedPreferences.getString(PREF_INSTANCE_URL, "-")));

        // Load Username
        TextView username = (TextView) rootView.findViewById(R.id.user_username);
        username.setText(mSharedPreferences.getString(USER_API_USERNAME, ""));

        // Load Last Login
        TextView lastLogin = (TextView) rootView.findViewById(R.id.user_last_login);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        //String date = new SimpleDateFormat("E d MMMM YY HH:mm:ss").format(new Date(mSharedPreferences.getLong(PREF_LAST_USER_LOGIN, new Date().getTime())));
        //int day = Integer.parseInt(new SimpleDateFormat("d").format(new Date(mSharedPreferences.getLong(PREF_LAST_USER_LOGIN, new Date().getTime()))));
        //date = date.replaceFirst(""+day, day+getDayOfMonthSuffix(day));
        lastLogin.setText(dateFormat.format(new Date(mSharedPreferences.getLong(PREF_LAST_USER_LOGIN, new Date().getTime()))));

        // Get text labels that will contain the count of records
        mApprovalCountView = rootView.findViewById(R.id.approvals);
        mIncidentCountView = rootView.findViewById(R.id.incidents);

        // Get aggregate table counts, if they aren't set already
        if (!countLoaded)
            loadCounts();

        return rootView;
    }

    public void loadCounts() {

        AggregateTableCount.runCountTask(TABLE_APPROVAL, mApprovalCountView, getActivity());
        AggregateTableCount.runCountTask(TABLE_INCIDENT, mIncidentCountView, getActivity());

        countLoaded = true;
    }


    String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}

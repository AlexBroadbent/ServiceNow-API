package com.abroadbent.servicenowapi.view;

import android.widget.ListView;

/**
 *      Gives a Record class the method to update the UI for when a Task has been completed
 *
 *
 * @author      alexander.broadbent
 * @version     18/02/2015
 */
public class StaticUIUpdater {

    protected static ListView mListView = null;

    private StaticUIUpdater() {}

    public static void setListView(ListView listView) {
        mListView = listView;
    }


    public static void updateValue(String recordSysId, String newValue) {
        if (mListView != null) {

        }
    }

}

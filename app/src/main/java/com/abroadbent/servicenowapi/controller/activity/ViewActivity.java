package com.abroadbent.servicenowapi.controller.activity;

import android.app.Activity;
import android.os.Bundle;

import com.abroadbent.servicenowapi.model.ViewType;
import com.abroadbent.servicenowapi.model.object.ApiResponseRecord;

/**
 *      Generic Class for viewing a card of a given type
 *
 * @author      alexander.broadbent
 * @version     18/12/2014
 */
public class ViewActivity extends Activity {

    ApiResponseRecord object;
    ViewType type;

    public ViewActivity(ApiResponseRecord object, ViewType type) {
        this.object = object;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_record);
    }


}

package com.abroadbent.servicenowapi.model.object;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 *      Class to represent a User record from the sys_user table.
 *      Mainly used for logging in, and having user information available
 *
 * @author      alexander.broadbent
 * @version     01/02/2015
 */
public class UserRecord extends ApiResponseRecord {
    private static final String LOG_TAG = "UserRecord";

    protected String first_name;
    protected String last_name;
    protected String user_name;
    protected String locked_out;



    public UserRecord() {
        super();

        first_name = null;
        last_name = null;
        user_name = null;
        locked_out = null;
    }


    public String getFirstName() {
        return first_name;
    }
    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }
    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getName() {
        return first_name + " " + last_name;
    }

    public String getUserName() {
        return user_name;
    }
    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getLockedOut() {
        return locked_out;
    }
    public void setLockedOut(String locked_out) {
        this.locked_out = locked_out;
    }


    public void readFirstName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, FIRST_NAME);
        first_name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, FIRST_NAME);
    }

    public void readLastName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, LAST_NAME);
        last_name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, LAST_NAME);
    }

    public void readUserName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, USER_NAME);
        user_name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, USER_NAME);
    }

    public void readLockedOut(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, LOCKED_OUT);
        locked_out = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, LOCKED_OUT);
    }




}

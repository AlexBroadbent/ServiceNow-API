package com.abroadbent.servicenowapi.model.object;

import com.abroadbent.servicenowapi.model.AppConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 *  Super class for all objects that are returned from an API Call
 *      - Contains the common elements of Approval, Email, Incident, and Request records
 *
 * @author      alexander.broadbent
 * @version     17/12/2014
 */
public class ApiResponseRecord implements AppConstants, Comparable<ApiResponseRecord> {
    // We don't use namespaces
    protected static final String ns = null;

    protected String sys_id;
    protected String sys_created_on;
    protected String sys_updated_on;
    protected String state;
    protected String active;
    protected String sys_created_by;
    protected String sys_updated_by;
    protected String comments;
    protected String order;


    public ApiResponseRecord() {
        sys_id = null;
        sys_created_on = null;
        sys_updated_on = null;
        state = null;
        active = null;
        sys_created_by = null;
        sys_updated_by = null;
        comments = null;
        order = null;
    }


    /*
     *      Getters and Setters of Common Record Attributes
     */

    public String getSysId() {
        return sys_id;
    }
    public void setSysId(String sys_id) {
        this.sys_id = sys_id;
    }

    public String getCreatedOn() {
        return sys_created_on;
    }
    public void setCreatedOn(String sys_created_on) {
        this.sys_created_on = sys_created_on;
    }

    public String getUpdatedOn() {
        return sys_updated_on;
    }
    public void setUpdatedOn(String sys_updated_on) {
        this.sys_updated_on = sys_updated_on;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return sys_created_by;
    }
    public void setCreatedBy(String sys_created_by) {
        this.sys_created_by = sys_created_by;
    }

    public String getUpdatedBy() {
        return sys_updated_by;
    }
    public void setUpdatedBy(String sys_updated_by) {
        this.sys_updated_by = sys_updated_by;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }


    /*
     *      XML Pull Parser methods
     */

    protected void readSysId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_ID);
        sys_id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SYS_ID);
    }

    protected void readCreatedOn(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_CREATED_ON);
        sys_created_on = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SYS_CREATED_ON);
    }

    protected void readUpdatedOn(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_UPDATED_ON);
        sys_updated_on = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SYS_UPDATED_ON);
    }

    protected void readState(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, STATE);
        state = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, STATE);
    }

    protected void readActive(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ACTIVE);
        active = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ACTIVE);
    }

    protected void readCreatedBy(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_CREATED_BY);
        sys_created_by = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SYS_CREATED_BY);
    }

    protected void readUpdatedBy(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_UPDATED_BY);
        sys_updated_by = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SYS_UPDATED_BY);
    }

    protected void readComments(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, COMMENTS);
        comments = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, COMMENTS);
    }

    protected void readOrder(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ORDER);
        order = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, ORDER);
    }




    /*
     *      Extract the text of an XML element
     */
    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Called by subclasses to get values for the elements in this superclass
    public boolean parseRecord(XmlPullParser parser, String name) throws XmlPullParserException, IOException {
        if (name.equals(SYS_ID)) {
            readSysId(parser);
            return true;
        }
        else if (name.equals(SYS_CREATED_ON)) {
            readCreatedOn(parser);
            return true;
        }
        else if (name.equals(SYS_UPDATED_ON)) {
            readUpdatedOn(parser);
            return true;
        }
        else if (name.equals(STATE)) {
            readState(parser);
            return true;
        }
        else if (name.equals(SYS_CREATED_BY)) {
            readCreatedBy(parser);
            return true;
        }
        else if (name.equals(SYS_UPDATED_BY)) {
            readUpdatedBy(parser);
            return true;
        }
        else if (name.equals(COMMENTS)) {
            readComments(parser);
            return true;
        }
        else if (name.equals(ORDER)) {
            readOrder(parser);
            return true;
        }

        return false;
    }


    public boolean fieldInSuper(String name) {
        return (name.equals(SYS_ID) || name.equals(SYS_CREATED_ON) ||
                name.equals(SYS_UPDATED_ON) || name.equals(STATE) ||
                name.equals(SYS_CREATED_BY) || name.equals(SYS_UPDATED_BY)  ||
                name.equals(COMMENTS) || name.equals(ORDER));
    }

    protected static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @Override
    public int compareTo(ApiResponseRecord another) {
        // TODO: Compare records based on a DATE of their sys_created_on/sys_updated_on, add parser - setting to select which field??

        /*try {
            this.getClass().getField("sys_date_created");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        }*/
        return -1;
    }
}

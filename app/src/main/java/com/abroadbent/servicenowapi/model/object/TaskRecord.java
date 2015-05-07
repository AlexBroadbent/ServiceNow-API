package com.abroadbent.servicenowapi.model.object;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 *
 *
 * @author      alexander.broadbent
 * @version     13/02/2015
 */
public class TaskRecord extends ApiResponseRecord {

    protected String short_description;
    protected String description;
    protected String priority;

    public TaskRecord() {
        super();

        short_description = null;
        description = null;
        priority = null;
    }

    public String getShortDescription() {
        return short_description;
    }
    public void setShortDescription(String short_description) {
        this.short_description = short_description;
    }

    public String getDescription() {
        return short_description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return short_description;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }


    public void readShortDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SHORT_DESCRIPTION);
        short_description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SHORT_DESCRIPTION);
    }

    public void readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, DESCRIPTION);
        description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, DESCRIPTION);
    }

    public void readPriority(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, PRIORITY);
        priority = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, PRIORITY);
    }

}

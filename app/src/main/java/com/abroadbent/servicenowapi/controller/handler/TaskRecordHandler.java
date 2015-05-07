package com.abroadbent.servicenowapi.controller.handler;

import android.util.Log;
import android.util.Xml;

import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.model.object.TaskRecord;

import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author      alexander.broadbent
 * @version     01/02/2015
 */
public class TaskRecordHandler implements AppConstants {
    private static final String LOG_TAG = "TaskRecordHandler";
    private static final String ns = null;

    protected static List<TaskRecord> taskRecords;

    public List<TaskRecord> getUserRecords() {
        return taskRecords;
    }

    public static List<TaskRecord> parseResultList(HttpResponse response) {
        try {
            return (taskRecords = parseRecordList(response.getEntity().getContent()));
        }
        catch (IOException | XmlPullParserException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return null;
    }

    public static TaskRecord parseRecord(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
            return readResult(parser);
        }
        finally {
            in.close();
        }
    }

    public static List<TaskRecord> parseRecordList(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readFeed(parser);
        }
        finally {
            in.close();
        }
    }

    protected static List<TaskRecord> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TaskRecord> taskRecordList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for a result tag
            if (name.equals(RESULT))
                taskRecordList.add(readResult(parser));
            else
                skip(parser);
        }

        return taskRecordList;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    private static TaskRecord readResult(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, RESULT);
        TaskRecord record = new TaskRecord();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (record.fieldInSuper(name)) {
                record.parseRecord(parser, name);
            }
            else if (name.equals(SHORT_DESCRIPTION)) {
                record.readShortDescription(parser);
            }
            else if (name.equals(DESCRIPTION)) {
                record.readDescription(parser);
            }
            else if (name.equals(PRIORITY)) {
                record.readPriority(parser);
            }
            else {
                skip(parser);
            }
        }

        return record;
    }

}

package com.abroadbent.servicenowapi.controller.handler;

import android.util.Log;
import android.util.Xml;

import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.model.object.ApprovalRecord;

import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *      Class that handles AsyncTasks that get Approval Records
 *
 * TODO: Remove BaseAuth - used because classes down the chain require the authorisation key for making a secondary async task
 *
 * @author      alexander.broadbent
 * @version     30/01/2015
 */
public class ApprovalRecordHandler implements AppConstants {
    private static final String LOG_TAG = "ApprovalRecordHandler";
    private static final String ns = null;

    protected static List<ApprovalRecord> approvalRecords;

    public static List<ApprovalRecord> getApprovalRecords() {
        return approvalRecords;
    }

    public static List<ApprovalRecord> parseResult(HttpResponse response, String baseAuth) {
        try {

            approvalRecords = parseRecordList(response.getEntity().getContent(), baseAuth);

            // Do duplicate check

            return approvalRecords;
        }
        catch (IOException | XmlPullParserException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return null;
    }


    /**
     *      Parse a single record from XML form
     *
     * @param in
     * @return ApprovalRecord
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ApprovalRecord parseRecord(InputStream in, String baseAuth) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
            return readResult(parser, baseAuth);
        }
        finally {
            in.close();
        }
    }


    /**
     *      Parse a list of records from XML form
     *
     * @param in <code>InputStream</code> XML of response
     * @param baseAuth
     * @return List&lt;ApprovalRecord&gt;
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static List<ApprovalRecord> parseRecordList(InputStream in, String baseAuth) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readFeed(parser, baseAuth);
        }
        finally {
            in.close();
        }
    }

    protected static List<ApprovalRecord> readFeed(XmlPullParser parser, String baseAuth) throws XmlPullParserException, IOException {
        List<ApprovalRecord> approvalRecords = new ArrayList<ApprovalRecord>();

        parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for a result tag
            if (name.equals(RESULT))
                approvalRecords.add(readResult(parser, baseAuth));
            else
                skip(parser);
        }

        return approvalRecords;
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

    private static ApprovalRecord readResult(XmlPullParser parser, String baseAuth) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, RESULT);

        ApprovalRecord record = new ApprovalRecord();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (record.fieldInSuper(name)) {
                record.parseRecord(parser, name);
            }
            else if (name.equals(APPROVER)) {
                record.readApprover(parser, baseAuth);
            }
            else if (name.equals(SYS_APPROVAL)) {
                record.readSysApproval(parser, baseAuth);
            }
            else {
                skip(parser);
            }
        }

        return record;
    }

}

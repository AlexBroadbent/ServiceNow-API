package com.abroadbent.servicenowapi.controller.handler;

import android.util.Log;
import android.util.Xml;

import com.abroadbent.servicenowapi.model.AppConstants;
import com.abroadbent.servicenowapi.model.object.UserRecord;

import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *      Contains a set of UserRecords, and handles responses from the server requesting
 *          User Records. The results get added to the set of UserRecords so they can be
 *          quickly accessed again.
 *
 * @author      alexander.broadbent
 * @version     01/02/2015
 */
public class UserRecordHandler implements AppConstants {
    private static final String LOG_TAG = "UserApprovalHandler";
    private static final String ns = null;

    protected static Set<UserRecord> userRecords;


    /**
     *      Check previously received records for a sys_id of a required record
     *
     * @param   sys_id GUID to check existing records for
     * @return  User with the matching given sys_id
     */
    public static UserRecord check(String sys_id) {
        if (userRecords != null && userRecords.size() > 0) {
            for (UserRecord userRecord : userRecords) {
                if (userRecord.getSysId().equals( sys_id )) {
                    return userRecord;
                }
            }
        }

        return null;
    }

    public static List<UserRecord> parseResult(HttpResponse response) {
        try {
            if (userRecords == null)
                userRecords = new HashSet<UserRecord>();

            List<UserRecord> thisResult = parseRecordList(response.getEntity().getContent());

            userRecords.addAll(thisResult);

            return thisResult;
        }
        catch (IOException | XmlPullParserException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return null;
    }


    public static List<UserRecord> parseRecordList(InputStream in) throws XmlPullParserException, IOException {

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

    public static UserRecord parseRecord(InputStream in) throws XmlPullParserException, IOException {

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

    protected static List<UserRecord> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<UserRecord> userRecordsList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, RESPONSE);
        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            // Starts by looking for a result tag
            if (name.equals(RESULT))
                userRecordsList.add(readResult(parser));
            else
                skip(parser);
        }

        return userRecordsList;
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

    private static UserRecord readResult(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, RESULT);

        UserRecord record = new UserRecord();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (record.fieldInSuper(name)) {
                record.parseRecord(parser, name);
            }
            else if (name.equals(FIRST_NAME)) {
                record.readFirstName(parser);
            }
            else if (name.equals(LAST_NAME)) {
                record.readLastName(parser);
            }
            else if (name.equals(USER_NAME)) {
                record.readUserName(parser);
            }
            else if (name.equals(LOCKED_OUT)) {
                record.readLockedOut(parser);
            }
            else {
                skip(parser);
            }
        }

        return record;
    }

}

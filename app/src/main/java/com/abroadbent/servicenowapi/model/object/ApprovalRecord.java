package com.abroadbent.servicenowapi.model.object;

import android.os.AsyncTask;
import android.util.Log;

import com.abroadbent.servicenowapi.controller.ConnectionHandler;
import com.abroadbent.servicenowapi.controller.handler.TaskRecordHandler;
import com.abroadbent.servicenowapi.controller.handler.UserRecordHandler;
import com.abroadbent.servicenowapi.model.AppConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 *      Represents an XML parsing Approval Record
 *
 *      TODO: Move Tasks up to ApprovalRecordHandler
 *
 * @author      alexander.broadbent
 * @version     17/12/2014
 */
public class ApprovalRecord extends ApiResponseRecord implements AppConstants {
    private static final String LOG_TAG = "ApprovalRecord";

    protected String approver_link;
    protected UserRecord approver_user_record;
    protected String sys_approval_link;
    protected TaskRecord sys_approval_task_record;

    protected ApprovalUserTask approvalUserTask = null;
    protected boolean approvalUserTaskFinished = false;

    protected ApprovalTaskTask approvalTaskTask = null;
    protected boolean approvalTaskTaskFinished = false;


    public ApprovalRecord() {
        super();

        approver_link = null;
        approver_user_record = null;
        sys_approval_link = null;
        sys_approval_task_record = null;
    }


    public String getApproverLink() {
        return approver_link;
    }
    public void setApproverLink(String approver_link) {
        this.approver_link = approver_link;
    }

    public UserRecord getApproverUserRecord() {
        return approver_user_record;
    }
    public void setApproverUserRecord(UserRecord approver_user_record) {
        this.approver_user_record = approver_user_record;
    }

    public String getSysApprovalLink() {
        return sys_approval_link;
    }
    public void setSysApprovalLink(String sys_approval_link) {
        this.sys_approval_link = sys_approval_link;
    }

    public TaskRecord getApprovalTaskRecord() {
        return sys_approval_task_record;
    }
    public void setApprovalTaskRecord(TaskRecord sys_approval_task_record) {
        this.sys_approval_task_record = sys_approval_task_record;
    }


    public void readApprover(XmlPullParser parser, String baseAuth) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, APPROVER);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(APPROVER_LINK)) {
                readApproverLink(parser, baseAuth);
            }
            else {
                skip(parser);
            }
        }
    }

    protected void readApproverLink(XmlPullParser parser, String baseAuth) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, APPROVER_LINK);

        approver_link = readText(parser);

        // Kick off ApproverUserTask to start a new task to retrieve the information about the User field
        approvalUserTask = new ApprovalUserTask();
        approvalUserTaskFinished = false;
        approvalUserTask.execute(approver_link, baseAuth);
        // TODO: Cache user records to avoid unnecessary API calls

        parser.require(XmlPullParser.END_TAG, ns, APPROVER_LINK);
    }

    public void readSysApproval(XmlPullParser parser, String baseAuth) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, SYS_APPROVAL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals(SYS_APPROVAL_LINK)) {
                readSysApprovalLink(parser, baseAuth);
            }
            else {
                skip(parser);
            }
        }
    }

    protected void readSysApprovalLink(XmlPullParser parser, String baseAuth) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SYS_APPROVAL_LINK);

        sys_approval_link = readText(parser);

        // Kick off ApprovalTaskTask to start a new task to retrieve the information about the Task field
        approvalTaskTask = new ApprovalTaskTask();
        approvalUserTaskFinished = false;
        approvalTaskTask.execute(sys_approval_link, baseAuth);

        // TODO: Caching

        parser.require(XmlPullParser.END_TAG, ns, SYS_APPROVAL_LINK);
    }




    // AsyncTask to pull User information
    public class ApprovalUserTask extends AsyncTask<String, Void, UserRecord> {
        private static final String LOG_TAG = "ApprovalUserTask";

        @Override
        protected UserRecord doInBackground(String... params) {

            String connectUrl = params[0];
            String baseAuth = params[1];

            // Connect to client and create request from safely encoded URL string
            HttpClient client = ConnectionHandler.getInstance();

            // Get sys_id of request and check the record handler for a match
            int lastSlash = connectUrl.lastIndexOf('/');
            if (lastSlash < connectUrl.length()) {
                String sys_id = connectUrl.substring(lastSlash + 1);

                UserRecord record = UserRecordHandler.check(sys_id);
                if (record != null)
                    return record;
            }

            // Safely encode URL parameters
            try {
                if (connectUrl.contains("sysparm_query")) {
                    String[] query = connectUrl.split("sysparm_query", 1);

                    connectUrl = URLEncoder.encode(query[1], "UTF-8");
                    connectUrl = query[0] + query[1];
                }
            } catch (UnsupportedEncodingException e) {
                Log.w(LOG_TAG, "URLEncoder encoding of '" + connectUrl + "' caused a UnsupportedEncodingException");
            }

            Log.d(LOG_TAG, "API URL: " + connectUrl);

            HttpGet request = ConnectionHandler.getRequest(connectUrl, baseAuth);

            try {
                // Execute request
                HttpResponse httpResponse = client.execute(request);

                // parse response into list of Approval records
                List<UserRecord> userRecord = UserRecordHandler.parseResult(httpResponse);

                if (userRecord.size() != 1) {
                    Log.d(LOG_TAG, "User Record not found from response. Records found: " + userRecord.size());
                    return null;
                }

                return userRecord.get(0);
            }
            catch (Exception e) {
                Log.e(LOG_TAG + ".dIB", "Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        public void onPostExecute(UserRecord result) {
            Log.d(LOG_TAG, "User found for approval: " + result.getName());
            approver_user_record = result;
            approvalUserTaskFinished = true;
            approvalUserTask = null;
        }

        @Override
        public void onCancelled() {
            Log.d(LOG_TAG, "Task cancelled");
            approver_user_record = null;
            approvalUserTaskFinished = true;
            approvalUserTask = null;
        }
    }

    // AsyncTask to pull Task information
    public class ApprovalTaskTask extends AsyncTask<String, Void, TaskRecord> {
        private static final String LOG_TAG = "ApprovalTaskTask";

        @Override
        protected TaskRecord doInBackground(String... params) {

            String connectUrl = params[0];
            String baseAuth = params[1];

            // Connect to client and create request from safely encoded URL string
            HttpClient client = ConnectionHandler.getInstance();

            // Safely encode URL parameters
            try {
                if (connectUrl.contains("sysparm_query")) {
                    String[] query = connectUrl.split("sysparm_query", 1);

                    connectUrl = URLEncoder.encode(query[1], "UTF-8");
                    connectUrl = query[0] + query[1];
                }
            } catch (UnsupportedEncodingException e) {
                Log.w(LOG_TAG, "URLEncoder encoding of '" + connectUrl + "' caused a UnsupportedEncodingException");
            }

            Log.d(LOG_TAG, "API URL: " + connectUrl);
            HttpGet request = ConnectionHandler.getRequest(connectUrl, baseAuth);

            try {
                // Execute request
                HttpResponse httpResponse = client.execute(request);

                // parse response into list of Approval records
                List<TaskRecord> taskRecords = TaskRecordHandler.parseResultList(httpResponse);

                if (taskRecords.size() != 1) {
                    Log.d(LOG_TAG, "User Record not found from response. Records found: " + taskRecords.size());
                    return null;
                }

                return taskRecords.get(0);
            }
            catch (Exception e) {
                Log.e(LOG_TAG + ".doInBackground", "Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        public void onPostExecute(TaskRecord result) {
            Log.d(LOG_TAG, "Task found for approval: " + result.getShortDescription());
            sys_approval_task_record = result;
            approvalTaskTaskFinished = true;
            approvalTaskTask = null;
        }

        @Override
        public void onCancelled() {
            Log.d(LOG_TAG, "Task Cancelled");
        }
    }


    public boolean isTaskFinished() {
        return (approvalUserTaskFinished && approvalTaskTaskFinished);
        //return (approvalUserTask != null && approvalUserTask.getStatus() == AsyncTask.Status.FINISHED);
    }

}

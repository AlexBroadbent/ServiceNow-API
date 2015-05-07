package com.abroadbent.servicenowapi.model;

import com.abroadbent.servicenowapi.model.object.ApprovalRecord;
import com.abroadbent.servicenowapi.model.object.EmailRecord;
import com.abroadbent.servicenowapi.model.object.IncidentRecord;
import com.abroadbent.servicenowapi.model.object.RequestRecord;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *  <h3>Handles all calls to API for records, keeps track of current records.</h3>
 *  <i>Note: Singleton class - only one instance of RecordHandler exists for the whole system.</i>
 *
 * @author      alexander.broadbent
 * @version     26/01/2015
 */
public class RecordHandler {

    private static RecordHandler recordHandler;

    protected String lastApprovalDate;
    protected String lastEmailDate;
    protected String lastIncidentDate;
    protected String lastRequestDate;

    protected List<ApprovalRecord> approvalRecordList;
    protected List<EmailRecord> emailRecordList;
    protected List<IncidentRecord> incidentRecordList;
    protected List<RequestRecord> requestRecordList;

    private RecordHandler() {
        // Create empty list of Records for each Record type
        approvalRecordList = new ArrayList<ApprovalRecord>();
        emailRecordList = new ArrayList<EmailRecord>();
        incidentRecordList = new ArrayList<IncidentRecord>();
        requestRecordList = new ArrayList<RequestRecord>();

        // Create empty dates for each Record type, so a query will return all records
        lastApprovalDate = "0";
        lastEmailDate = "0";
        lastIncidentDate = "0";
        lastRequestDate = "0";
    }

    public static RecordHandler getInstance() {
        if (recordHandler == null)
            recordHandler = new RecordHandler();

        return recordHandler;
    }



    public String getLastApprovalDate() {
        return lastApprovalDate;
    }

    public void setLastApprovalDate(String date) {
        lastApprovalDate = date;
    }







    public static String getHeader(HttpResponse response, String key) {
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equals(key)) {
                return header.getValue();
            }
        }

        return null;
    }
}

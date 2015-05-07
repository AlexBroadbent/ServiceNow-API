package com.abroadbent.servicenowapi.model.exception;

/**
 * @author      alexander.broadbent
 * @version     16/02/2015
 */
public class NoSingleRecordFoundException extends Exception {

    public NoSingleRecordFoundException() {
        super();
    }

    public NoSingleRecordFoundException(String message) {
        super(message);
    }

}

package com.abroadbent.servicenowapi.model.exception;

/**
 * @author alexander.broadbent
 * @version 12/02/2015
 */
public class NoRecordFoundException extends Exception {

    public NoRecordFoundException() {
        super();
    }

    public NoRecordFoundException(String message) {
        super(message);
    }
}

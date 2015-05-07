package com.abroadbent.servicenowapi.model;

/**
 *  Contains static constants for the whole application
 *
 * @author      alexander.broadbent
 * @version     19/12/2014
 */
public interface AppConstants {

    /* SHARED PREFERENCES */
    public static final String SHARED_PREF_NAME             = "service_now";
    public static final String PREF_INSTANCE_NAME           = "instance_name";
    public static final String PREF_INSTANCE_URL            = "instance_url";
    public static final String PREF_INSTANCE_PORT           = "instance_port";
    public static final String BASE_URL                     = "base_url";
    public static final String BASE_API_URL                 = "base_api_url";
    public static final String PREF_LAST_USER_LOGIN         = "last_user_login";

    public static final String USER_API_USERNAME            = "user_api_username";
    public static final String USER_API_PASSWORD            = "user_api_password";
    public static final String USER_API_BASIC_AUTH          = "user_api_basic_auth";
    public static final String USER_API_COOKIE              = "user_api_cookie";
    public static final String USER_SYS_ID                  = "user_sys_id";
    public static final String API_COOKIE_HEADER            = "Set-Cookie";

    /* DEMO SETTINGS */
    public static final String DEMO_INSTANCE_NAME           = "demonightlyuk";
    public static final String DEMO_INSTANCE_BASE_URL       = "https://demonightlyuk.service-now.com/$m.do";
    public static final String DEMO_INSTANCE_BASE_API_URL   = "https://demonightlyuk.service-now.com";

    /* API TABLE NAMES */
    public static final String TABLE_APPROVAL               = "sysapproval_approver";
    public static final String TABLE_INCIDENT               = "incident";


    /* API RESPONSE XML ELEMENT TAGS - FROM ApiResponseRecord (returned in all result types) */
    public static final String SYS_ID                       = "sys_id";
    public static final String RESPONSE                     = "response";
    public static final String RESULT                       = "result";
    public static final String STATS                        = "stats";
    public static final String COUNT                        = "count";
    public static final String SYS_CREATED_ON               = "sys_created_on";
    public static final String SYS_UPDATED_ON               = "sys_updated_on";
    public static final String STATE                        = "state";
    public static final String ACTIVE                       = "active";
    public static final String SYS_CREATED_BY               = "sys_created_by";
    public static final String SYS_UPDATED_BY               = "sys_updated_by";
    public static final String COMMENTS                     = "comments";
    public static final String ORDER                        = "order";

    /* APPROVAL API RESPONSE XML ELEMENT TAGS */
    public static final String APPROVER                     = "approver";
    public static final String APPROVER_LINK                = "link";           // approver->link
    public static final String SYS_APPROVAL                 = "sysapproval";
    public static final String SYS_APPROVAL_LINK            = "link";


    /* USER API RESPONSE XML ELEMENT TAGS */
    public static final String FIRST_NAME                   = "first_name";
    public static final String LAST_NAME                    = "last_name";
    public static final String USER_NAME                    = "user_name";
    public static final String LOCKED_OUT                   = "locked_out";

    /* TASK API RESPONSE XML ELEMENT TAGS */
    public static final String SHORT_DESCRIPTION            = "short_description";
    public static final String DESCRIPTION                  = "description";
    public static final String PRIORITY                     = "priority";



    //TODO: Throw error when a shared preference doesn't exist
    public static final String THROW_ERROR                  = "throw_error";

}

package com.abroadbent.servicenowapi.controller;

import android.text.TextUtils;
import android.util.Log;

import com.abroadbent.servicenowapi.model.AppConstants;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 *  Handles a connection to the server, by using the same client the server-side connection
 *      stays authenticated from login
 *
 *  Also handles AsyncTasks to stop deadlock
 *
 * @author      alexander.broadbent
 * @version     04/02/2015
 */
public class ConnectionHandler implements AppConstants {
    private static final String LOG_TAG = "ConnectionHeader";

    private static HttpClient client = null;
    private static CookieManager cookieManager = null;

    private ConnectionHandler() {}

    /**
     *
     * @return
     */
    public static HttpClient getInstance() {
        if (client == null)
            client = new DefaultHttpClient();

        if (cookieManager == null)
            cookieManager = new CookieManager();

        return client;
    }

    /**
     *
     * @param headers
     */
    public static void addCookies(Map<String, List<String>> headers) {

        try {
            cookieManager.put(null, headers);
        }
        catch (IOException ex) {
            Log.e(LOG_TAG, "Could not add headers: " + headers);
        }
    }

    /**
     *
     * @param header
     */
    public static void addCookie(Header header) {
        if (header.getName().equalsIgnoreCase(API_COOKIE_HEADER)) {
            cookieManager.getCookieStore().add(null, HttpCookie.parse(header.getValue()).get(0));
        }
    }

    /**
     *
     * @return
     */
    public static String getCookies() {
        return TextUtils.join(",", cookieManager.getCookieStore().getCookies());
    }

    /**
     *  Returns a <code>HttpGet</code> object that contains the appropriate headers for connection
     *      to the server
     *
     * @param connectUrl
     * @param authHeader
     * @return
     */
    public static HttpGet getRequest(String connectUrl, String authHeader) {
        HttpGet request = new HttpGet(connectUrl);

        request.setHeader("Accept", "application/xml");
        request.setHeader("Content-Type", "application/xml");

        // Add cookie or basic authentication
        if (cookieManager != null && cookieManager.getCookieStore().getCookies().size() > 0)
            request.setHeader("Cookie", getCookies());
        else
            request.setHeader("Authorization", "Basic " + authHeader);

        return request;
    }
}

package com.intellibins.intellibinsandroid;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class IntellbinsUrls {
    private static final String HTTP = "https://";
    private static final String HOST = "intellibin.herokuapp.com";
    private static final String BASE_URL = HTTP + HOST;
    private static final String CLIENT = "/client";
    private static final String BINS = "/bins";
    private static final String ROUTE_CLIENT = BASE_URL + CLIENT;
    private static final String BEGIN_PARAM = "?";
    private static final String ARG_INDEX = "index=";
    private static final String ARG_COUNT = "count=";
    private static final String UPDATE = "/update";
    public static String getBinsUrl(int index, int count) {
        return ROUTE_CLIENT + BINS + BEGIN_PARAM + ARG_INDEX + Integer.toString(index) + "&" + ARG_COUNT + Integer.toString(count);
    }
}

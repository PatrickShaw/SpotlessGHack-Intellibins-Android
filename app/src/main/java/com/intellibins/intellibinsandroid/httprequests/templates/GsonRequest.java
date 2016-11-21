package com.intellibins.intellibinsandroid.httprequests.templates;

import com.android.volley.Response;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */
public class GsonRequest<T> extends HeterogeneousGsonRequest<T, T> {
    public GsonRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> responseBodyClazz, T requestBody) {
        super(url, listener, errorListener, responseBodyClazz, responseBodyClazz, requestBody);
    }

    public GsonRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener, Class<T> responseBodyClazz, T requestBody) {
        super(method, url, listener, errorListener, responseBodyClazz, responseBodyClazz, requestBody);
    }
}
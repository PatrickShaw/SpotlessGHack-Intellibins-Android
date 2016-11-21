package com.intellibins.intellibinsandroid.httprequests.templates;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public class AuthorizedGsonRequest<T> extends GsonRequest<T> {
    public AuthorizedGsonRequest(
            String url,
            Response.Listener<T> listener,
            Response.ErrorListener errorListener,
            Class<T> clazz,
            T requestBody) {
        super(url, listener, errorListener, clazz, requestBody);
    }
    public AuthorizedGsonRequest(
            int method,
            String url,
            Response.Listener<T> listener,
            Response.ErrorListener errorListener,
            Class<T> clazz,
            T requestBody) {
        super(method, url, listener, errorListener, clazz, requestBody);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String,String> headers = new HashMap<>();
        // TODO:
        return headers;
    }
}

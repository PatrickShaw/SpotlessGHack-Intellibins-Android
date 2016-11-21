package com.intellibins.intellibinsandroid.httprequests.templates;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public class AuthorizedHeterogeneousGsonRequest<Q, R> extends HeterogeneousGsonRequest<Q, R> {
    public AuthorizedHeterogeneousGsonRequest(
            String url,
            Response.Listener<R> listener,
            Response.ErrorListener errorListener,
            Class<Q> requestBodyClazz,
            Class<R> responseBodyClazz,
            Q requestBody) {
        super(  url,
                listener,
                errorListener,
                requestBodyClazz,
                responseBodyClazz,
                requestBody);
    }

    public AuthorizedHeterogeneousGsonRequest(
            int method,
            String url,
            Response.Listener<R> listener,
            Response.ErrorListener errorListener,
            Class<Q> requestBodyClazz,
            Class<R> responseBodyClazz,
            Q requestBody) {
        super(
                method,
                url,
                listener,
                errorListener,
                requestBodyClazz,
                responseBodyClazz,
                requestBody);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String,String> headers = new HashMap<>();
        // TODO:
        return headers;
    }
}

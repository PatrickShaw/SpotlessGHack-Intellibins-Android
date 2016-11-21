package com.intellibins.intellibinsandroid.httprequests.data;

import com.android.volley.Request;
import com.android.volley.Response;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public abstract class RetrievalRequestHandler<R> extends RequestHandler<Void, R> {
    protected abstract Request<R> createRequest(Response.Listener<R> listener, Response.ErrorListener errorListener);
    @Override
    protected final Request<R> createRequest(Response.Listener<R> listener, Response.ErrorListener errorListener, Void requestBody) {
        return createRequest(listener, errorListener);
    }
}

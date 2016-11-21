package com.intellibins.intellibinsandroid.httprequests.data;

import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.intellibins.intellibinsandroid.ErrorHandler;
import com.intellibins.intellibinsandroid.IntellibinsApplication;
import com.intellibins.intellibinsandroid.httprequests.VolleyErrorUtils;
import com.intellibins.intellibinsandroid.status.Status;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public abstract class RequestHandler<Q, R> {
    private final Status mIsRequesting = Status.getInstance();
    @Nullable
    private ErrorHandler mRequestErrorHandler;
    public boolean getStatus() {
        return mIsRequesting.getStatus();
    }

    public void setStatusListener(Status.OnStatusChangeListener statusListener) {
        mIsRequesting.setStatusListener(statusListener);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        mRequestErrorHandler = errorHandler;
    }

    public void forceStatusUpdate(boolean status) {
        mIsRequesting.setStatus(status, true);
    }

    public void request(String tag, @Nullable Q requestBody) {
        mIsRequesting.setStatus(true, false);
        Request<R> request = createRequest(
                this::onRequestFinished,
                error -> {
                    mIsRequesting.setStatus(false, true);
                    VolleyErrorUtils.handleVolleyError(
                            error,
                            () -> request(tag, requestBody),
                            mRequestErrorHandler,
                            getItemName(),
                            getErrorMessageFormat()
                    );
                },
                requestBody
        );
        IntellibinsApplication.getInstance().cancelPendingRequests(tag);
        IntellibinsApplication.getInstance().addToRequestQueue(request, tag);
    }

    protected String getErrorMessageFormat() {
        return VolleyErrorUtils.DEFAULT_ERROR_MESSAGE_FORMAT;
    }

    public abstract String getItemName();

    protected abstract Request<R> createRequest(Response.Listener<R> listener, Response.ErrorListener errorListener, Q requestBody);

    private void onRequestFinished(R requestObject) {
        onDataRetrieved(requestObject);
        mIsRequesting.setStatus(false, true);
    }

    public abstract void onDataRetrieved(R requestObject);

    public void onDetach() {
        mIsRequesting.onDetach();
        mRequestErrorHandler = null;
    }
}

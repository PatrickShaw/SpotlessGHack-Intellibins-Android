package com.intellibins.intellibinsandroid.httprequests.templates;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public class HeterogeneousGsonRequest<Q, R> extends Request<R> {
    private final Gson gson = new Gson();
    private final Class<Q> mRequestBodyClazz;
    private final Class<R> mResponseBodyClazz;
    private final Q mRequestBody;
    private final Response.Listener<R> mListener;
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    /**
     * Make a GET request and return a parsed object from JSON.
     *  @param url URL of the request to make
     * @param responseBodyClazz Relevant class object, for Gson's reflection
     */
    public HeterogeneousGsonRequest(
            String url,
            Response.Listener<R> listener,
            Response.ErrorListener errorListener,
            Class<Q> requestBodyClazz,
            Class<R> responseBodyClazz,
            Q requestBody) {
        this(
                Request.Method.GET,
                url,
                listener,
                errorListener,
                requestBodyClazz,
                responseBodyClazz,
                requestBody);
    }

    public HeterogeneousGsonRequest(int method,
                                    String url,
                                    Response.Listener<R> listener, Response.ErrorListener errorListener, Class<Q> requestBodyClazz, Class<R> responseBodyClazz, Q requestBody){
        super(method, url, errorListener);
        this.mRequestBody = requestBody;
        this.mListener = listener;
        this.mRequestBodyClazz = requestBodyClazz;
        this.mResponseBodyClazz = responseBodyClazz;
    }

    @Override
    protected Response<R> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d(getClass().getSimpleName(), json);
            return Response.success(
                    gson.fromJson(json, mResponseBodyClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getSimpleName(),"Unsupported encoding",e);
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            Log.e(getClass().getSimpleName(),"Json syntax", e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(R response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            Log.d(this.getClass().getSimpleName(), gson.toJson(mRequestBody, mRequestBodyClazz));
            return mRequestBody == null ? "{}".getBytes(PROTOCOL_CHARSET) : gson.toJson(mRequestBody, mRequestBodyClazz).getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}

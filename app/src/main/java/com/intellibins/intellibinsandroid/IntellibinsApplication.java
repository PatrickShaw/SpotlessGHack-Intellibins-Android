package com.intellibins.intellibinsandroid;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import okhttp3.OkHttpClient;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class IntellibinsApplication extends MultiDexApplication {
    public static final String TAG = IntellibinsApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static IntellibinsApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static synchronized IntellibinsApplication getInstance() {
        return mInstance;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack(new OkHttpClient()));
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,//DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                        0,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        ); // TODO: Change the policy one day
        getRequestQueue().add(req);
        Log.d(tag, req.getUrl());
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        addToRequestQueue(req, TAG);
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequests(String... tags) {
        if(mRequestQueue== null)
            return;
        for(String i : tags)
            cancelPendingRequests(i);
    }

}

package com.intellibins.intellibinsandroid.status;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.intellibins.intellibinsandroid.IntellibinsApplication;
import com.intellibins.intellibinsandroid.OnDataFragmentStatusChangeListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class StatusFragment extends Fragment {
    protected static class FragmentStatusManager {
        protected static class FragmentStatusWrapper {
            protected FragmentStatusWrapper(Status status, boolean useDefaultStatusListener) {
                mStatus = status;
                mUseDefaultStatusListener = useDefaultStatusListener;
            }
            final Status mStatus;
            final boolean mUseDefaultStatusListener;
        }
        public FragmentStatusWrapper newInstance(Status status, boolean useDefaultStatusListener) {
            return new FragmentStatusWrapper(status, useDefaultStatusListener);
        }
        public static FragmentStatusManager newInstance() {
            return new FragmentStatusManager();
        }
    }
    private boolean mIsCreated;
    private FragmentStatusManager mFragmentStatusManager = newFragmentStatusManager();
    private LinkedList<FragmentStatusManager.FragmentStatusWrapper> mStatuses = new LinkedList<>();
    private HashSet<String> mRequestTags = new HashSet<>();
    private final HashMap<String, Request> mRequestQueue = new HashMap<>();
    protected final Status registerStatus(boolean useDefaultStatusListener) {
        Status status = Status.getInstance();
        mStatuses.add(mFragmentStatusManager.newInstance(status, useDefaultStatusListener));
        return status;
    }
    protected final Status registerStatus() {
        return registerStatus(true);
    }
    protected void putRequest(Request request, Status status, String requestTag) {
        Log.d("StatusFragment", "Putting request: " + requestTag);
        status.setStatus(true, false);
        synchronized (mRequestQueue) {
            if (mIsCreated) {
                IntellibinsApplication.getInstance().cancelPendingRequests(requestTag);
                IntellibinsApplication.getInstance().addToRequestQueue(request);
                mRequestQueue.remove(requestTag);
            } else {
                mRequestQueue.put(requestTag, request);
            }
        }
    }

    protected Response.ErrorListener newErrorListener(Response.ErrorListener errorListener, Status status, String requestTag) {
        return error -> {
            status.setStatus(false, false);
            mRequestTags.remove(requestTag);
            errorListener.onErrorResponse(error);
        };
    }
    protected <T>Response.Listener<T> newStatusListener(Response.Listener<T> originalListener, Status status, boolean forceUpdate, String requestTag) {
        return item -> {
            mRequestTags.remove(requestTag);
            originalListener.onResponse(item);
            status.setStatus(false, forceUpdate);
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = true;
        for(Map.Entry<String, Request> request : mRequestQueue.entrySet()) {
            IntellibinsApplication.getInstance().addToRequestQueue(request.getValue(), request.getKey());
        }
        mRequestQueue.clear();
        setRetainInstance(true);
        for(FragmentStatusManager.FragmentStatusWrapper statusWrapper : mStatuses) {
            Status status = statusWrapper.mStatus;
            status.setStatus(status.getStatus(), true);
        }
    }

    protected <T>Response.Listener<T> newStatusListener(Response.Listener<T> originalListener, Status status, String requestTag) {
        return newStatusListener(originalListener, status, false, requestTag);
    }
    protected FragmentStatusManager newFragmentStatusManager() {
        return FragmentStatusManager.newInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnDataFragmentStatusChangeListener) {
            OnDataFragmentStatusChangeListener defaultStatusListener = (OnDataFragmentStatusChangeListener) context;
            for(FragmentStatusManager.FragmentStatusWrapper statusWrapper : mStatuses) {
                if (statusWrapper.mUseDefaultStatusListener) {
                    statusWrapper.mStatus.setStatusListener(status -> defaultStatusListener.onDataFragmentStatusChangeListener(isDetached()));
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for(FragmentStatusManager.FragmentStatusWrapper status : mStatuses) {
            status.mStatus.onDetach();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsCreated = false;
        for(String i : mRequestTags) {
            IntellibinsApplication.getInstance().cancelPendingRequests(i);
        }
    }
}

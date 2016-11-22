package com.intellibins.intellibinsandroid.data.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.intellibins.intellibinsandroid.ErrorHandler;
import com.intellibins.intellibinsandroid.HasData;
import com.intellibins.intellibinsandroid.IntellibinsApplication;
import com.intellibins.intellibinsandroid.IsRefreshing;
import com.intellibins.intellibinsandroid.OnDataFragmentStatusChangeListener;
import com.intellibins.intellibinsandroid.httprequests.VolleyErrorUtils;
import com.intellibins.intellibinsandroid.httprequests.data.RetrievalRequestHandler;
import com.intellibins.intellibinsandroid.status.Status;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public abstract class DataFragment<T> extends Fragment implements HasData, IsRefreshing {
    OnDataFragmentStatusChangeListener mOnChangeListener;
    protected String getErrorMessageFormat() {
        return VolleyErrorUtils.DEFAULT_ERROR_MESSAGE_FORMAT;
    }
    private RetrievalRequestHandler<T> mRefreshDataHandler = new RetrievalRequestHandler<T>() {
        @Override
        public String getItemName() {
            return getRetrieveDataItemName();
        }

        @Override
        protected String getErrorMessageFormat() {
            return DataFragment.this.getErrorMessageFormat();
        }

        @Override
        protected Request<T> createRequest(Response.Listener<T> listener, Response.ErrorListener errorListener) {
            return getDataRequest(listener, errorListener);
        }

        @Override
        public void onDataRetrieved(T requestObject) {
            Log.d("DataFragment", getClass().getSimpleName() + " retrieved data from the Intellibins server");
            mData = requestObject;
            if (mIsCreatedView) {
                notifyDataRetrieved(requestObject);
            }
        }
    };
    private T mData;
    /**
     * Every time you think that it would be a good idea to make a request before the
     * fragment is created a baby giraffe dies.
     */
    private boolean mIsCreated;
    private boolean mIsCreatedView;
    private boolean mRefreshOnCreation;
    private boolean mForceRefresh;

    public void setRefreshErrorHandler(ErrorHandler errorHandler) {
        mRefreshDataHandler.setErrorHandler(errorHandler);
    }

    public boolean isRefreshing() {
        return mRefreshDataHandler.getStatus();
    }

    public void clearData() {
        mData = null;
    }

    public final boolean isEmpty() {
        return !hasData();
    }

    public boolean hasData() {
        return mData != null;
    }

    public void setServerSideData(T data) {
        if(data == null)
            return;
        mData = data;
        notifyDataRetrieved(data);
    }

    public abstract void notifyDataRetrieved(T data);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = true;
        setRetainInstance(true);
        if(mRefreshOnCreation) {
            mRefreshOnCreation = true;
            refreshData(mForceRefresh);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsCreatedView = true;
        if (mData != null) {
            notifyDataRetrieved(mData);
        }
        // Being created counts as a status change
        if (mOnChangeListener != null) {
            // Being created counts as a status change
            mOnChangeListener.onDataFragmentStatusChangeListener(true);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    public T getServerData() {
        return mData;
    }


    public void refreshData(boolean forceRefresh) {
        if(!mIsCreated) {
            Log.d("DataFragment", getClass().getSimpleName() + " has not created yet. Remembering to refresh on fragment creation.");
            mForceRefresh = forceRefresh;
            mRefreshOnCreation = true;
            return;
        }
        if(hasData() && !forceRefresh) {
            mRefreshDataHandler.forceStatusUpdate(false);
            Log.d("DataFragment", getClass().getSimpleName() + " has data already & not forced to refresh. Not refreshing.");
            return;
        }
        Log.d("DataFragment", getClass().getSimpleName() + " is refreshing | forced: " + Boolean.toString(forceRefresh));
        mRefreshDataHandler.request(getDataRequestTag(), null);
    }

    protected abstract String getRetrieveDataItemName();

    @CallSuper
    public void onRegisterWithDefaultStatusListener(Status.OnStatusChangeListener defaultListener) {
        mRefreshDataHandler.setStatusListener(defaultListener);
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnDataFragmentStatusChangeListener) {
            OnDataFragmentStatusChangeListener onStatusChangeListener = (OnDataFragmentStatusChangeListener) context;
            onRegisterWithDefaultStatusListener(status -> onStatusChangeListener.onDataFragmentStatusChangeListener(isDetached()));
            mOnChangeListener=  onStatusChangeListener;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRefreshDataHandler.onDetach();
    }

    @Override
    public void onDestroyView() {
        mIsCreatedView = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mIsCreated = false;
        IntellibinsApplication.getInstance().cancelPendingRequests(getDataRequestTag());
        super.onDestroy();
    }

    public abstract String getDataRequestTag();
    protected abstract Request<T> getDataRequest(Response.Listener<T> responseRetrievedListener, Response.ErrorListener errorListener);
}

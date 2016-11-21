package com.intellibins.intellibinsandroid.data.fragments.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intellibins.intellibinsandroid.ErrorHandler;
import com.intellibins.intellibinsandroid.HasData;
import com.intellibins.intellibinsandroid.OnDataFragmentStatusChangeListener;
import com.intellibins.intellibinsandroid.listhandler.IdentifiableListHandler;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public abstract class ListDataFragment<T extends IdentifiableListHandler> extends Fragment implements HasData {
    OnDataFragmentStatusChangeListener mOnDataFragmentStatusChangeListener;
    private T mListHandler;
    private ErrorHandler mLoadMoreItemsErrorHandler;
    private boolean mRefreshOnCreation = false;
    private boolean mRefreshClear = false;
    private boolean mIsCreated;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsCreated = true;
        if(mOnDataFragmentStatusChangeListener != null)
            mOnDataFragmentStatusChangeListener.onDataFragmentStatusChangeListener(true);
        getListHandler().setOnErrorMessageRetrievedHandler(mLoadMoreItemsErrorHandler);
        if (mRefreshOnCreation) {
            Log.d("ListDataFragment", getClass().getSimpleName() + " remembered to refresh.");
            getListHandler().refreshItems(mRefreshClear);
            mRefreshOnCreation = false;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setLoadMoreItemsErrorHandler(ErrorHandler onLoadMoreItemsErrorHandler) {
        mLoadMoreItemsErrorHandler = onLoadMoreItemsErrorHandler;
        getListHandler().setOnErrorMessageRetrievedHandler(onLoadMoreItemsErrorHandler);
    }

    @NonNull
    protected abstract T createListHandler();

    public synchronized T getListHandler() {
        if(mListHandler == null) {
            mListHandler = createListHandler();
        }
        return mListHandler;
    }

    public void refreshItems(boolean clearItems) {
        /*
        Note: Currently using lazy loading but I think we should keep the list handler within the fragment life cycle.
        if(!mIsCreated) {
            L.d("ListDataFragment", getClass().getSimpleName() + " has not finished creating yet. Remembering to load once finished.");
            mRefreshClear = clearItems;
            mRefreshOnCreation = true;
            return;
        }*/
        mRefreshOnCreation = false;
        getListHandler().refreshItems(clearItems);
    }

    @Override
    public void onDestroy() {
        mRefreshClear = false;
        mIsCreated = false;
        getListHandler().cancelAllRequests();
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnDataFragmentStatusChangeListener) {
            mOnDataFragmentStatusChangeListener = (OnDataFragmentStatusChangeListener) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        getListHandler().onDetach();
        mLoadMoreItemsErrorHandler = null;
        mOnDataFragmentStatusChangeListener = null;
        super.onDetach();
    }

    @Override
    public boolean hasData() {
        return getListHandler() != null && (getListHandler().size() > 0 || getListHandler().isReachedEnd());
    }
}

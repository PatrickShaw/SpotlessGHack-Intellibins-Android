package com.intellibins.intellibinsandroid.listhandler;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.intellibins.intellibinsandroid.ErrorHandler;
import com.intellibins.intellibinsandroid.HasData;
import com.intellibins.intellibinsandroid.IntellibinsApplication;
import com.intellibins.intellibinsandroid.httprequests.VolleyErrorUtils;
import com.intellibins.intellibinsandroid.httprequests.templates.AuthorizedGsonRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.UnaryOperator;

/**
 * The list handler manages requests and responses of requests for linear list streams
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */
// TODO: Actually make it a fragment
public abstract class ListHandler<T, V> implements List<T>, HasData {
    public interface OnItemMovedListener {
        void onItemMoved(int newIndex, int oldIndex);
    }
    public interface OnItemRangeInsertedListener {
        void onItemRangeInserted(int startingIndex, int count);
    }
    public interface OnReachedEndListener {
        void onReachedEnd();
    }
    public interface OnItemsRemovedListener {
        void onItemsRemoved(int startingIndex, int count);
    }
    public interface OnRefreshingChangedListener {
        void onRefreshingChanged(boolean isRefreshing);
    }
    public interface OnStatusChangedListener {
        void onStatusChanged(boolean isLoading);
    }
    public interface OnItemRangeChangedListener {
        void onItemRangeChanged(int startingIndex, int count);
    }
    public interface OnDataSetChangedListener {
        void onDataSetChanged();
    }
    private ArrayList<T> mValues;
    private final List<OnItemMovedListener> mOnItemMovedListeners = new LinkedList<>();
    private final List<OnItemRangeInsertedListener> mOnItemRangeInsertedListeners = new LinkedList<>();
    private final List<OnReachedEndListener> mReachedEndListeners = new LinkedList<>();
    private final List<OnItemsRemovedListener> mOnItemsRemovedListeners = new LinkedList<>();
    private final List<OnStatusChangedListener> mOnStatusChangedListeners = new LinkedList<>();
    private final List<OnRefreshingChangedListener> mOnRefreshingChangedListeners = new LinkedList<>();
    private final List<OnItemRangeChangedListener> mOnItemRangeChangedListener = new LinkedList<>();
    private final List<OnDataSetChangedListener> mOnDataSetChangedListener = new LinkedList<>();
    private boolean mIsLoading;
    private boolean mReachedEnd;
    private final Class<V> mClazz;
    private ErrorHandler mErrorHandlerHandler;

    protected ErrorHandler getOnErrorMessageRetrievedHandler(){
        return mErrorHandlerHandler;
    }
    public ListHandler(Class<V> clazz, ArrayList<T> values) {
        mValues = values == null ? new ArrayList<>() : values;
        mClazz = clazz;
    }
    public ListHandler(Class<V> clazz) {
        mClazz = clazz;
        mValues = new ArrayList<>();
    }
    public final void onDestroy() {

    }
    @CallSuper
    public void unregisterAllListeners() {
        mOnItemMovedListeners.clear();
        mOnItemRangeInsertedListeners.clear();
        mReachedEndListeners.clear();
        mOnItemsRemovedListeners.clear();
        mOnStatusChangedListeners.clear();
        mOnRefreshingChangedListeners.clear();
        mOnItemRangeChangedListener.clear();
        mOnDataSetChangedListener.clear();
        mErrorHandlerHandler = null;
    }

    public void onDetach() {
        unregisterAllListeners();
    }
    // TODO: Technically, if something is done asynchronously something is going to fail. Be careful, check the children of this class to make sure they aren't removing & adding things asynchronously
    public final void setOnItemMovedListener(OnItemMovedListener listener){
        synchronized (mOnItemMovedListeners) {
            mOnItemMovedListeners.clear();
            mOnItemMovedListeners.add(listener);
        }
    }
    public final void setOnItemRangeInsertedListener(OnItemRangeInsertedListener listener) {
        synchronized (mOnItemRangeInsertedListeners) {
            mOnItemRangeInsertedListeners.clear();
            mOnItemRangeInsertedListeners.add(listener);
        }
    }
    public final void setOnItemRangeChangedListener(OnItemRangeChangedListener listener) {
        synchronized (mOnItemRangeChangedListener) {
            mOnItemRangeChangedListener.clear();
            mOnItemRangeChangedListener.add(listener);
        }
    }
    public final void setOnItemRangeRemoved(OnItemsRemovedListener listener) {
        synchronized (mOnItemsRemovedListeners) {
            mOnItemsRemovedListeners.clear();
            mOnItemsRemovedListeners.add(listener);
        }
    }
    public final void setOnReachedEndListener(OnReachedEndListener listener){
        synchronized (mReachedEndListeners) {
            mReachedEndListeners.clear();
            mReachedEndListeners.add(listener);
        }
    }
    public final void setOnStatusChangedListener(OnStatusChangedListener listener){
        synchronized (mOnStatusChangedListeners) {
            mOnStatusChangedListeners.clear();
            mOnStatusChangedListeners.add(listener);
        }
    }
    public final void setOnRefreshChangedListener(OnRefreshingChangedListener listener){
        synchronized (mOnRefreshingChangedListeners) {
            mOnRefreshingChangedListeners.clear();
            mOnRefreshingChangedListeners.add(listener);
        }
    }
    public final void setOnDataSetChangedListener(OnDataSetChangedListener listener) {
        synchronized (mOnDataSetChangedListener) {
            mOnDataSetChangedListener.clear();
            mOnDataSetChangedListener.add(listener);
        }
    }
    protected synchronized void clearListItems() {
        int originalItemCount = mValues.size();
        mValues.clear();
        synchronized (mOnItemsRemovedListeners) {
            for (OnItemsRemovedListener i : mOnItemsRemovedListeners) {
                if (i != null) {
                    i.onItemsRemoved(0, originalItemCount);
                } else {
                    Log.e("ListHandler","on items removed listener was null");
                }
            }
        }
    }

    protected void onRefreshItemsRetrieved(
            V response,
            int countRequested) {
        clearListItems();
        setIsRefreshing(false);
        setReachedEnd(false);
        onLoadMoreItemsRetrieved(response, countRequested);
    }
    /**
     * Adds items from a listener
     * @param response
     * The items being added to the list handler
     * @param countRequested
     * The number of items that were requested
     */
    protected synchronized void onLoadMoreItemsRetrieved(
            V response, int countRequested)
    {
        addAll(response);
        checkItemsReachedEnd(response, countRequested);
    }
    protected synchronized void setIsLoadingNextPage(boolean isLoading) {
        mIsLoading = isLoading;
        synchronized (mOnStatusChangedListeners) {
            for (OnStatusChangedListener i : mOnStatusChangedListeners) {
                if (i != null) {
                    i.onStatusChanged(isLoading);
                } else {
                    Log.e("ListHandler", "OnStatusChangedListener was null");
                }
            }
        }
    }
    public void refreshItems(boolean clearItems) {
        IntellibinsApplication.getInstance().cancelPendingRequests(getLoadMoreItemsTag());
        if (clearItems) {
            clearListItems();
        }
        setReachedEnd(false);
        setIsRefreshing(true);
        setIsLoadingNextPage(true);
        AuthorizedGsonRequest<V> refreshRequest = new AuthorizedGsonRequest<>(
                getRefreshUrl(getItemsPerPage()),
                (response) -> onRefreshItemsRetrieved(
                        response,
                        getItemsPerPage()
                ),
                error -> VolleyErrorUtils.handleVolleyError(
                        error,
                        () -> refreshItems(clearItems),
                        mErrorHandlerHandler,
                        getItemName()
                ),
                mClazz,
                null
        );
        IntellibinsApplication.getInstance().addToRequestQueue(refreshRequest, getLoadMoreItemsTag());
    }

    @Override
    public boolean hasData() {
        return size() > 0;
    }

    public boolean isEmpty() {
        return !hasData();
    }

    protected void setIsRefreshing(boolean isRefreshing){
        synchronized (mOnRefreshingChangedListeners) {
            for (OnRefreshingChangedListener listener : mOnRefreshingChangedListeners) {
                if (listener != null) {
                    listener.onRefreshingChanged(isRefreshing);
                } else {
                    Log.e("ListHandler", "on refreshing changed listener was null");
                }
            }
        }
    }
    public void loadMoreItems() {
        if(mReachedEnd || mIsLoading) {
            return;
        }
        setIsLoadingNextPage(true);
        AuthorizedGsonRequest<V> loadMoreRequest = new AuthorizedGsonRequest<>(
                getNextPage(),
                (response) -> onLoadMoreItemsRetrieved(response, getItemsPerPage()),
                error -> VolleyErrorUtils.handleVolleyError(
                error,
                this::loadMoreItems,
                        mErrorHandlerHandler,
                getItemName()
        ),
                mClazz,
                null
        );
        IntellibinsApplication.getInstance().addToRequestQueue(loadMoreRequest, getLoadMoreItemsTag());
    }

    protected void notifyItemsRangeInserted(int startingIndex, int count){
        for(OnItemRangeInsertedListener i : mOnItemRangeInsertedListeners) {
            if (i == null) {
                Log.e("ListHandler", "ItemRangeInsertedListener was null");
            } else {
                i.onItemRangeInserted(startingIndex, count);
            }
        }
    }

    protected void notifyItemsRangeRemoved(int startingIndex, int count){
        for(OnItemsRemovedListener i: mOnItemsRemovedListeners) {
            if (i == null) {
                Log.e("ListHandler", "OnItemsRemovedListener was null");
            } else {
                i.onItemsRemoved(startingIndex, count);
            }
        }
    }

    protected void notifyItemsRangeMoved(int fromIndex, int toIndex) {
        synchronized (mOnItemMovedListeners) {
            for (OnItemMovedListener i : mOnItemMovedListeners) {
                if (i == null) {
                    Log.e("ListHandler", "OnItemMovedListener was null");
                } else {
                    i.onItemMoved(fromIndex, toIndex);
                }
            }
        }
    }

    protected void notifyItemsRangeChanged(int startingIndex, int count) {
        synchronized (mOnItemRangeChangedListener) {
            for (OnItemRangeChangedListener i : mOnItemRangeChangedListener) {
                if (i == null) {
                    Log.e("ListHandler", "OnItemsRangeChangedListener was null");
                } else {
                    i.onItemRangeChanged(startingIndex, count);
                }
            }
        }
    }

    protected void setReachedEnd(boolean isReachedEnd){
        mReachedEnd = isReachedEnd;
        synchronized (mReachedEndListeners) {
            for (OnReachedEndListener mReachedEnd : mReachedEndListeners) {
                if (mReachedEnd != null) {
                    mReachedEnd.onReachedEnd();
                } else {
                    Log.e("ListHandler", "Reached end listener is null");
                }
            }
        }
    }

    protected void notifyDataSetChanged() {
        synchronized (mOnDataSetChangedListener) {
            for (OnDataSetChangedListener i : mOnDataSetChangedListener) {
                if (i != null) {
                    i.onDataSetChanged();
                } else {
                    Log.e("Listhandler", "Data set changed listener is null");
                }
            }
        }
    }
    protected abstract int getSize(V response);

    protected void checkItemsReachedEnd(V response, int countRequested){
        if(getSize(response) < countRequested)
            setReachedEnd(true);
    }
    public boolean isReachedEnd(){
        return mReachedEnd;
    }
    public final ArrayList<T> getList()
    {
        return mValues;
    }
    public int getItemsPerPage()
    {
        return 12;
    }
    public boolean isLoadingMoreItems()
    {
        return mIsLoading;
    }
    public void setOnErrorMessageRetrievedHandler(@Nullable ErrorHandler errorHandlerHandler){
        mErrorHandlerHandler = errorHandlerHandler;
    }
    @Nullable
    public ErrorHandler getLoadMoreErrorMessageErrorHandler() {
        return mErrorHandlerHandler;
    }
    public void cancelAllRequests(){
        IntellibinsApplication.getInstance().cancelPendingRequests(getLoadMoreItemsTag());
    }
    protected abstract void addAll(V response);
    public abstract String getNextPage();
    public abstract String getItemName();
    protected abstract String getLoadMoreItemsTag();
    protected abstract String getRefreshUrl(int count);

    public void removeAndNotify(int index) {
        remove(index);
        notifyItemsRangeRemoved(index, 1);
    }

    /**
     * TODO: It's a bit misleading that I've implemented {@link List} because although such methods will technically work,
     * they might cause weird bugs if you use, for example, 'iterator' the child classes have two containers.
     */

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Spliterator<T> spliterator() {
        return mValues.spliterator();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        mValues.replaceAll(operator);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sort(Comparator<? super T> c) {
        mValues.sort(c);
    }

    @Override
    public int size() {
        return mValues.size();
    }

    @Override
    public boolean contains(Object o) {
        return mValues.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return mValues.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[mValues.size()];
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] t1s) {
        return mValues.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return mValues.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return mValues.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mValues.containsAll(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mValues.addAll(collection);
    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends T> collection) {
        return mValues.addAll(i, collection);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return mValues.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return mValues.retainAll(collection);
    }

    @Override
    public void clear() {
        clearListItems();
    }

    @Override
    public T get(int i) {
        return mValues.get(i);
    }

    @Override
    public T set(int i, T t) {
        return mValues.set(i, t);
    }

    @Override
    public void add(int i, T t) {
        mValues.add(i, t);
    }

    @Override
    public T remove(int i) {
        return mValues.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return mValues.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mValues.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return mValues.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int i) {
        return mValues.listIterator(i);
    }

    @NonNull
    @Override
    public List<T> subList(int i, int i1) {
        return mValues.subList(i, i1);
    }
}

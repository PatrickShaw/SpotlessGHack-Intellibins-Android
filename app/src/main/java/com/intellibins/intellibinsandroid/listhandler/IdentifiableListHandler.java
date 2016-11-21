package com.intellibins.intellibinsandroid.listhandler;

import android.util.Log;

import com.intellibins.intellibinsandroid.MiscHelper;
import com.intellibins.intellibinsandroid.restdata.Identifiable;

import java.util.ArrayList;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */
public abstract class IdentifiableListHandler<T extends Identifiable, V> extends ListHandler<T, V> {
    private boolean isPolledRefresh = false;

    public IdentifiableListHandler(
            Class<V> clazz,
            ArrayList<T> values) {
        super(clazz, values);
    }
    public IdentifiableListHandler(
            Class<V> clazz) {
        super(clazz);
    }
    /**
     * Sames as the {@link ListHandler#onRefreshItemsRetrieved} except we utilise the fact that
     * the items are unique to each other. Specifically, we append the refreshed items to the
     * start of the list if we can see the previous, top most item in the list within the refreshed
     * items list.
     * If we don't see it, we revert to the {@link ListHandler#onRefreshItemsRetrieved} implementation.
     * Technically we could call the next set of items in the list but if we haven't refreshed in ages
     * that could go on for a very long time.
     * @param response
     * The response we retrieved from the request.
     * @param countRequested
     * The number of items that were requested
     */
    @Override
    public void onRefreshItemsRetrieved(V response, int countRequested) {
        if (getList().size() <= 0 || !isPolledRefresh) {
            // If there's nothing in the list then we don't need to worry about doing cool stuff
            // Just insert the data like we're loading more items
            super.onRefreshItemsRetrieved(response, countRequested);
        } else {
            // We already have things!
            // Let's check if we can find our current top item inside the refreshed list
            int newItemsCount = getResponseNewItemsCount(response);
            if (newItemsCount >= getSize(response))
                // We didn't find the top most item in the new items
                // We're going to have to use the old logic :( since we don't know where
                // the top most item is in the list anymore
                // TODO: If there was a way to check how many items there were above the current,
                // TODO: top most item then we could use that rather than trying to find it in the list
                super.onRefreshItemsRetrieved(response, countRequested);
            else {
                if (newItemsCount > 0) {
                    addPolledRange(response, 0, newItemsCount);
                }
                checkItemsReachedEnd(response, countRequested);
            }
            setIsRefreshing(false);
        }
    }

    @Override
    protected synchronized void onLoadMoreItemsRetrieved(V response, int countRequested) {
        super.onLoadMoreItemsRetrieved(response, countRequested);
    }


    protected final int getNewItemsCount(Identifiable[] items) {
        Identifiable currentItem = getList().get(0);
        int newItemsCount = 0;
        for(Identifiable item : items)
            if (item.getId().equals(currentItem.getId()))
                break;
            else
                newItemsCount++;
        return newItemsCount;
    }

    protected abstract void addPolledRange(V response, int startIndex, int count);
    protected abstract int getResponseNewItemsCount(V response);
    public final void setPolledRefresh(boolean isPolledRefresh){
        this.isPolledRefresh = isPolledRefresh;
    }

    public boolean removeAndNotify(T item, MiscHelper.GetIdentifiable<T> getIdentifiable, int possibleIndex, boolean refreshIfMissing) {
        int actualIndex = MiscHelper.propagationSearch(this, getIdentifiable, item, possibleIndex);
        return removeAndNotify(actualIndex, refreshIfMissing);
    }

    private boolean removeAndNotify(int actualIndex, boolean refreshIfMissing) {
        if (actualIndex <= -1) {
            Log.d("IdentifiableListHandler", getClass().getSimpleName() + " tried to remove an item but could not find it."+ " actualIndex: " + Integer.toString(actualIndex));
            if(refreshIfMissing) {
                refreshItems(false);
            }
            return false;
        }
        remove(actualIndex);
        notifyItemsRangeRemoved(actualIndex, 1);
        return true;
    }

    public boolean removeAndNotify(T item, int possibleIndex, boolean refreshIfMissing) {
        int actualIndex = MiscHelper.propagationSearch(this,item, possibleIndex);
        return removeAndNotify(actualIndex, refreshIfMissing);
    }
    public boolean removeAndNotify(T item, int possibleIndex) {
        return removeAndNotify(item, possibleIndex, false);
    }
    public boolean setAndNotify(T item, int possibleIndex) {
        int actualIndex = MiscHelper.propagationSearch(this,item, possibleIndex);
        if (actualIndex <= -1) {
            Log.d("IdentifiableListHandler", getClass().getSimpleName() + " tried to set an item but could not find it. possibleIndex: " + Integer.toString(possibleIndex) + " actualIndex: " + Integer.toString(actualIndex));
            return false;
        }
        set(actualIndex, item);
        notifyItemsRangeChanged(actualIndex, 1);
        return true;
    }
}

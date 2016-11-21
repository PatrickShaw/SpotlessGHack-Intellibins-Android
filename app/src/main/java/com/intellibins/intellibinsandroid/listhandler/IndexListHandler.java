package com.intellibins.intellibinsandroid.listhandler;

import com.intellibins.intellibinsandroid.restdata.Identifiable;

import java.util.ArrayList;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public abstract class IndexListHandler<T extends Identifiable, A> extends IdentifiableListHandler<T, A[]> {

    public IndexListHandler(Class<A[]> clazz, ArrayList<T> values) {
        super(clazz, values);
    }

    public IndexListHandler(Class<A[]> clazz) {
        super(clazz);
    }

    @Override
    public final String getNextPage() {
        return getNextPage(getList().size(), getItemsPerPage());
    }

    @Override
    protected String getRefreshUrl(int count) {
        return getNextPage(0, count);
    }

    public abstract String getNextPage(int index, int count);

    @Override
    protected int getSize(A[] response) {
        return response.length;
    }

    @Override
    protected synchronized void onLoadMoreItemsRetrieved(A[] response, int countRequested) {
        super.onLoadMoreItemsRetrieved(response, countRequested);
    }
}

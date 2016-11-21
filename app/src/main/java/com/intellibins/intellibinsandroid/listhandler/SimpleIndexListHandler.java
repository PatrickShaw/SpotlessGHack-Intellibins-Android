package com.intellibins.intellibinsandroid.listhandler;

import com.intellibins.intellibinsandroid.restdata.Identifiable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public abstract class SimpleIndexListHandler<T extends Identifiable> extends IndexListHandler<T,T>
{
    public SimpleIndexListHandler(Class<T[]> clazz, ArrayList<T> values) {
        super(clazz, values);
    }
    public SimpleIndexListHandler(
            Class<T[]> clazz) {
        super(clazz);
    }

    @Override
    protected void addAll(T[] response) {
        int originalIndex = getList().size();
        Collections.addAll(getList(), response);
        notifyItemsRangeInserted(originalIndex, response.length);
        setIsLoadingNextPage(false);
    }

    @Override
    protected synchronized void addPolledRange(T[] response, final int startIndex, final int count) {
        int originalIndex = getList().size();
        for(int i = startIndex; i < startIndex + count; i++) {
            getList().add(response[i]);
        }
        notifyItemsRangeInserted(originalIndex, count);
    }

    @Override
    protected int getResponseNewItemsCount(T[] response) {
        return super.getNewItemsCount(response);
    }
}

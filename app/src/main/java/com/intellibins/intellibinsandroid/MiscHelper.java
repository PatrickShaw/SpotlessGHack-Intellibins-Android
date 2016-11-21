package com.intellibins.intellibinsandroid;

import android.content.Intent;

import com.google.gson.Gson;
import com.intellibins.intellibinsandroid.restdata.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Really shouldn't exist. A helper class for random method calls.
 * TODO: Refactor this one day
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */
public class MiscHelper {
    public static <T> T clone (T object, Class<T> clonedClass){
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(object, clonedClass), clonedClass);
    }
    public static Intent newAttachIntent()
    {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    /**
     * Designed for {@link ArrayList}: uses {@link ArrayList#get(int)} so the insertion is incredibly
     * inefficient for {@link java.util.LinkedList}.
     * @return the index at which the new item was inserted
     */
    public static <T extends Comparable<T>> int insertSort(List<T> collection, T item){
        int oldArrayLength = collection.size();
        collection.add(item);
        int i = oldArrayLength;
        while (i > 0) {
            T comparedItem = collection.get(i);
            if (item.compareTo(comparedItem) < 0) {
                // If inserted item is smaller, swap (move down the list)
                collection.set(i, item);
                collection.set(i + 1, comparedItem);
            }
            else {
                // Otherwise we're done
                break;
            }
            i--;
        }
        return i;
    }

    /**
     * @param roughIndex
     * The rough index of the item
     * @param maxIterations
     * The maximum number items we can search through
     * @return
     * The actual index of the item
     */
    private static <T> int propagationSearch(
            List<T> list,
            GetIdentifiable<T> getIdentifiable,
            Integer id,
            int roughIndex,
            int maxIterations
    ) {
        if(id == null)
            return -1;
        roughIndex = Math.max(Math.min(roughIndex, list.size() - 1), 0);
        int i = roughIndex;
        int j = roughIndex + 1;
        int iterationCount = 0;
        while (i >= 0 && j < list.size() && iterationCount < maxIterations) {
            if (getIdentifiable.getIdentifiable(list.get(i)).getId().equals(id))
                return i;
            if (getIdentifiable.getIdentifiable(list.get(j)).getId().equals(id))
                return j;
            i--;
            j++;
            iterationCount += 2;
        }
        while (i >= 0 && iterationCount < maxIterations) {
            if (getIdentifiable.getIdentifiable(list.get(i)).getId().equals(id))
                return i;
            i--;
            iterationCount++;
        }
        while (j < list.size() && iterationCount < maxIterations) {
            if (getIdentifiable.getIdentifiable(list.get(j)).getId().equals(id))
                return j;
            j++;
            iterationCount++;
        }
        return -1;
    }
    public interface GetIdentifiable<T> {
        Identifiable getIdentifiable(T item);
    }
    public static <T> int propagationSearch(List<T> list, GetIdentifiable<T> getIdentifiable, T item, int roughIndex) {
        return propagationSearch(list, getIdentifiable, getIdentifiable.getIdentifiable(item).getId(), roughIndex);
    }
    public static <T> int propagationSearch(List<T> list, GetIdentifiable<T> getIdentifiable, Integer id, int roughIndex) {
        return propagationSearch(list, getIdentifiable, id, roughIndex, 200);
    }
    public static <T extends Identifiable> int propagationSearch(List<T> list, Integer id, int roughIndex) {
        return propagationSearch(list, i -> i, id, roughIndex, 200);
    }

    public static <T extends Identifiable> int propagationSearch(List<T> list, T item, int roughIndex) {
        return propagationSearch(list, i -> i, item.getId(), roughIndex, 200);
    }

    public static boolean isEmpty(HasData... hasdatas) {
        return !hasData(hasdatas);
    }

    public static boolean hasData(HasData... hasDatas) {
        for(HasData i: hasDatas)
            if (i == null || i.hasData())
                return true;
        return false;
    }

    public static boolean isRefreshing(IsRefreshing... isRefreshing) {
        for(IsRefreshing i : isRefreshing)
            if(i == null || i.isRefreshing())
                return true;
        return false;
    }

}

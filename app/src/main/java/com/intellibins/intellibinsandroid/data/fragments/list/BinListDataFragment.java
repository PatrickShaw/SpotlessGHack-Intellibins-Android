package com.intellibins.intellibinsandroid.data.fragments.list;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.intellibins.intellibinsandroid.IntellbinsUrls;
import com.intellibins.intellibinsandroid.listhandler.SimpleIndexListHandler;
import com.intellibins.intellibinsandroid.restdata.BinInformation;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class BinListDataFragment extends ListDataFragment<SimpleIndexListHandler<BinInformation>> {
    public static final String ARG_GET_BINS_REQUEST_TAG = "get_bins_request_tag";
    public static BinListDataFragment newInstance(String getBinsTag) {
        BinListDataFragment fragment = new BinListDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GET_BINS_REQUEST_TAG, getBinsTag);
        fragment.setArguments(args);
        return fragment;
    }
    private String mGetBinsTag;
    @NonNull
    @Override
    protected SimpleIndexListHandler<BinInformation> createListHandler() {
        Bundle args = new Bundle();
        mGetBinsTag = args.getString(ARG_GET_BINS_REQUEST_TAG);
        return new SimpleIndexListHandler<BinInformation>(BinInformation[].class) {
            @Override
            public String getNextPage(int index, int count) {
                return IntellbinsUrls.getBinsUrl(index, count);
            }

            @Override
            public String getItemName() {
                return "bins";
            }

            @Override
            protected String getLoadMoreItemsTag() {
                return mGetBinsTag;
            }
        };
    }
}

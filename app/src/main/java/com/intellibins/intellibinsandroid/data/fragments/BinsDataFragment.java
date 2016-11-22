package com.intellibins.intellibinsandroid.data.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.intellibins.intellibinsandroid.data.fragments.DataFragment;
import com.intellibins.intellibinsandroid.gui.OnBinsRetrieved;
import com.intellibins.intellibinsandroid.httprequests.GetBinsRequest;
import com.intellibins.intellibinsandroid.restdata.BinInformation;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class BinsDataFragment extends DataFragment<BinInformation[]> {
    private static final String ARG_TAG_REQUEST_GET_BINS = "tag_request_get_bins";
    public static BinsDataFragment newInstance(String tagRequestGetBins) {
        BinsDataFragment fragment = new BinsDataFragment();
        Bundle args=  new Bundle();
        args.putString(ARG_TAG_REQUEST_GET_BINS, tagRequestGetBins);
        fragment.setArguments(args);
        return fragment;
    }
    private String mTagRequestRetrieveBins;
    private OnBinsRetrieved mOnBinsRetrievedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle args = new Bundle();
        mTagRequestRetrieveBins = args.getString(ARG_TAG_REQUEST_GET_BINS);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void notifyDataRetrieved(BinInformation[] data) {
        if(mOnBinsRetrievedListener != null) {
            mOnBinsRetrievedListener.onBinsRetrieved(data);
        }
    }

    @Override
    protected String getRetrieveDataItemName() {
        return "bins";
    }

    @Override
    public String getDataRequestTag() {
        return "AEWRAEWRAEWR";
    }

    @Override
    protected Request<BinInformation[]> getDataRequest(Response.Listener<BinInformation[]> responseRetrievedListener, Response.ErrorListener errorListener) {
        return new GetBinsRequest(
                responseRetrievedListener,
                errorListener,
                0,
                1000001
        );
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnBinsRetrieved) {
            mOnBinsRetrievedListener = (OnBinsRetrieved) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnBinsRetrievedListener = null;
    }
}

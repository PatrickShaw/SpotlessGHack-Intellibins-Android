package com.intellibins.intellibinsandroid.data.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.intellibins.intellibinsandroid.gui.OnClosestBinRetrieved;
import com.intellibins.intellibinsandroid.httprequests.GetClosestBinIdRequest;
import com.intellibins.intellibinsandroid.restdata.BinInformation;
import com.intellibins.intellibinsandroid.restdata.ClosestBinRequestData;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class ClosestBinIdDataFragment extends DataFragment<BinInformation> {
    private static final String ARG_TAG_REQUEST_CLOSEST_BIN = "tag_request_closest_bin";
    public static ClosestBinIdDataFragment newInstance(String closestBinTag) {
        ClosestBinIdDataFragment fragment = new ClosestBinIdDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG_REQUEST_CLOSEST_BIN, closestBinTag);
        fragment.setArguments(args);
        return fragment;
    }
    private String mTagRequestClosestBin;
    private OnClosestBinRetrieved mClosestBinIdRetrieved;
    private LocationManager mLocationManager;
    public void setLocationManager(LocationManager client) {
        mLocationManager = client;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mTagRequestClosestBin = getArguments().getString(ARG_TAG_REQUEST_CLOSEST_BIN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void notifyDataRetrieved(BinInformation data) {
        if(mClosestBinIdRetrieved != null) {
            mClosestBinIdRetrieved.onClosestBinIdRetrieved(data);
        }
    }

    @Override
    protected String getRetrieveDataItemName() {
        return "the closest bin";
    }

    @Override
    public String getDataRequestTag() {
        return mTagRequestClosestBin;
    }

    @Override
    protected Request<BinInformation> getDataRequest(Response.Listener<BinInformation> responseRetrievedListener, Response.ErrorListener errorListener) {
        return new GetClosestBinIdRequest(
                responseRetrievedListener,
                errorListener,
                new ClosestBinRequestData(new double[] {0,0})
        );
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof OnClosestBinRetrieved) {
            mClosestBinIdRetrieved = (OnClosestBinRetrieved) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mClosestBinIdRetrieved = null;
        super.onDetach();
    }
}

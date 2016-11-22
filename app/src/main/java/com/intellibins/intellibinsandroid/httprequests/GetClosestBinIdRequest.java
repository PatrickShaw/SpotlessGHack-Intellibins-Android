package com.intellibins.intellibinsandroid.httprequests;

import com.android.volley.Response;
import com.intellibins.intellibinsandroid.IntellbinsUrls;
import com.intellibins.intellibinsandroid.httprequests.templates.HeterogeneousGsonRequest;
import com.intellibins.intellibinsandroid.restdata.BinInformation;
import com.intellibins.intellibinsandroid.restdata.ClosestBinRequestData;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class GetClosestBinIdRequest extends HeterogeneousGsonRequest<ClosestBinRequestData, BinInformation>{
    public GetClosestBinIdRequest(
            Response.Listener<BinInformation> listener,
            Response.ErrorListener errorListener,
            ClosestBinRequestData requestBody) {
        super(
                Method.POST,
                IntellbinsUrls.getClosestBinIdUrl(),
                listener,
                errorListener,
                ClosestBinRequestData.class,
                BinInformation.class,
                requestBody);
    }
}

package com.intellibins.intellibinsandroid.httprequests;


import com.android.volley.Response;
import com.intellibins.intellibinsandroid.IntellbinsUrls;
import com.intellibins.intellibinsandroid.httprequests.templates.HeterogeneousGsonRequest;
import com.intellibins.intellibinsandroid.restdata.BinInformation;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class GetBinsRequest extends HeterogeneousGsonRequest<Void, BinInformation[]> {
    public GetBinsRequest(
            Response.Listener<BinInformation[]> listener,
            Response.ErrorListener errorListener,
            int index,
            int count) {
        super(
                IntellbinsUrls.getBinsUrl(index, count),
                listener,
                errorListener,
                Void.class,
                BinInformation[].class,
                null
        );
    }
}

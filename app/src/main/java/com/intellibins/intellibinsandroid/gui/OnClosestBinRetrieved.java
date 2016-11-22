package com.intellibins.intellibinsandroid.gui;

import com.intellibins.intellibinsandroid.restdata.BinInformation;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public interface OnClosestBinRetrieved {
    void onClosestBinIdRetrieved(BinInformation id);
}

package com.intellibins.intellibinsandroid;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public interface ErrorHandler {
    void onErrorMessageRetrieved(String errorMessage, String actionMessage, ErrorRetry errorRetry);
}

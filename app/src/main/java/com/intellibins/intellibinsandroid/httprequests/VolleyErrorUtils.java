package com.intellibins.intellibinsandroid.httprequests;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.intellibins.intellibinsandroid.ErrorHandler;
import com.intellibins.intellibinsandroid.ErrorRetry;
import com.intellibins.intellibinsandroid.IntellibinsApplication;

import org.json.JSONObject;

import java.text.MessageFormat;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmaiLog.com)
 * @since {22/11/2016}
 */
public class VolleyErrorUtils {
    public static final String DEFAULT_ERROR_MESSAGE_FORMAT = "Could not retrieve {0}. {1}";
    private static final String ERROR_COULD_NOT_LOGIN = "Could not log in.";
    private static final String DEBUGGER_TAG = "AuthorizedErrorListener";
    private static final int HTTP_BAD_TOKEN = 498;
    private static final String ERROR_NOT_LOGGED_IN = "";
    private static final String ERROR_NO_CONNECTION = "";

    public static void handleVolleyError(
            VolleyError volleyError,
            ErrorRetry retryMethod,
            @Nullable ErrorHandler errorHandler,
            String itemName) {
        handleVolleyError(volleyError, retryMethod, errorHandler, itemName, DEFAULT_ERROR_MESSAGE_FORMAT);
    }

    public static void handleVolleyError(
            VolleyError volleyError,
            ErrorRetry retryMethod,
            @Nullable
                    ErrorHandler errorHandler,
            String itemName,
            String messageFormat) {
        handleNonTokenError(volleyError, retryMethod, errorHandler, itemName, messageFormat);
    }

    public static String getHttpError(int httpCode) {
        return "HTTP Error " + Integer.toString(httpCode);
    }

    public static void handleNonTokenError(
            VolleyError volleyError,
            ErrorRetry retryMethod,
            ErrorHandler errorHandler,
            String itemName) {
        handleNonTokenError(volleyError, retryMethod, errorHandler, itemName, DEFAULT_ERROR_MESSAGE_FORMAT);
    }

    public static void handleNonTokenError(
            VolleyError volleyError,
            ErrorRetry retryMethod,
            ErrorHandler errorHandler,
            String itemName,
            String messageFormat) {

        String errorMessage;
        if (volleyError.networkResponse != null) {
            try {
                JSONObject jsonErrorResponse = new JSONObject(new String(volleyError.networkResponse.data));
                String serverErrorMessage = jsonErrorResponse.getString("message");
                Log.e("SimpleErrorListener", Integer.toString(volleyError.networkResponse.statusCode) + " Server error message: " + serverErrorMessage);
                errorMessage = serverErrorMessage;
            } catch (Exception ex) {
                errorMessage = MessageFormat.format(messageFormat.replace("'", "''"), itemName, "HTTP Error " + Integer.toString(volleyError.networkResponse.statusCode));
            }
        } else {
            if (volleyError.getMessage() != null && !TextUtils.isEmpty(volleyError.getMessage())) {
                //errorMessage = error.getMessage();
                // TODO: Show this error in the Snackbar if Volley stops showing the weird SSL thing
                Log.e("VolleyErrorUtils", "Volley error message: " + volleyError.getMessage());
            }
            errorMessage = MessageFormat.format(messageFormat.replace("'", "''"), itemName, "Can't connect to Reallocate. ");
            try {
                if (!IntellibinsApplication.getInstance().isConnected()) {
                    errorMessage += "Are you connected the internet? ";
                }
            } catch (Exception ex) {
                Log.e("SimpleErrorListener", errorMessage, ex);
            }
        }
        try {
            errorHandler.onErrorMessageRetrieved(
                    errorMessage,
                    "Retry",
                    retryMethod
            );
            Log.e("SimpleErrorListener", "The error was: " + errorMessage);
        } catch (Exception ex) {
            Log.e("SimpleErrorListener", "Could not handle error.", ex);
            Log.e("SimpleErrorListener", "The error was: " + errorMessage);
        }
    }
}
package com.intellibins.intellibinsandroid.status;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public class Status {
    public interface OnStatusChangeListener {
        void onStatusChangeListener(boolean status);
    }
    private OnStatusChangeListener mStatusListener;
    private boolean mStatus;
    public static Status getInstance(boolean initialStatus) {
        return new Status(initialStatus);
    }
    public static Status getInstance() {
        return new Status(false);
    }
    protected Status(boolean initialStatus) {
        mStatus = initialStatus;
    }

    public void setStatusListener(OnStatusChangeListener onStatusChangeListener) {
        mStatusListener = onStatusChangeListener;
    }

    public boolean getStatus() {
        return mStatus;
    }

    public void setStatus(boolean status, boolean forceUpdate) {
        if(mStatus != status || forceUpdate) {
            mStatus = status;
            if(mStatusListener != null)
                mStatusListener.onStatusChangeListener(status);
        }
    }

    public void onDetach() {
        mStatusListener = null;
    }
}

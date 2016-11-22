package com.intellibins.intellibinsandroid.gui.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.intellibins.intellibinsandroid.R;


/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since {22/11/2016}
 */

public class IntellibinsSwipeRefreshLayout extends SwipeRefreshLayout {
    public IntellibinsSwipeRefreshLayout(Context context) {
        super(context);
        setColorSchemeColors(getDefaultColorSchemeColors(context));
    }

    public IntellibinsSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeColors(getDefaultColorSchemeColors(context));
    }
    public int[] getDefaultColorSchemeColors(Context context) {
        return new int[] {
                ContextCompat.getColor(context, R.color.swipe_refresh_progress_color_1),
                ContextCompat.getColor(context, R.color.swipe_refresh_progress_color_2)
        };
    }
}

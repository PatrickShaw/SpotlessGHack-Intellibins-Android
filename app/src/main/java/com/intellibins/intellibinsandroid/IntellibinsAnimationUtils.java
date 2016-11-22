package com.intellibins.intellibinsandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * @author Patrick Shaw (Patrick.Leong.Shaw@gmail.com)
 * @since 22/11/2016
 */

public class IntellibinsAnimationUtils {
    public static ViewPropertyAnimatorCompat getToggleVisibilityAnimator(View view, boolean showView, int animationTime) {
        return ViewCompat.animate(view)
                .setDuration(animationTime)
                .alpha(showView ? 1 : 0);
    }

    public static void toggleVisibility(Boolean showView, View view, boolean isInstant) {
        int shortAnimTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        toggleVisibility(showView, view, shortAnimTime, isInstant);
    }

    public static void toggleVisibility(boolean showView, View view, int animationTime, boolean isInstant) {
        if(isInstant) {
            view.setVisibility(showView ? View.VISIBLE : View.GONE);
        } else {
            toggleVisibility(showView, view, animationTime);
        }
    }

    public static void toggleVisibility(final boolean showView, View view, int animationTime) {
        Log.d("IntellibinsAnimtil", view.getClass().getSimpleName() + " is showing: " + Boolean.toString(showView));
        view.clearAnimation();
        // If it's already hidden then don't bother
        if(!showView && view.getVisibility() == View.GONE) {
            return;
        }
        getToggleVisibilityAnimator(view, showView, animationTime)
                .setListener(
                        new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(View view) {
                                super.onAnimationStart(view);
                                view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                super.onAnimationEnd(view);
                                view.setVisibility(showView ? View.VISIBLE : View.GONE);
                            }
                        }
                );
    }

    public static void toggleVisibility(boolean showView, View view) {
        int shortAnimTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        toggleVisibility(showView, view, shortAnimTime);
    }

    public static void toggleCrossFade(boolean showSecondView, View view1, View view2) {
        toggleCrossFade(showSecondView, view1, view2, false);
    }

    public static void toggleCrossFadeFragment(boolean showProgress, @IdRes int fragmentId, View view2, boolean isImmediate, FragmentManager fragmentManager) {
        if (isImmediate) {
            view2.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        } else {
            toggleVisibility(showProgress, view2);
        }
        if (showProgress) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentById(fragmentId)).commit();
        } else {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(fragmentId)).commit();
        }
    }

    public static void toggleCrossFade(boolean showSecondView, View view1, View view2, boolean isImmediate) {
        if (isImmediate) {
            view1.setVisibility(showSecondView ? View.GONE : View.VISIBLE);
            view2.setVisibility(showSecondView ? View.VISIBLE : View.GONE);
        } else {
            int shortAnimTime = view1.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
            toggleVisibility(!showSecondView, view1, shortAnimTime);
            toggleVisibility(showSecondView, view2, shortAnimTime);
        }
    }

    public void compatCrossFadeCircleReveal(
            View view1,
            View view2,
            boolean showSecondView,
            int centerX,
            int centerY,
            float startRadius,
            float endRadius
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            crossFadeCircleReveal(view1, view2, showSecondView, centerX, centerY, startRadius, endRadius);
        } else {
            toggleCrossFade(showSecondView, view1, view2);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator crossFadeCircleReveal(
            View view1,
            View view2,
            boolean showSecondView,
            int centerX,
            int centerY,
            float startRadius,
            float endRadius
    ) {
        final View revealingView = showSecondView ? view2 : view1;
        final View hidingView = showSecondView ? view1 : view2;
        Animator animator = ViewAnimationUtils.createCircularReveal(
                revealingView,
                centerX,
                centerY,
                startRadius,
                endRadius
        );
        animator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        // Make sure both are visible
                        revealingView.setVisibility(View.VISIBLE);
                        hidingView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // Okay we'd be overdrawing the hiding view so get rid of it
                        revealingView.setVisibility(View.VISIBLE);
                        hidingView.setVisibility(View.GONE);
                    }
                }
        );
        return animator;
    }

    public static void compatCirclularReveal(
            final View view,
            int centerX,
            int centerY,
            float startRadius,
            float endRadius
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    centerX,
                    centerY,
                    startRadius,
                    endRadius
            );
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
            );
        } else {
            toggleVisibility(true, view);
        }
    }
}
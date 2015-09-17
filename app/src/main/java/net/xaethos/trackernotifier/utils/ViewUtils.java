package net.xaethos.trackernotifier.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewUtils {

    private ViewUtils() {
    }

    /**
     * Fades-in a hidden view while fading out some others
     *
     * @param showView  the view to show
     * @param hideViews the views to hide
     */
    public static void switchVisible(final View showView, View... hideViews) {
        int animTime = showView.getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (showView.getVisibility() != View.VISIBLE) {
            showView.setVisibility(View.VISIBLE);
            showView.animate()
                    .setDuration(animTime)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showView.setVisibility(View.VISIBLE);
                        }
                    });
        }

        for (final View hideView : hideViews) {
            if (hideView.getVisibility() == View.GONE) continue;

            hideView.setVisibility(View.GONE);
            hideView.animate()
                    .setDuration(animTime)
                    .alpha(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            hideView.setVisibility(View.GONE);
                        }
                    });
        }
    }

}

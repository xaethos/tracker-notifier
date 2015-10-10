package net.xaethos.trackernotifier.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
        int animTime = getShortAnimTime(showView.getResources());

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

    /**
     * Fades a view in or out
     *
     * @param showView the view to affect
     * @param visible  whether to show or hide the view
     */
    public static void animateVisible(final View showView, boolean visible) {
        final int visibility = visible ? View.VISIBLE : View.GONE;
        if (showView.getVisibility() == visibility) return;
        showView.setVisibility(visibility);
        showView.animate()
                .setDuration(getShortAnimTime(showView.getResources()))
                .alpha(visible ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showView.setVisibility(visibility);
                    }
                });
    }

    /**
     * Set the text on the given view, and make it visible. If the text is empty or null, the
     * view will be hidden.
     * <p>
     * If the view is not a {@link TextView}, nothing will be done.
     *
     * @param textView the TextView on which to set the text
     * @param text
     */
    public static void setTextOrHide(@Nullable View textView, @Nullable CharSequence text) {
        if (textView instanceof TextView) {
            if (TextUtils.isEmpty(text)) {
                textView.setVisibility(View.GONE);
            } else {
                ((TextView) textView).setText(text);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    private static int getShortAnimTime(Resources resources) {
        return resources.getInteger(android.R.integer.config_shortAnimTime);
    }

}

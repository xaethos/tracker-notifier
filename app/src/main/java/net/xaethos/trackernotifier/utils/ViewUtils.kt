package net.xaethos.trackernotifier.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.view.View
import android.widget.TextView

/**
 * Fades-in a hidden view while fading out some others

 * @param showView  the view to show
 * *
 * @param hideViews the views to hide
 */
fun switchVisible(showView: View, vararg hideViews: View) {
    val animTime = getShortAnimTime(showView.resources)

    if (showView.visibility != View.VISIBLE) {
        showView.visibility = View.VISIBLE
        showView.animate()
                .setDuration(animTime)
                .alpha(1f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        showView.visibility = View.VISIBLE
                    }
                })
        showView.animate().setDuration(animTime).alpha(1f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                showView.visibility = View.VISIBLE
            }
        })
    }

    for (hideView in hideViews) {
        if (hideView.visibility == View.GONE) continue

        hideView.visibility = View.GONE
        hideView.animate()
                .setDuration(animTime)
                .alpha(0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        hideView.visibility = View.GONE
                    }
                })
    }
}

/**
 * Fades a view in or out

 * @param showView the view to affect
 * *
 * @param visible  whether to show or hide the view
 */
fun View?.animateVisible(visible: Boolean) {
    if (this == null) return;
    val visibility = if (visible) View.VISIBLE else View.GONE
    if (this.visibility == visibility) return
    this.visibility = visibility
    this.animate()
            .setDuration(getShortAnimTime(this.resources))
            .alpha((if (visible) 1f else 0f))
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@animateVisible.visibility = visibility
                }
            })
}

/**
 * Set the text on the given view, and make it visible. If the text is empty or null, the
 * view will be hidden.
 *
 *
 * If the view is not a [TextView], nothing will be done.

 * @param textView the TextView on which to set the text
 * *
 * @param text
 */
fun setTextOrHide(textView: View?, text: CharSequence?) {
    if (textView is TextView) textView.setTextOrHide(text)
}

fun TextView.setTextOrHide(textRes: Int) {
    this.setTextOrHide(if (textRes == 0) null else context.getText(textRes))
}

fun TextView.setTextOrHide(text: CharSequence?) {
    if (text.empty) {
        this.text = null
        this.visibility = View.GONE
    } else {
        this.text = text
        this.visibility = View.VISIBLE
    }
}

private fun getShortAnimTime(resources: Resources): Long {
    return resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
}

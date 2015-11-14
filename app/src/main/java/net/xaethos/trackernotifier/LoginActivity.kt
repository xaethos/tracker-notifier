package net.xaethos.trackernotifier

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import net.xaethos.quicker.cloud.Authenticator
import net.xaethos.quicker.cloud.MeApi
import net.xaethos.quicker.cloud.login
import net.xaethos.trackernotifier.di.AppComponent
import net.xaethos.trackernotifier.subscribers.toastError
import net.xaethos.trackernotifier.utils.PreferencesManager
import net.xaethos.trackernotifier.utils.switchVisible
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : Activity() {

    @Inject lateinit var authenticator: Authenticator
    @Inject lateinit var meApi: MeApi

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var loginSubscription: Subscription? = null

    // UI references
    private lateinit var accountNameView: EditText
    private lateinit var passwordView: EditText
    private lateinit var progressView: View
    private lateinit var loginFormView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        AppComponent.instance.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the login form.
        accountNameView = findViewById(R.id.input_account) as EditText
        passwordView = findViewById(R.id.input_password) as EditText
        passwordView.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                true
            } else {
                false
            }
        }

        findViewById(R.id.btn_sign_in).setOnClickListener { view -> attemptLogin() }

        loginFormView = findViewById(R.id.login_form)
        progressView = findViewById(R.id.progress)
    }

    override fun onDestroy() {
        loginSubscription?.unsubscribe()
        super.onDestroy()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (!(loginSubscription?.isUnsubscribed ?: true)) return

        // Reset errors.
        accountNameView.error = null
        passwordView.error = null

        // Store values at the time of the login attempt.
        val accountName = accountNameView.text.toString()
        val password = passwordView.text.toString()

        var errorField: View? = null

        if (TextUtils.isEmpty(password)) {
            passwordView.error = getString(R.string.error_field_required)
            errorField = passwordView
        }

        if (TextUtils.isEmpty(accountName)) {
            accountNameView.error = getString(R.string.error_field_required)
            errorField = accountNameView
        }

        if (errorField != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            errorField.requestFocus()
            return
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true)

        val prefs = PreferencesManager.getInstance(this)

        loginSubscription = meApi.login(accountName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    Toast.makeText(this@LoginActivity,
                            "Welcome back " + user.name, Toast.LENGTH_LONG).show()

                    authenticator.trackerToken = user.api_token
                    prefs.trackerToken = user.api_token
                    setResult(Activity.RESULT_OK)
                    finish()
                }, { error ->
                    showProgress(false)
                    this@LoginActivity.toastError(error)
                })
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    fun showProgress(show: Boolean) {
        if (show) {
            switchVisible(progressView, loginFormView)
        } else {
            switchVisible(loginFormView, progressView)
        }
    }

}

package net.xaethos.trackernotifier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.xaethos.trackernotifier.api.UserApi;
import net.xaethos.trackernotifier.models.Person;

import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Subscription mLoginSubscription;

    // UI references.
    private EditText mAccountNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mAccountNameView = (EditText) findViewById(R.id.input_account);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn_sign_in).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mLoginSubscription != null && !mLoginSubscription.isUnsubscribed()) {
            return;
        }

        // Reset errors.
        mAccountNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String accountName = mAccountNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        View errorField = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            errorField = mPasswordView;
        }

        if (TextUtils.isEmpty(accountName)) {
            mAccountNameView.setError(getString(R.string.error_field_required));
            errorField = mAccountNameView;
        }

        if (errorField != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            errorField.requestFocus();
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl("https://www.pivotaltracker.com/services/v5/")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build();
        UserApi service = retrofit.create(UserApi.class);

        String credentials = accountName + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        mLoginSubscription = service.me(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person user) {
                        Toast.makeText(LoginActivity.this,
                                "Welcome back " + user.name,
                                Toast.LENGTH_LONG).show();
                        showProgress(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        showProgress(false);
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // Fade-in the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

}

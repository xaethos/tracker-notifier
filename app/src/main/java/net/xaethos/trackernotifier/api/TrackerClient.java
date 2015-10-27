package net.xaethos.trackernotifier.api;

import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.xaethos.trackernotifier.BuildConfig;
import net.xaethos.trackernotifier.models.Me;

import java.io.IOException;

import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

public class TrackerClient {

    private static TrackerClient sInstance;

    private final AuthInterceptor mAuthInterceptor = new AuthInterceptor();

    public final MeApi me;
    public final NotificationsApi notifications;
    public final StoriesApi stories;
    public final CommentsApi comments;

    public static TrackerClient getInstance() {
        if (sInstance == null) {
            Retrofit retrofit =
                    new Retrofit.Builder().baseUrl("https://www.pivotaltracker.com/services/v5/")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .build();
            if (BuildConfig.DEBUG) retrofit.client().interceptors().add(new LoggingInterceptor());
            sInstance = new TrackerClient(retrofit);
        }
        return sInstance;
    }

    TrackerClient(Retrofit retrofit) {
        retrofit.client().interceptors().add(0, mAuthInterceptor);

        me = retrofit.create(MeApi.class);
        notifications = retrofit.create(NotificationsApi.class);
        stories = retrofit.create(StoriesApi.class);
        comments = retrofit.create(CommentsApi.class);
    }

    public boolean hasToken() {
        return mAuthInterceptor.hasAuthToken();
    }

    public void setToken(String trackerToken) {
        mAuthInterceptor.setToken(trackerToken);
    }

    public Observable<Me> login(String username, String password) {
        String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return me.login(auth);
    }

    private static class AuthInterceptor implements Interceptor {
        private String mTrackerToken;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (mTrackerToken == null) return chain.proceed(request);

            Request authorizedRequest =
                    request.newBuilder().header("X-TrackerToken", mTrackerToken).build();
            return chain.proceed(authorizedRequest);
        }

        public boolean hasAuthToken() {
            return mTrackerToken != null;
        }

        public void setToken(String trackerToken) {
            mTrackerToken = trackerToken;
        }
    }

    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.v("TrackerClient",
                    String.format("Sending request %s on %s%n%s",
                            request.url(),
                            chain.connection(),
                            request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.v("TrackerClient",
                    String.format("Received response for %s in %.1fms%n%s",
                            response.request().url(),
                            (t2 - t1) / 1e6d,
                            response.headers()));

            return response;
        }
    }
}

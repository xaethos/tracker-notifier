package net.xaethos.trackernotifier.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import net.xaethos.trackernotifier.models.Me;
import net.xaethos.trackernotifier.models.Notification;
import net.xaethos.trackernotifier.utils.PrefUtils;

import java.util.List;

import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public class TrackerClient {

    private static TrackerClient sInstance;

    private final SharedPreferences mPrefs;
    private final MeApi mMeApi;
    private final NotificationsApi mNotificationsApi;

    public static TrackerClient getInstance(Context context) {
        if (sInstance == null) {
            Retrofit retrofit =
                    new Retrofit.Builder().baseUrl("https://www.pivotaltracker.com/services/v5/")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .build();
            sInstance = new TrackerClient(retrofit, PrefUtils.getPrefs(context.getApplicationContext()));
        }
        return sInstance;
    }

    TrackerClient(Retrofit retrofit, SharedPreferences prefs) {
        mPrefs = prefs;
        mMeApi = new MeApiImpl(this, retrofit);
        mNotificationsApi = new NotificationsApiImpl(this, retrofit);
    }

    String getToken() {
        return mPrefs.getString(PrefUtils.PREF_TOKEN, null);
    }

    public boolean hasToken() {
        return mPrefs.contains(PrefUtils.PREF_TOKEN);
    }

    public MeApi user() {
        return mMeApi;
    }

    public NotificationsApi notifications() {
        return mNotificationsApi;
    }

    private static class MeApiImpl implements MeApi {

        public interface Api {
            @GET("me")
            Observable<Me> login(@Header("Authorization") String authorization);

            @GET("me")
            Observable<Me> get(@Header("X-TrackerToken") String token);
        }

        private final TrackerClient mClient;
        private final Api mApi;

        public MeApiImpl(TrackerClient client, Retrofit retrofit) {
            mClient = client;
            mApi = retrofit.create(Api.class);
        }

        @Override
        public Observable<Me> login(String username, String password) {
            String credentials = username + ":" + password;
            String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            return mApi.login(auth);
        }

        @Override
        public Observable<Me> get() {
            return mApi.get(mClient.getToken());
        }
    }

    private static class NotificationsApiImpl implements NotificationsApi {

        private interface Api {
            @GET("my/notifications?fields=:default,story(story_type,:default)")
            Observable<List<Notification>> get(@Header("X-TrackerToken") String token);

            @PUT("my/notifications/{id}")
            Observable<Notification> markRead(
                    @Header("X-TrackerToken") String token,
                    @Path("id") long notificationId,
                    @Body Notification notification);
        }

        private final TrackerClient mClient;
        private final Api mApi;

        public NotificationsApiImpl(TrackerClient client, Retrofit retrofit) {
            mClient = client;
            mApi = retrofit.create(Api.class);
        }

        @Override
        public Observable<List<Notification>> get() {
            return mApi.get(mClient.getToken());
        }

        @Override
        public Observable<Notification> markRead(long notificationId) {
            Notification readNotification = new Notification();
            readNotification.read_at = System.currentTimeMillis();
            return mApi.markRead(mClient.getToken(), notificationId, readNotification);
        }
    }
}

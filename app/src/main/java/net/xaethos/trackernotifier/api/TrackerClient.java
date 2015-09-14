package net.xaethos.trackernotifier.api;

import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class TrackerClient {

    private final UserApi mUserApi;

    public TrackerClient() {
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl("https://www.pivotaltracker.com/services/v5/")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build();

        mUserApi = retrofit.create(UserApi.class);
    }

    public UserApi getUserApi() {
        return mUserApi;
    }
}

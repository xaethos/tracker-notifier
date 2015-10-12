package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Me;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

public interface MeApi {

    @GET("me")
    Observable<Me> get();

    @GET("me")
    Observable<Me> login(@Header("Authorization") String authorization);

}

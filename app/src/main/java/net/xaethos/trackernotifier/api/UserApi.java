package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Me;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

public interface UserApi {
    @GET("me")
    Observable<Me> me(@Header("Authorization") String authorization);
}

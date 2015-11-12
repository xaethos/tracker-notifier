package net.xaethos.quicker.cloud;

import net.xaethos.quicker.entities.MeData;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

public interface MeApi {

    @GET("me")
    Observable<MeData> get();

    @GET("me")
    Observable<MeData> login(@Header("Authorization") String authorization);

}

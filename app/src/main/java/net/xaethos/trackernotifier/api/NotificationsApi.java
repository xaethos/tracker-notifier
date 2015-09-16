package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Notification;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

public interface NotificationsApi {

    @GET("my/notifications")
    Observable<List<Notification>> notifications(@Header("X-TrackerToken") String token);
}

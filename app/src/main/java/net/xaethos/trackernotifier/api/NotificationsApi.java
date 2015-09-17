package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Notification;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface NotificationsApi {

    @GET("my/notifications")
    Observable<List<Notification>> get(@Header("X-TrackerToken") String token);

    @PUT("my/notifications/{id}")
    Observable<Notification> markRead(
            @Header("X-TrackerToken") String token,
            @Path("id") long notificationId,
            @Body Notification notification);
}

package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Notification;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface NotificationsApi {

    @GET("my/notifications?fields=:default,story(story_type,:default)")
    Observable<List<Notification>> get();

    @GET("my/notifications")
    Observable<List<Notification>> get(@Query("fields") String fields);

    @PUT("my/notifications/{id}")
    Observable<Notification> put(
            @Path("id") long notificationId, @Body Notification notification);

}

package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Notification;

import java.util.List;

import rx.Observable;

public interface NotificationsApi {

    Observable<List<Notification>> get();

    Observable<Notification> markRead(long notificationId);

}

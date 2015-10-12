package net.xaethos.trackernotifier.utils;

import net.xaethos.trackernotifier.api.TrackerClient;
import net.xaethos.trackernotifier.models.Notification;

import rx.Observable;

public class Notifications {
    private Notifications() {
        throw new AssertionError("No instances");
    }

    public static Observable<Notification> markRead(TrackerClient client, long notificationId) {
        Notification readNotification = new Notification();
        readNotification.read_at = System.currentTimeMillis();
        return client.notifications.put(notificationId, readNotification);
    }
}

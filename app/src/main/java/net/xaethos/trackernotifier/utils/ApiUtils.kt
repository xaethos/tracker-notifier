package net.xaethos.trackernotifier.utils

import net.xaethos.trackernotifier.api.NotificationsApi
import net.xaethos.trackernotifier.models.Notification
import rx.Observable

fun NotificationsApi.markRead(notificationId: Long): Observable<Notification> {
    val readNotification = Notification()
    readNotification.read_at = System.currentTimeMillis()
    return put(notificationId, readNotification)
}

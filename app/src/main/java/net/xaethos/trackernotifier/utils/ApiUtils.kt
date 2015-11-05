package net.xaethos.trackernotifier.utils

import android.util.Base64
import net.xaethos.trackernotifier.api.MeApi
import net.xaethos.trackernotifier.api.NotificationsApi
import net.xaethos.trackernotifier.models.Me
import net.xaethos.trackernotifier.models.Notification
import rx.Observable

fun MeApi.login(username: String, password: String): Observable<Me> {
    val credentials = username + ":" + password
    val auth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    return login(auth)
}

fun NotificationsApi.markRead(notificationId: Long): Observable<Notification> {
    val readNotification = Notification()
    readNotification.read_at = System.currentTimeMillis()
    return put(notificationId, readNotification)
}

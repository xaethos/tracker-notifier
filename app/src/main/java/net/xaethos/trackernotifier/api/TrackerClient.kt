package net.xaethos.trackernotifier.api

import net.xaethos.quicker.cloud.Authenticator
import net.xaethos.trackernotifier.di.AppComponent
import net.xaethos.trackernotifier.utils.empty
import retrofit.Retrofit
import javax.inject.Inject

class TrackerClient @Inject constructor(
        retrofit: Retrofit,
        private val authenticator: Authenticator) {

    val me = retrofit.create(MeApi::class.java)
    val notifications = retrofit.create(NotificationsApi::class.java)
    val stories = retrofit.create(StoriesApi::class.java)
    val comments = retrofit.create(CommentsApi::class.java)

    fun hasToken() = !authenticator.trackerToken.empty

    fun setToken(trackerToken: String?) {
        authenticator.trackerToken = trackerToken
    }

    companion object {
        val instance = AppComponent.instance.trackerClient
    }
}

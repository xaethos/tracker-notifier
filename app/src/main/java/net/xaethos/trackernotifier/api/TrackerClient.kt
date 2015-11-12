package net.xaethos.trackernotifier.api

import net.xaethos.quicker.cloud.interceptors.AuthInterceptor
import net.xaethos.quicker.cloud.interceptors.LoggingInterceptor
import net.xaethos.trackernotifier.BuildConfig
import net.xaethos.trackernotifier.utils.empty
import retrofit.MoshiConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import javax.inject.Inject

class TrackerClient internal constructor(retrofit: Retrofit) {

    private val authInterceptor = AuthInterceptor()

    val me = retrofit.create(MeApi::class.java)
    val notifications = retrofit.create(NotificationsApi::class.java)
    val stories = retrofit.create(StoriesApi::class.java)
    val comments = retrofit.create(CommentsApi::class.java)

    constructor() : this(Retrofit.Builder()
            .baseUrl("https://www.pivotaltracker.com/services/v5/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build())

    init {
        if (BuildConfig.DEBUG) retrofit.client().interceptors().add(LoggingInterceptor())
        retrofit.client().interceptors().add(0, authInterceptor)
    }

    fun hasToken() = !authInterceptor.trackerToken.empty

    fun setToken(trackerToken: String?) {
        authInterceptor.trackerToken = trackerToken
    }

    companion object {
        val instance = TrackerClient()
    }
}

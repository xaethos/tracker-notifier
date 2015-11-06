package net.xaethos.trackernotifier.api

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import net.xaethos.trackernotifier.BuildConfig
import net.xaethos.trackernotifier.utils.Log
import net.xaethos.trackernotifier.utils.empty
import retrofit.MoshiConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import java.util.concurrent.TimeUnit

class TrackerClient internal constructor(retrofit: Retrofit) {

    private val authInterceptor = AuthInterceptor()

    val me = retrofit.create(MeApi::class.java)
    val notifications = retrofit.create(NotificationsApi::class.java)
    val stories = retrofit.create(StoriesApi::class.java)
    val comments = retrofit.create(CommentsApi::class.java)

    init {
        retrofit.client().interceptors().add(0, authInterceptor)
    }

    fun hasToken() = !authInterceptor.trackerToken.empty

    fun setToken(trackerToken: String?) {
        authInterceptor.trackerToken = trackerToken
    }

    private class AuthInterceptor : Interceptor {
        var trackerToken: String? = null

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val token = trackerToken
            if (token.empty) return chain.proceed(request)

            val authorizedRequest = request.newBuilder().header("X-TrackerToken", token).build()
            return chain.proceed(authorizedRequest)
        }
    }

    private class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val startTime = System.nanoTime()
            Log.v { "Sending request ${request.method()} ${request.url()}\n${request.headers()}" }

            val response = chain.proceed(request)
            val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            Log.v { "Received response for ${request.url()} in ${elapsed}ms\n${response.headers()}" }

            return response
        }
    }

    companion object {
        val instance: TrackerClient by lazy {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.pivotaltracker.com/services/v5/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            if (BuildConfig.DEBUG) retrofit.client().interceptors().add(LoggingInterceptor())
            TrackerClient(retrofit)
        }
    }
}

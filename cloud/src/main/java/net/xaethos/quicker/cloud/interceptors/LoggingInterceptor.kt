package net.xaethos.quicker.cloud.interceptors

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import net.xaethos.quicker.common.Log
import java.util.concurrent.TimeUnit

class LoggingInterceptor : Interceptor {
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

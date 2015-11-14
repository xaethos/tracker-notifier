package net.xaethos.quicker.cloud.interceptors

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import net.xaethos.quicker.cloud.Authenticator
import javax.inject.Inject

class TransformInterceptor @Inject constructor(
        private val authenticator: Authenticator) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.httpUrl().newBuilder().addQueryParameter("date_format", "millis").build()
        val requestBuilder = request.newBuilder().url(url)

        val token = authenticator.trackerToken
        if (token != null) requestBuilder.header("X-TrackerToken", token)

        return chain.proceed(requestBuilder.build())
    }
}

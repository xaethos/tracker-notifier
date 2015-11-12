package net.xaethos.quicker.cloud.interceptors

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import net.xaethos.quicker.cloud.Authenticator

class AuthInterceptor : Interceptor, Authenticator {
    override var trackerToken: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = trackerToken ?: return chain.proceed(request)

        val authorizedRequest = request.newBuilder().header("X-TrackerToken", token).build()
        return chain.proceed(authorizedRequest)
    }
}

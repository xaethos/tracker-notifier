package net.xaethos.quicker.cloud

import android.util.Base64
import net.xaethos.quicker.entities.MeData

import retrofit.http.GET
import retrofit.http.Header
import rx.Observable

interface MeApi {

    @GET("me")
    fun get(): Observable<MeData>

    @GET("me")
    fun login(@Header("Authorization") authorization: String): Observable<MeData>

}

fun MeApi.login(username: String, password: String): Observable<MeData> {
    val credentials = username + ":" + password
    val auth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    return login(auth)
}

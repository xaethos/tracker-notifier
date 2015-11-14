package net.xaethos.quicker.cloud.di

import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import net.xaethos.quicker.cloud.Authenticator
import net.xaethos.quicker.cloud.MeApi
import net.xaethos.quicker.cloud.interceptors.TransformInterceptor
import net.xaethos.quicker.cloud.interceptors.LoggingInterceptor
import net.xaethos.quicker.common.Config
import retrofit.MoshiConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import javax.inject.Singleton

@Module class CloudModule {

    @Provides @Singleton fun provideOkHttpClient(
            config: Config,
            authenticator: Authenticator): OkHttpClient {
        val client = OkHttpClient()
        if (config.isDebug) client.interceptors().add(LoggingInterceptor())
        client.interceptors().add(0, TransformInterceptor(authenticator))
        return client
    }

    @Provides @Singleton fun provideRetrofit(
            config: Config,
            httpClient: OkHttpClient) =
            Retrofit.Builder()
                    .baseUrl(config.baseUrl)
                    .client(httpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

    @Provides @Singleton fun provideAuthenticator() = Authenticator()

    @Provides @Singleton fun provideMeApi(retrofit: Retrofit) = retrofit.create(MeApi::class.java)

}

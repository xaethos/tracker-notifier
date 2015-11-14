package net.xaethos.trackernotifier.di

import dagger.Module
import dagger.Provides
import net.xaethos.quicker.cloud.Authenticator
import net.xaethos.trackernotifier.api.TrackerClient
import retrofit.Retrofit
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun provideTrackerClient(retrofit: Retrofit, authenticator: Authenticator) =
            TrackerClient(retrofit, authenticator)

}

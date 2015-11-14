package net.xaethos.trackernotifier.di

import dagger.Component
import net.xaethos.quicker.cloud.di.CloudModule
import net.xaethos.quicker.common.di.ConfigurationModule
import net.xaethos.trackernotifier.BuildConfig
import net.xaethos.trackernotifier.LoginActivity
import net.xaethos.trackernotifier.api.TrackerClient
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(CloudModule::class, ConfigurationModule::class, AppModule::class))
interface AppComponent {

    fun inject(loginActivity: LoginActivity)

    val trackerClient: TrackerClient

    companion object {
        val instance: AppComponent by lazy {
            val configurationModule = ConfigurationModule(
                    "https://www.pivotaltracker.com/services/v5/",
                    BuildConfig.DEBUG
            )
            DaggerAppComponent.builder().configurationModule(configurationModule).build()
        }
    }

}

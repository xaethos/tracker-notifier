package net.xaethos.quicker.common.di

import dagger.Module
import dagger.Provides
import net.xaethos.quicker.common.Config
import javax.inject.Singleton

@Module
@Singleton
class ConfigurationModule(val baseUrl: String, val isDebug: Boolean) {
    @Provides @Singleton fun provideConfig() = Config(baseUrl, isDebug)
}

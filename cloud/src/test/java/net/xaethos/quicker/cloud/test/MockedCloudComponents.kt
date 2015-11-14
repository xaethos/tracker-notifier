package net.xaethos.quicker.cloud.test

import com.squareup.okhttp.mockwebserver.MockWebServer
import net.xaethos.quicker.cloud.di.CloudModule
import net.xaethos.quicker.common.Config

class MockedCloudComponents private constructor(val mockWebServer: MockWebServer) {
    private val module = CloudModule()

    val config by lazy { Config(baseUrl.toString(), false) }
    val baseUrl by lazy { mockWebServer.url("api") }

    val authenticator by lazy { module.provideAuthenticator() }
    val httpClient by lazy { module.provideOkHttpClient(config, authenticator) }
    val retrofit by lazy { module.provideRetrofit(config, httpClient) }
    val meApi by lazy { module.provideMeApi(retrofit) }

    fun withRunningServer(run: MockedCloudComponents.() -> Unit) {
        try {
            mockWebServer.start()
            run()
        } finally {
            mockWebServer.shutdown()
        }
    }

    companion object {
        fun build(init: MockWebServer.() -> Unit): MockedCloudComponents {
            val server = MockWebServer()
            server.init()
            return MockedCloudComponents(server)
        }
    }
}
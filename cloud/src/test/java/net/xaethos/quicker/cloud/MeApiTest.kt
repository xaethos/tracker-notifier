package net.xaethos.quicker.cloud

import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import net.xaethos.quicker.cloud.di.CloudModule
import net.xaethos.quicker.cloud.test.RobolectricTest
import net.xaethos.quicker.common.Config
import net.xaethos.quicker.entities.MeData
import org.junit.Test
import rx.observers.TestSubscriber
import kotlin.test.assertTrue

class MeApiTest : RobolectricTest() {

    @Test fun getLoginOkay() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(200).setBody(okay))
        server.start()

        val config = Config(server.url("api").toString(), true);

        val module = CloudModule()
        val authInterceptor = module.provideAuthInterceptor();
        val httpClient = module.provideOkHttpClient(config, authInterceptor)
        val retrofit = module.provideRetrofit(config, httpClient)
        val meApi = module.provideMeApi(retrofit)

        val subscriber = TestSubscriber<MeData>()
        meApi.login("joe@doe.com", "p-word").subscribe(subscriber)

        with(server.takeRequest()) {
            assertTrue { path == "/me" }
            assertTrue { getHeader("Authorization") == "Basic am9lQGRvZS5jb206cC13b3Jk" }
        }

        with (subscriber) {
            awaitTerminalEvent()
            assertCompleted()
            assertTrue { onNextEvents.size == 1 }

            val me = onNextEvents[0]
            assertTrue { me.name.equals("Xae Robit") }
        }

        server.shutdown()
    }
}

const val okay = """{
  "kind": "me",
  "id": 1820362,
  "name": "Xae Robit",
  "initials": "XR",
  "username": "xaebot",
  "time_zone": {
    "kind": "time_zone",
    "olson_name": "America/Los_Angeles",
    "offset": "-08:00"
  },
  "api_token": "fake_api_token",
  "has_google_identity": false,
  "projects": [
    {
      "kind": "membership_summary",
      "id": 6024802,
      "project_id": 1437338,
      "project_name": "tracker-notifier",
      "project_color": "cccccc",
      "role": "member",
      "last_viewed_at": "2015-10-12T22:20:28Z"
    }
  ],
  "email": "xaebot@gmail.com",
  "receives_in_app_notifications": true,
  "created_at": "2015-10-10T03:14:20Z",
  "updated_at": "2015-11-12T05:22:10Z"
}"""

const val forbidden = """{
    "code": "invalid_authentication",
    "kind": "error",
    "error": "Invalid authentication credentials were presented.",
    "possible_fix": "Recheck your name (email address or Tracker username) and password."
}"""
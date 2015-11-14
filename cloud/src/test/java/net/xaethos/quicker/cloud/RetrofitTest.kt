package net.xaethos.quicker.cloud

import com.squareup.okhttp.ResponseBody
import com.squareup.okhttp.mockwebserver.MockResponse
import net.xaethos.quicker.cloud.test.MockedCloudComponents
import org.junit.Test
import retrofit.Call
import retrofit.http.HEAD
import kotlin.test.expect

class RetrofitTest {

    interface FakeApi {
        @HEAD("foo") fun head(): Call<ResponseBody>
    }

    @Test fun dateFormat() = MockedCloudComponents.build {
        enqueue(MockResponse().setResponseCode(204))
    }.withRunningServer {
        retrofit.create(FakeApi::class.java).head().execute()

        with(mockWebServer.takeRequest()) {
            val url = baseUrl.resolve(path)
            expect("millis") { url.queryParameter("date_format") }
        }
    }

}

package com.github.jorgecastillo.kotlinandroid.io.runtime.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.jorgecastillo.kotlinandroid.R
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsApiService
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.NewsAuthInterceptor
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.RuntimeContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.jorgecastillo.hiroaki.Method
import me.jorgecastillo.hiroaki.internal.AndroidMockServerRule
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import me.jorgecastillo.hiroaki.retrofitService
import me.jorgecastillo.hiroaki.whenever
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AndroidNewsListActivityTest {

    @get:Rule val rule: AndroidMockServerRule = AndroidMockServerRule()

    @Before
    fun setup() {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(NewsAuthInterceptor("StubApiKey"))
            .build()

        val service = rule.server.retrofitService(
            NewsApiService::class.java,
            MoshiConverterFactory.create(),
            httpClient
        )
        val runtimeContext = RuntimeContext(newsService = service)

        appContext().stubService = service
        appContext().mockContext = runtimeContext
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadsNewsOnResume() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(
                success(jsonBody = fileBody("GetNews.json")).throttleBody(
                    1024,
                    1,
                    TimeUnit.SECONDS
                )
            )

        ActivityScenario.launch(AndroidNewsListActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                Thread.sleep(100000)
                onView(withId(R.id.loader)).check(matches(isDisplayed()))
            }
        }
    }
}

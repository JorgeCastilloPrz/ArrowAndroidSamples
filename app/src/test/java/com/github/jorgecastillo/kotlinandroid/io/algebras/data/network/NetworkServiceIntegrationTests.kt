package com.github.jorgecastillo.kotlinandroid.io.algebras.data.network

import arrow.core.left
import arrow.core.right
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import com.github.jorgecastillo.kotlinandroid.io.algebras.business.model.NewsItem
import com.github.jorgecastillo.kotlinandroid.io.algebras.data.network.error.NetworkError
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.Runtime
import com.github.jorgecastillo.kotlinandroid.io.runtime.context.RuntimeContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import me.jorgecastillo.hiroaki.*
import me.jorgecastillo.hiroaki.internal.MockServerRule
import me.jorgecastillo.hiroaki.models.error
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import arrow.core.Either
import retrofit2.converter.moshi.MoshiConverterFactory

@ExperimentalCoroutinesApi
class NetworkServiceIntegrationTests {

    @get:Rule
    val rule: MockServerRule = MockServerRule()

    private lateinit var runtime: Runtime<ForIO> // let's fix F to IO in tests also.

    @Before
    fun setup() {
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(NewsAuthInterceptor("StubApiKey"))
            .build()

        val service = rule.server.retrofitService(
            NewsApiService::class.java,
            MoshiConverterFactory.create(),
            httpClient)

        val testDispatcher = TestCoroutineDispatcher()
        val runtimeContext = RuntimeContext(testDispatcher, testDispatcher, service)

        runtime = object : Runtime<ForIO>(IO.concurrent(), runtimeContext) {}
    }

    @Test
    fun sendsFetchNewsToExpectedPath() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        unsafe {
            runBlocking {
                runtime.loadNews()
            }
        }

        rule.server.verify("everything").called(
            times = once(),
            queryParams = params(
                "q" to "android",
                "apiKey" to "StubApiKey"),
            method = Method.GET)
    }

    @Test
    fun mapsSuccessfulNewsToDomain() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        unsafe {
            val news = runBlocking {
                runtime.loadNews()
            }

            news eq expectedNews()
        }
    }

    @Test
    fun mapsSuccessfulNewsItemDetailsToDomain() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        unsafe {
            val newsItem = runBlocking {
                runtime.loadNewsItemDetails(title = "Capital One's virtual credit cards could help you avoid fraud")
            }

            newsItem eq expectedNews()[1]
        }
    }

    @Test
    fun mapsAllUnhandledServerErrors() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(error(code = 500))

        unsafe {
            val res: Either<Throwable, List<NewsItem>> = runBlocking {
                val op = runtime.loadNews().attempt()
                op.map {
                    it.fold(
                        ifLeft = { error -> error.left() },
                        ifRight = { news -> news.right() }
                    )
                }
            }

            res eq NetworkError.ServerError.left()
        }
    }

    @Test
    fun maps401ToUnauthorizedErrors() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(error(code = 401))

        unsafe {
            val res: Either<Throwable, List<NewsItem>> = runBlocking {
                val op = runtime.loadNews().attempt()
                op.map {
                    it.fold(
                        ifLeft = { error -> error.left() },
                        ifRight = { news -> news.right() }
                    )
                }
            }

            res eq NetworkError.Unauthorized.left()
        }
    }

    @Test
    fun mapsNotFoundNewsItemToNotFoundError() {
        rule.server.whenever(Method.GET, "everything")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        unsafe {
            val res: Either<Throwable, NewsItem> = runBlocking {
                val op = runtime.loadNewsItemDetails("Some not present title").attempt()
                op.map {
                    it.fold(
                        ifLeft = { error -> error.left() },
                        ifRight = { news -> news.right() }
                    )
                }
            }

            res eq NetworkError.NotFound.left()
        }
    }

    private fun expectedNews(): List<NewsItem> = listOf(
        NewsItem(
            "Lifehacker.com",
            "Jacob Kleinman",
            "How to Get Android P's Screenshot Editing Tool on Any Android Phone",
            "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …",
            "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122",
            "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg",
            "2018-03-09T20:30:00Z",
            "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year and a bunch more content!"
        ),
        NewsItem(
            "Engadget",
            "Devindra Hardawar",
            "Capital One's virtual credit cards could help you avoid fraud",
            "Capital One is no stranger to trying new things -- especially when it comes to technology. Its Eno texting chatbot, for example, is a quick and conversational way for its customers to check their balances and perform simple tasks, like checking on recent tran…",
            "https://www.engadget.com/2018/03/09/capital-one-virtual-credit-cards/",
            "https://o.aolcdn.com/images/dims?thumbnail=1200%2C630&quality=80&image_uri=https%3A%2F%2Fo.aolcdn.com%2Fimages%2Fdims%3Fcrop%3D3861%252C2574%252C0%252C0%26quality%3D85%26format%3Djpg%26resize%3D1600%252C1067%26image_uri%3Dhttp%253A%252F%252Fo.aolcdn.com%252Fhss%252Fstorage%252Fmidas%252Fca9d74d8d57677d4f3291ae4347b26da%252F206197048%252Fcapital-one-financial-corp-signage-is-displayed-at-a-bank-branch-in-picture-id120933061%26client%3Da1acac3e1b3290917d92%26signature%3Db51d9958f8487452f6a8c6d354650fcb339f0520&client=cbc79c14efcebee57402&signature=44e59feb505c1b9c4e5867d453d3ed345010acac",
            "2018-03-09T13:00:00Z",
            "Capital One is no stranger to trying new things -- especially when it comes to technology. Its Eno texting chatbot, for example, is a quick and conversational way for its customers to check their balances and perform simple tasks, like checking on recent and a bunch more content!"
        ),
        NewsItem(
            "TechCrunch",
            "Alex Wilhelm",
            "Magic Leap gets $461M more, Travis goes VC, and HQ Trivia scales up",
            "Hello and welcome back to Equity, TechCrunch’s venture capital-focused podcast where we unpack the numbers behind the headlines. This week we had a corking set of news to get through, so we rounded up the usual gang (Matthew Lynley, Katie Roof, and myself), a…",
            "http://techcrunch.com/2018/03/09/magic-leap-gets-461m-more-travis-goes-vc-and-hq-trivia-scales-up/",
            "https://tctechcrunch2011.files.wordpress.com/2017/03/tc-equity-podcast-ios.jpg",
            "2018-03-09T14:10:01Z",
            "Hello and welcome back to Equity, TechCrunch’s venture capital-focused podcast where we unpack the numbers behind the headlines. This week we had a corking set of news to get through, so we rounded up the usual gang (Matthew Lynley, Katie Roof, and myself), and a bunch more content!"
        )
    )
}
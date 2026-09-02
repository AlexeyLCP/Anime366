package su.afk.yummy.tv.core.network.yani

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import su.afk.yummy.tv.core.network.BuildConfig
import su.afk.yummy.tv.core.preferences.auth.YaniAuthPreferences
import su.afk.yummy.tv.core.preferences.settings.YaniAccountSettingsStore

fun buildYaniHttpClient(
    settingsStore: YaniAccountSettingsStore,
    yaniAuthPreferences: YaniAuthPreferences,
    okHttpClient: OkHttpClient,
    scope: CoroutineScope,
): HttpClient {
    val headerCache = YaniRequestHeaderCache(settingsStore, yaniAuthPreferences, scope)

    return HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 20_000
            requestTimeoutMillis = 40_000
            socketTimeoutMillis = 40_000
        }
        install(HttpRequestRetry) {
            maxRetries = 2
            retryIf { request, response ->
                request.method == HttpMethod.Get && response.status.value in 500..599
            }
            retryOnExceptionIf { request, cause ->
                request.method == HttpMethod.Get && cause !is kotlinx.coroutines.CancellationException
            }
            delayMillis { retry -> 500L * retry }
        }
        install(ContentNegotiation) {
            json(YaniApiJson)
        }
        install(ContentEncoding) {
            gzip()
            deflate()
        }
        install(createClientPlugin("Anime365AccessToken") {
            onRequest { request, _ ->
                if (request.url.host !in ANIME365_HOSTS) return@onRequest
                val headers = headerCache.current()
                val mirror = normalizeAnime365Host(headers.applicationToken)
                if (request.url.host != mirror) request.url.host = mirror
                request.headers.remove(HttpHeaders.UserAgent)
                request.headers.append(HttpHeaders.UserAgent, ANIME365_USER_AGENT)
                if (headers.refreshToken.isNotBlank() &&
                    request.url.parameters["access_token"].isNullOrBlank()
                ) {
                    request.url.parameters.append("access_token", headers.refreshToken)
                }
            }
        })
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
                sanitizeHeader { header ->
                    header.equals(HttpHeaders.Authorization, ignoreCase = true) ||
                        header.equals(HttpHeaders.Cookie, ignoreCase = true) ||
                        header.equals(HttpHeaders.SetCookie, ignoreCase = true)
                }
            }
        }
    }
}

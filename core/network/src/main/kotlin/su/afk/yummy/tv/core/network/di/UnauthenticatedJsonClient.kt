package su.afk.yummy.tv.core.network.di

import javax.inject.Qualifier

/**
 * Ktor-клиент для публичных сторонних API (GitHub и т.п.): умеет JSON, но не несёт
 * ни токена приложения, ни сессии пользователя — в отличие от `YaniHttpClientProvider`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnauthenticatedJsonClient

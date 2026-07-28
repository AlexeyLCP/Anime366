package su.afk.yummy.tv.core.error

import su.afk.yummy.tv.core.model.ErrorItem

/** Converts thrown errors into user-facing error items and optionally opens the error screen. */
interface IErrorHandlerUseCase {
    fun parse(
        t: Throwable,
        navigate: Boolean = false,
        retryKey: String? = null,
        owner: String? = null,
    ): ErrorItem
}

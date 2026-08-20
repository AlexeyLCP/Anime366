package su.afk.yummy.tv.core.error.api

import su.afk.yummy.tv.core.model.ErrorItem

/**
 * Приводит исключение к пользовательскому [ErrorItem] и по запросу открывает экран ошибки.
 *
 * Не use case: у него нет одной доменной операции — это сквозной сервис слоя представления,
 * который вызывается из каждой ViewModel.
 */
interface ErrorHandler {
    fun parse(
        t: Throwable,
        navigate: Boolean = false,
        retryKey: String? = null,
        owner: String? = null,
    ): ErrorItem
}

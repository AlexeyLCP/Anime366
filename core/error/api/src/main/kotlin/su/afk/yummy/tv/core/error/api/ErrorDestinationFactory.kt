package su.afk.yummy.tv.core.error.api

import androidx.navigation3.runtime.NavKey
import su.afk.yummy.tv.core.model.ErrorItem

/**
 * Порт для перехода на экран ошибки: [ErrorHandler] умеет распарсить [ErrorItem], но не знает,
 * какая фича этот экран показывает. Реализация живёт в фиче, владеющей экраном ошибки.
 */
fun interface ErrorDestinationFactory {
    operator fun invoke(error: ErrorItem): NavKey
}
